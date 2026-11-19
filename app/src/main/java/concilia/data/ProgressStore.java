package concilia.data;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 * Streak / XP / completed councils, saved in SharedPreferences so progress survives app restarts.
 * (In the Figma these lived in React state; the starting values 3 and 140 are the Figma demo
 * values - change DEFAULT_STREAK / DEFAULT_XP to 0 for a brand-new user.)
 */
public final class ProgressStore {
    public static final int DEFAULT_STREAK = 3;
    public static final int DEFAULT_XP = 140;

    private static final String PREFS = "concilia_progress";
    private static final String K_XP = "xp";
    private static final String K_STREAK = "streak";
    private static final String K_DONE = "completed";

    private static ProgressStore instance;

    public static synchronized ProgressStore get(Context ctx) {
        if (instance == null) instance = new ProgressStore(ctx.getApplicationContext());
        return instance;
    }

    private final SharedPreferences prefs;

    private ProgressStore(Context appCtx) {
        prefs = appCtx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public int getXp() { return prefs.getInt(K_XP, DEFAULT_XP); }

    public int getStreak() { return prefs.getInt(K_STREAK, DEFAULT_STREAK); }

    public Set<Integer> getCompletedIds() {
        Set<String> raw = prefs.getStringSet(K_DONE, new HashSet<String>());
        Set<Integer> out = new HashSet<>();
        for (String s : raw) {
            try { out.add(Integer.parseInt(s)); } catch (NumberFormatException ignored) { }
        }
        return out;
    }

    public boolean isCompleted(int councilId) { return getCompletedIds().contains(councilId); }

    public int getCompletedCount() { return getCompletedIds().size(); }

    /**
     * Same rule as the Figma: council 1 is open; every other council unlocks once the previous
     * one is completed (or it was already completed itself).
     */
    public boolean isLocked(int index) {
        if (index == 0) return false;
        Set<Integer> done = getCompletedIds();
        int prevId = CouncilsData.COUNCILS.get(index - 1).id;
        int thisId = CouncilsData.COUNCILS.get(index).id;
        return !done.contains(prevId) && !done.contains(thisId);
    }

    public void completeCouncil(int councilId, int xpGained) {
        Set<String> raw = new HashSet<>(prefs.getStringSet(K_DONE, new HashSet<String>()));
        raw.add(String.valueOf(councilId));
        prefs.edit()
                .putStringSet(K_DONE, raw)
                .putInt(K_XP, getXp() + xpGained)
                .apply();
    }

    public void resetAll() { prefs.edit().clear().apply(); }
    // ── per-lesson progress ──
    private static final String K_LESSONS = "completed_lessons";

    public Set<String> getCompletedLessons() {
        return new HashSet<>(prefs.getStringSet(K_LESSONS, new HashSet<String>()));
    }

    public boolean isLessonCompleted(int councilId, int lessonId) {
        return getCompletedLessons().contains(councilId + "-" + lessonId);
    }

    public void completeLesson(int councilId, int lessonId, int xpGained) {
        Set<String> set = new HashSet<>(getCompletedLessons());
        set.add(councilId + "-" + lessonId);
        prefs.edit()
                .putStringSet(K_LESSONS, set)
                .putInt(K_XP, getXp() + xpGained)
                .apply();
    }

    /** Vrne true, če so vse lekcije do maxLessonId končane. */
    public boolean areAllLessonsCompleted(int councilId, int maxLessonId) {
        for (int i = 1; i <= maxLessonId; i++) {
            if (!isLessonCompleted(councilId, i)) return false;
        }
        return true;
    }

    /** Vrne prvo še neodklenjeno lessonId za ta koncil (1-based). Če so vse končane, vrne -1. */
    public int nextLessonId(int councilId, int maxLessonId) {
        for (int i = 1; i <= maxLessonId; i++) {
            if (!isLessonCompleted(councilId, i)) return i;
        }
        return -1;
    }
}

