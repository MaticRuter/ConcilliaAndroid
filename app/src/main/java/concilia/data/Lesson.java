package concilia.data;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class Lesson {
    public enum Type { INFO, QUIZ, MATCH }

    public final String id;
    public final Type type;
    public final String title;
    public final String content;       // INFO only
    public final String question;      // QUIZ only
    public final List<String> options; // QUIZ only
    public final int correct;          // QUIZ only (index into options)
    public final String explanation;   // QUIZ only

    private Lesson(String id, Type type, String title, String content, String question,
                   List<String> options, int correct, String explanation) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.content = content;
        this.question = question;
        this.options = options;
        this.correct = correct;
        this.explanation = explanation;
    }

    public static Lesson info(String id, String title, String content) {
        return new Lesson(id, Type.INFO, title, content, null,
                Collections.<String>emptyList(), -1, null);
    }

    public static Lesson quiz(String id, String title, String question, String[] options,
                              int correct, String explanation) {
        return new Lesson(id, Type.QUIZ, title, null, question,
                Collections.unmodifiableList(Arrays.asList(options)), correct, explanation);
    }
}
