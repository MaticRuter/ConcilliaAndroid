package concilia.data;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads per-council lesson content from assets/councils/council_N.json.
 * Maps the richer JSON structure (multiple questions per lesson, several question types)
 * into the existing Lesson INFO / QUIZ model so QuizActivity and LessonActivity keep working
 * without changes to their core flow.
 *
 * Supported question types from JSON: multiple_choice, true_false.
 * fill_blank and matching are skipped for now (can be added later with UI support).
 */
public final class CouncilJsonLoader {

    private static final String TAG = "CouncilJsonLoader";
    private static final String ASSET_DIR = "councils";

    private CouncilJsonLoader() {}

    /**
     * Returns lessons loaded from JSON for the given council id, or null if no file / parse error.
     */
    public static List<Lesson> loadLessons(Context context, int councilId) {
        String path = ASSET_DIR + "/council_" + councilId + ".json";
        try {
            String json = readAsset(context, path);
            if (json == null) return null;
            return parseLessons(json, councilId);
        } catch (Exception e) {
            Log.w(TAG, "Failed to load " + path + ": " + e.getMessage());
            return null;
        }
    }

    private static String readAsset(Context context, String path) {
        try (InputStream is = context.getAssets().open(path);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    private static List<Lesson> parseLessons(String json, int councilId) throws Exception {
        JSONObject root = new JSONObject(json);
        JSONArray lessonsArr = root.optJSONArray("lessons");
        if (lessonsArr == null || lessonsArr.length() == 0) return null;

        List<Lesson> out = new ArrayList<>();
        for (int i = 0; i < lessonsArr.length(); i++) {
            JSONObject les = lessonsArr.getJSONObject(i);
            int lessonId = les.optInt("lessonId", i + 1);
            String title = les.optString("title", "Lesson " + lessonId);
            String type = les.optString("type", "intro");

            // INFO kartica = ena lekcija na overviewju
            String infoContent = buildInfoContent(title, type, root);
            out.add(Lesson.info(
                    councilId + "-" + lessonId + "-info",
                    title,
                    infoContent
            ));

            JSONArray questions = les.optJSONArray("questions");
            if (questions == null) continue;

            for (int q = 0; q < questions.length(); q++) {
                JSONObject qu = questions.getJSONObject(q);
                Lesson quiz = mapQuestion(councilId, lessonId, q, title, qu);
                if (quiz != null) {
                    out.add(quiz);
                }
            }
        }
        return out.isEmpty() ? null : out;
    }

    private static String buildInfoContent(String title, String type, JSONObject root) {
        StringBuilder sb = new StringBuilder();
        sb.append(title);
        if (type != null && !type.isEmpty()) {
            sb.append("\n\n(").append(type).append(")");
        }
        String main = root.optString("mainConcept", "");
        if (!main.isEmpty()) {
            sb.append("\n\nKey idea: ").append(main);
        }
        String desc = root.optString("description", "");
        if (!desc.isEmpty() && sb.length() < 80) {
            sb.append("\n\n").append(desc);
        }
        return sb.toString();
    }


    private static Lesson mapQuestion(int councilId, int lessonId, int qIndex,
                                      String lessonTitle, JSONObject qu) {
        String qType = qu.optString("type", "");
        String questionText = qu.optString("questionText", "");
        if (questionText.isEmpty()) return null;

        String id = councilId + "-" + lessonId + "-q" + (qIndex + 1);

        if ("multiple_choice".equals(qType) || "true_false".equals(qType)) {
            JSONArray answers = qu.optJSONArray("answers");
            if (answers == null || answers.length() == 0) return null;

            List<String> options = new ArrayList<>();
            int correct = -1;
            for (int a = 0; a < answers.length(); a++) {
                JSONObject ans = answers.optJSONObject(a);
                if (ans == null) continue;
                String text = ans.optString("text", "");
                if (text.isEmpty()) continue;
                options.add(text);
                if (ans.optBoolean("isCorrect", false) && correct < 0) {
                    correct = options.size() - 1;
                }
            }
            if (options.size() < 2 || correct < 0) return null;   // mora imeti vsaj 2 možnosti

            String[] opts = options.toArray(new String[0]);
            return Lesson.quiz(id, lessonTitle, questionText, opts, correct, "");
        }

        // fill_blank in matching zaenkrat preskočimo (kasneje jih lahko dodamo)
        return null;
    }
}
