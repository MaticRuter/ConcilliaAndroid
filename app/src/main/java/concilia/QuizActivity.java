package concilia;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import concilia.data.CouncilsData;
import concilia.data.Council;
import concilia.data.Lesson;
import concilia.data.ProgressStore;
import concilia.ui.Fonts;
import concilia.ui.Palette;
import concilia.ui.Ui;
import concilia.ui.views.BackButtonView;
import concilia.ui.views.BubbleTailView;
import concilia.ui.views.CouncilCharacterView;
import concilia.ui.views.ProgressBarView;

import java.util.Locale;
import java.util.Random;
import android.util.Log;

/**
 * Lesson / quiz flow with the talking bishop (QuizScreen.tsx):
 * reading card -> question -> correct / wrong feedback -> next lesson -> "Council Complete!".
 */
public class QuizActivity extends Activity {

    private static final int READING = 0, QUESTION = 1, CORRECT = 2, WRONG = 3, COMPLETE = 4;

    private static final String[] CORRECT_LINES = {
            "Excellent! You honor the Fathers.",
            "Well said! The Council is pleased.",
            "Correct! God guides your tongue.",
            "Indeed! Spoken like a true theologian.",
    };
    private static final String[] WRONG_LINES = {
            "Hmm… study the canons more carefully.",
            "Not so. The heretics said that too.",
            "That's not what we defined. Try again.",
            "The Council would beg to differ…",
    };

    private static final String HEART = "❤️";

    private java.util.List<Lesson> lessons;
    private int targetLessonId = -1;


    // ── state (all saved across rotation) ──
    private int lessonIdx = 0, phase = READING, selected = -1, hearts = 3, xpGained = 0;
    private int mood = CouncilCharacterView.NEUTRAL;
    private String reaction = "";

    private Council co;
    private int era;
    private String charName, charTitle;
    private final Random rnd = new Random();

    // ── views ──
    private Context c;
    private LinearLayout header, content;
    private ProgressBarView progress;
    private TextView[] headerHearts = new TextView[3];
    private ScrollView scroll;
    private TextView burst;
    private CouncilCharacterView character;
    private LinearLayout optionsBox;
    private boolean shakePending;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        c = this;
        co = CouncilsData.byId(getIntent().getIntExtra(LessonActivity.EXTRA_COUNCIL_ID, 1));
        if (co == null) { finish(); return; }

        java.util.List<Lesson> loaded = CouncilsData.lessonsFor(this, co);
        if (loaded == null || loaded.isEmpty()) { finish(); return; }

        // Popolnoma nova lista + novi Lesson objekti
        lessons = new java.util.ArrayList<>();
        for (Lesson src : loaded) {
            if (src.type == Lesson.Type.INFO) {
                lessons.add(Lesson.info(src.id, src.title, src.content));
            } else if (src.type == Lesson.Type.QUIZ) {
                String[] opts = src.options.toArray(new String[0]);
                lessons.add(Lesson.quiz(src.id, src.title, src.question, opts, src.correct,
                        src.explanation != null ? src.explanation : ""));
            }
        }
        targetLessonId = getIntent().getIntExtra("lesson_id", -1);

        // Če je izbrana konkretna lekcija, filtriraj samo njene elemente
        if (targetLessonId > 0) {
            java.util.List<Lesson> filtered = new java.util.ArrayList<>();
            boolean inTarget = false;
            for (Lesson l : lessons) {
                if (l.type == Lesson.Type.INFO) {
                    try {
                        int id = Integer.parseInt(l.id.split("-")[1]);
                        inTarget = (id == targetLessonId);
                    } catch (Exception e) {
                        inTarget = false;
                    }
                }
                if (inTarget) filtered.add(l);
            }
            if (!filtered.isEmpty()) {
                lessons = filtered;
            }
        }

        Log.e("QuizDebug", "=== QUIZ START – total lessons: " + lessons.size() + " ===");
        for (int i = 0; i < Math.min(10, lessons.size()); i++) {
            Lesson l = lessons.get(i);
            Log.e("QuizDebug", "  [" + i + "] idHash=" + System.identityHashCode(l)
                    + " " + l.type + " | "
                    + (l.question != null ? l.question.substring(0, Math.min(45, l.question.length())) : l.title));
        }

        era = co.eraColor;
        pickCharacterName();

        if (b != null) {
            lessonIdx = b.getInt("lessonIdx");
            phase = b.getInt("phase");
            selected = b.getInt("selected", -1);
            hearts = b.getInt("hearts", 3);
            xpGained = b.getInt("xpGained");
            mood = b.getInt("mood");
            reaction = b.getString("reaction", "");
        }

        // ... od tukaj naprej pusti ostalo kodo (FrameLayout root = ... itd.)

        FrameLayout root = new FrameLayout(c);
        root.setBackgroundColor(Palette.BACKGROUND);
        Ui.applyInsets(root);

        LinearLayout column = new LinearLayout(c);
        column.setOrientation(LinearLayout.VERTICAL);

        // ── header: back, progress bar, hearts ──
        header = new LinearLayout(c);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(Ui.dp(c, 16), Ui.dp(c, 8), Ui.dp(c, 16), Ui.dp(c, 12));
        BackButtonView back = new BackButtonView(c, 12, 2f);
        back.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { finish(); }
        });
        header.addView(back);
        progress = new ProgressBarView(c, era).setTrackColor(Palette.CARD).setMin(0.04f).setGlow(true);
        header.addView(progress, Ui.lpMargins(c, 0, 12, 12, 0, 12, 0));
        ((LinearLayout.LayoutParams) progress.getLayoutParams()).weight = 1f;
        LinearLayout hh = new LinearLayout(c);
        for (int i = 0; i < 3; i++) {
            headerHearts[i] = Ui.text(c, HEART, 14, Palette.FOREGROUND, null);
            hh.addView(headerHearts[i], Ui.lpMargins(c, -2, -2, i == 0 ? 0 : 2, 0, 0, 0));
        }
        header.addView(hh);
        column.addView(header, Ui.lp(-1, -2));

        scroll = new ScrollView(c);
        scroll.setFillViewport(true);
        scroll.setVerticalScrollBarEnabled(false);
        content = new LinearLayout(c);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setClipChildren(false);
        scroll.addView(content, new FrameLayout.LayoutParams(-1, -2));
        column.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1f));
        root.addView(column, new FrameLayout.LayoutParams(-1, -1));

        // floating "+10 XP ✦"
        burst = Ui.text(c, "+10 XP ✦", 12, Palette.BACKGROUND, Fonts.inter(c, 700));
        burst.setPadding(Ui.dp(c, 12), Ui.dp(c, 6), Ui.dp(c, 12), Ui.dp(c, 6));
        burst.setBackground(Ui.box(c, Palette.GOLD, 0, 0, 999));
        burst.setVisibility(View.GONE);
        FrameLayout.LayoutParams blp = new FrameLayout.LayoutParams(-2, -2, Gravity.TOP | Gravity.END);
        blp.topMargin = Ui.dp(c, 48);
        blp.rightMargin = Ui.dp(c, 20);
        root.addView(burst, blp);

        character = new CouncilCharacterView(c, Ui.dp(c, 110));
        character.setEra(co.era);

        setContentView(root);
        progress.setProgress(lessonIdx / (float) lessons.size());
        render();
    }

    @Override protected void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        out.putInt("lessonIdx", lessonIdx);
        out.putInt("phase", phase);
        out.putInt("selected", selected);
        out.putInt("hearts", hearts);
        out.putInt("xpGained", xpGained);
        out.putInt("mood", mood);
        out.putString("reaction", reaction);
    }

    // ── which bishop talks, per era (CHARACTER_NAMES) ──
    private void pickCharacterName() {
        switch (co.era) {
            case "Byzantine Age":   charName = "Patriarch Photius"; charTitle = "Archbishop of Constantinople"; break;
            case "Medieval Church": charName = "Innocent III";      charTitle = "Pontifex Maximus"; break;
            case "Late Medieval":   charName = "Clement V";         charTitle = "Bishop of Rome"; break;
            case "Reformation Era": charName = "Paul III";          charTitle = "Pontifex Maximus"; break;
            case "Modern Church":   charName = "Pius IX";           charTitle = "Bishop of Rome"; break;
            default:                charName = "Athanasius";        charTitle = "Bishop of Alexandria"; break;
        }
    }

    // ── interaction ──
    private Lesson lesson() {
        return lessons.get(Math.min(lessonIdx, lessons.size() - 1));
    }

    private boolean isLast() { return lessonIdx == lessons.size() - 1; }

    private void onSelect(int idx) {
        if (phase != QUESTION) return;
        selected = idx;
        Log.e("QuizDebug", "onSelect idx=" + idx + " correct=" + lesson().correct + " lessonIdx=" + lessonIdx);
        if (idx == lesson().correct) {
            phase = CORRECT;
            mood = CouncilCharacterView.HAPPY;
            xpGained += 10;
            reaction = CORRECT_LINES[rnd.nextInt(CORRECT_LINES.length)];
            render();
            showBurst();
        } else {
            phase = WRONG;
            mood = CouncilCharacterView.SAD;
            reaction = WRONG_LINES[rnd.nextInt(WRONG_LINES.length)];
            hearts = Math.max(0, hearts - 1);
            shakePending = true;
            render();
        }
    }

    private void onContinue() {
        Log.e("QuizDebug", "onContinue phase=" + phase + " lessonIdx=" + lessonIdx + " type=" + lesson().type + " isLast=" + isLast());
        if (phase == READING) {
            if (lesson().type == Lesson.Type.INFO) {
                advance();
            } else {
                phase = QUESTION;
                mood = CouncilCharacterView.THINKING;
                render();
            }
        } else if (phase == CORRECT || phase == WRONG) {
            advance();
        }
    }

    private void advance() {
        Log.e("QuizDebug", "advance() BEFORE lessonIdx=" + lessonIdx
                + " listHash=" + System.identityHashCode(lessons)
                + " size=" + lessons.size()
                + " get2=" + System.identityHashCode(lessons.get(Math.min(2, lessons.size()-1)))
                + " get3=" + System.identityHashCode(lessons.get(Math.min(3, lessons.size()-1)))
                + " current=" + System.identityHashCode(lesson()));

        if (isLast()) {
            phase = COMPLETE;
            Log.e("QuizDebug", "→ COMPLETE");
        } else {
            lessonIdx++;
            phase = READING;
            selected = -1;
            mood = CouncilCharacterView.NEUTRAL;

            Log.e("QuizDebug", "→ AFTER lessonIdx=" + lessonIdx
                    + " listHash=" + System.identityHashCode(lessons)
                    + " get2=" + System.identityHashCode(lessons.get(Math.min(2, lessons.size()-1)))
                    + " get3=" + System.identityHashCode(lessons.get(Math.min(3, lessons.size()-1)))
                    + " next=" + System.identityHashCode(lesson())
                    + " Q=" + (lesson().question != null ? lesson().question.substring(0, Math.min(40, lesson().question.length())) : lesson().title));
        }
        render();
        scroll.post(new Runnable() { @Override public void run() { scroll.scrollTo(0, 0); } });
    }

    private void finishCouncil() {
        if (targetLessonId > 0) {
            // Končali smo samo eno lekcijo → shrani progress in pojdi nazaj na LessonActivity
            ProgressStore.get(c).completeLesson(co.id, targetLessonId, xpGained);
            finish();   // samo zapri QuizActivity, LessonActivity je še v back stacku
        } else {
            // Stari način (cel koncil)
            ProgressStore.get(c).completeCouncil(co.id, xpGained);
            Intent i = new Intent(this, TimelineActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
            finish();
        }
    }
    // ── effects ──
    private void showBurst() {
        burst.setVisibility(View.VISIBLE);
        float dp = Ui.density(c);
        PropertyValuesHolder a = PropertyValuesHolder.ofKeyframe("alpha",
                Keyframe.ofFloat(0f, 1f), Keyframe.ofFloat(0.6f, 1f), Keyframe.ofFloat(1f, 0f));
        PropertyValuesHolder ty = PropertyValuesHolder.ofKeyframe("translationY",
                Keyframe.ofFloat(0f, 0f), Keyframe.ofFloat(0.6f, -22f * dp), Keyframe.ofFloat(1f, -44f * dp));
        PropertyValuesHolder sx = PropertyValuesHolder.ofKeyframe("scaleX",
                Keyframe.ofFloat(0f, 1f), Keyframe.ofFloat(0.6f, 1.1f), Keyframe.ofFloat(1f, 0.9f));
        PropertyValuesHolder sy = PropertyValuesHolder.ofKeyframe("scaleY",
                Keyframe.ofFloat(0f, 1f), Keyframe.ofFloat(0.6f, 1.1f), Keyframe.ofFloat(1f, 0.9f));
        ObjectAnimator an = ObjectAnimator.ofPropertyValuesHolder(burst, a, ty, sx, sy);
        an.setDuration(1400);
        an.setInterpolator(new DecelerateInterpolator());
        an.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(android.animation.Animator animation) {
                burst.setVisibility(View.GONE);
            }
        });
        an.start();
    }

    private void shake(View v) {
        float dp = Ui.density(c);
        PropertyValuesHolder x = PropertyValuesHolder.ofKeyframe("translationX",
                Keyframe.ofFloat(0f, 0f), Keyframe.ofFloat(0.2f, -7f * dp), Keyframe.ofFloat(0.4f, 7f * dp),
                Keyframe.ofFloat(0.6f, -4f * dp), Keyframe.ofFloat(0.8f, 4f * dp), Keyframe.ofFloat(1f, 0f));
        ObjectAnimator an = ObjectAnimator.ofPropertyValuesHolder(v, x);
        an.setDuration(400);
        an.start();
    }

    // ── rendering ──
    private void render() {
        boolean complete = phase == COMPLETE;
        header.setVisibility(complete ? View.GONE : View.VISIBLE);
        progress.animateTo(lessonIdx / (float) lessons.size());
        for (int i = 0; i < 3; i++) headerHearts[i].setAlpha(i < hearts ? 1f : 0.18f);

        content.removeAllViews();
        optionsBox = null;
        content.setGravity(complete ? Gravity.CENTER_VERTICAL : Gravity.TOP);

        if (complete) {
            buildComplete();
            return;
        }

        Lesson l = lesson();
        boolean answered = phase == CORRECT || phase == WRONG;
        content.addView(buildStage(l, answered), Ui.lp(-1, -2));

        LinearLayout area = new LinearLayout(c);
        area.setOrientation(LinearLayout.VERTICAL);
        area.setPadding(Ui.dp(c, 20), 0, Ui.dp(c, 20), Ui.dp(c, 28));
        area.setClipChildren(false);

        if (phase == READING) {
            LinearLayout card = new LinearLayout(c);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(Ui.dp(c, 16), Ui.dp(c, 16), Ui.dp(c, 16), Ui.dp(c, 16));
            card.setBackground(Ui.box(c, Palette.CARD, Palette.BORDER, 1, 16));
            TextView t = Ui.text(c, l.title.toUpperCase(Locale.ROOT), 12, era, Fonts.inter(c, 700));
            Ui.tracking(t, 0.1f);
            card.addView(t, Ui.lpMargins(c, -2, -2, 0, 0, 0, 12));
            TextView body;
            if (l.type == Lesson.Type.INFO) {
                body = Ui.text(c, l.content, 14, Palette.SOFT_TEXT, Fonts.fell(c, false));
                Ui.lineHeight(body, 1.8f);
            } else {
                body = Ui.text(c, l.question, 15, Palette.FOREGROUND, Fonts.playfair(c, 400));
                Ui.lineHeight(body, 1.6f);
            }
            card.addView(body);
            area.addView(card, Ui.lpMargins(c, -1, 0, 0, 0, 0, 16));
            ((LinearLayout.LayoutParams) card.getLayoutParams()).weight = 1f;

            TextView btn = Ui.glowButton(c, l.type == Lesson.Type.INFO ? "Understood →" : "Answer →",
                    era, Palette.FOREGROUND, 16, Fonts.inter(c, 700), 16, 0f, Ui.alpha(era, 0x66));
            btn.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) { onContinue(); }
            });
            area.addView(btn, Ui.lp(-1, -2));
        } else if (l.type == Lesson.Type.QUIZ) {
            // question box
            LinearLayout q = new LinearLayout(c);
            q.setOrientation(LinearLayout.VERTICAL);
            q.setPadding(Ui.dp(c, 16), Ui.dp(c, 16), Ui.dp(c, 16), Ui.dp(c, 16));
            q.setBackground(Ui.box(c, Palette.CARD_DARK, Palette.BORDER, 1, 16));
            TextView ql = Ui.text(c, "CHOOSE THE CORRECT ANSWER", 12, Palette.MUTED, Fonts.inter(c, 700));
            Ui.tracking(ql, 0.1f);
            q.addView(ql, Ui.lpMargins(c, -2, -2, 0, 0, 0, 8));
            TextView qt = Ui.text(c, l.question, 14, Palette.FOREGROUND, Fonts.playfair(c, 600));
            Ui.lineHeight(qt, 1.375f);
            q.addView(qt);
            area.addView(q, Ui.lpMargins(c, -1, -2, 0, 0, 0, 16));

            // options
            optionsBox = new LinearLayout(c);
            optionsBox.setOrientation(LinearLayout.VERTICAL);
            for (int i = 0; i < l.options.size(); i++) {
                optionsBox.addView(buildOption(l, i, answered), Ui.lpMargins(c, -1, -2, 0, i == 0 ? 0 : 10, 0, 0));
            }
            area.addView(optionsBox, Ui.lpMargins(c, -1, -2, 0, 0, 0, 16));

            if (answered) {
                boolean ok = phase == CORRECT;
                TextView ex = Ui.text(c, l.explanation, 12, ok ? Palette.OK_SOFT : Palette.BAD_SOFT, Fonts.inter(c, 400));
                Ui.lineHeight(ex, 1.625f);
                ex.setPadding(Ui.dp(c, 14), Ui.dp(c, 14), Ui.dp(c, 14), Ui.dp(c, 14));
                ex.setBackground(Ui.box(c, ok ? Palette.OK_PANEL : Palette.BAD_PANEL,
                        ok ? Palette.OK_PANEL_BD : Palette.BAD_PANEL_BD, 1, 16));
                area.addView(ex, Ui.lpMargins(c, -1, -2, 0, 0, 0, 16));

                int btnColor = ok ? Palette.OK_BORDER : era;
                TextView btn = Ui.glowButton(c, isLast() ? "✦ Finish Lesson" : "Continue →", btnColor,
                        0xFFFFFFFF, 16, Fonts.inter(c, 700), 16, 0f, Ui.alpha(btnColor, 0x66));
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) { onContinue(); }
                });
                area.addView(btn, Ui.lp(-1, -2));
            }
        }
        content.addView(area, new LinearLayout.LayoutParams(-1, 0, 1f));

        applyCharacterPose();
        if (shakePending && optionsBox != null) {
            shakePending = false;
            shake(optionsBox);
        }
    }

    /** Character reacts: pops + tilts when right, shrinks slightly when wrong (0.4s overshoot). */
    private void applyCharacterPose() {
        float s = phase == CORRECT ? 1.06f : phase == WRONG ? 0.96f : 1f;
        float rot = phase == CORRECT ? -2f : phase == WRONG ? 1f : 0f;
        character.animate().scaleX(s).scaleY(s).rotation(rot).setDuration(400)
                .setInterpolator(new OvershootInterpolator()).start();
    }

    private View buildStage(Lesson l, boolean answered) {
        LinearLayout stage = new LinearLayout(c);
        stage.setOrientation(LinearLayout.VERTICAL);
        stage.setGravity(Gravity.CENTER_HORIZONTAL);
        stage.setPadding(Ui.dp(c, 20), Ui.dp(c, 8), Ui.dp(c, 20), Ui.dp(c, 8));
        stage.setClipChildren(false);
        stage.setBackground(Ui.vGradient(Ui.alpha(era, 0x18), Ui.alpha(era, 0x00)));

        TextView tag = Ui.text(c, charName + " · " + charTitle, 12, era, Fonts.inter(c, 600));
        Ui.tracking(tag, 0.025f);
        tag.setPadding(Ui.dp(c, 12), Ui.dp(c, 4), Ui.dp(c, 12), Ui.dp(c, 4));
        tag.setBackground(Ui.box(c, Ui.alpha(era, 0x22), Ui.alpha(era, 0x44), 1, 999));
        stage.addView(tag, Ui.lpMargins(c, -2, -2, 0, 0, 0, 4));

        LinearLayout row = new LinearLayout(c);
        row.setGravity(Gravity.BOTTOM);
        row.setClipChildren(false);

        character.setMood(mood);
        if (character.getParent() instanceof ViewGroup) ((ViewGroup) character.getParent()).removeView(character);
        row.addView(character);

        // speech bubble
        FrameLayout wrap = new FrameLayout(c);
        wrap.setClipChildren(false);
        int bg = answered ? (phase == CORRECT ? Palette.OK_BG : Palette.BAD_BG) : Palette.CARD;
        int bd = answered ? (phase == CORRECT ? Ui.alpha(Palette.OK_BORDER, 0x88) : Ui.alpha(Palette.BAD_BORDER, 0x66))
                : Palette.BORDER;

        TextView bubble;
        if (answered) {
            bubble = Ui.text(c, "\"" + reaction + "\"", 13, phase == CORRECT ? Palette.OK_SOFT : Palette.BAD_SOFT,
                    Fonts.fell(c, true));
        } else if (phase == READING) {
            bubble = Ui.text(c, l.type == Lesson.Type.INFO ? "Let me share some history with you…"
                    : "I have a question for you, scholar.", 13, Palette.SOFT_TEXT, Fonts.fell(c, false));
        } else {
            bubble = Ui.text(c, "Choose wisely — the faith depends on it.", 13, Palette.SOFT_TEXT, Fonts.fell(c, false));
        }
        Ui.lineHeight(bubble, 1.5f);
        bubble.setPadding(Ui.dp(c, 12), Ui.dp(c, 12), Ui.dp(c, 12), Ui.dp(c, 12));
        bubble.setMinHeight(Ui.dp(c, 64));
        bubble.setGravity(Gravity.CENTER_VERTICAL);
        float r = Ui.dp(c, 16), sm = Ui.dp(c, 4);
        GradientDrawable bgd = new GradientDrawable();
        bgd.setColor(bg);
        bgd.setStroke(Math.max(1, Ui.dp(c, 1)), bd);
        bgd.setCornerRadii(new float[]{r, r, r, r, r, r, sm, sm}); // bottom-left corner is squarer
        bubble.setBackground(bgd);
        wrap.addView(bubble, new FrameLayout.LayoutParams(-1, -2));

        BubbleTailView tail = new BubbleTailView(c);
        tail.setColor(bg);
        FrameLayout.LayoutParams tlp = new FrameLayout.LayoutParams(-2, -2, Gravity.BOTTOM | Gravity.START);
        tlp.leftMargin = -Ui.dp(c, 10);
        tlp.bottomMargin = Ui.dp(c, 20);
        wrap.addView(tail, tlp);

        row.addView(wrap, Ui.lpMargins(c, 0, -2, 12, 0, 0, 16));
        ((LinearLayout.LayoutParams) wrap.getLayoutParams()).weight = 1f;
        stage.addView(row, Ui.lp(-1, -2));
        return stage;
    }

    private View buildOption(Lesson l, final int idx, boolean answered) {
        int bg = Palette.CARD, border = Palette.BORDER, text = Palette.FOREGROUND;
        int badgeBg = Palette.BORDER, badgeFg = Palette.MUTED;
        String badge = String.valueOf((char) ('A' + idx));

        if (answered) {
            if (idx == l.correct) {
                bg = Palette.OK_BG; border = Palette.OK_BORDER; text = Palette.OK_TEXT;
                badgeBg = Palette.OK_BORDER; badgeFg = 0xFFFFFFFF; badge = "✓";
            } else if (idx == selected) {
                bg = Palette.BAD_BG; border = Palette.BAD_BORDER; text = Palette.BAD_TEXT;
                badgeBg = Palette.BAD_BORDER; badgeFg = 0xFFFFFFFF; badge = "✗";
            } else {
                text = Palette.DIM_TEXT; border = Palette.DIM_BORDER;
            }
        }

        LinearLayout row = new LinearLayout(c);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(Ui.dp(c, 14), Ui.dp(c, 14), Ui.dp(c, 14), Ui.dp(c, 14));
        row.setBackground(Ui.box(c, bg, border, 1, 16));

        TextView b = Ui.text(c, badge, 12, badgeFg, Fonts.inter(c, 700));
        b.setGravity(Gravity.CENTER);
        b.setBackground(Ui.box(c, badgeBg, 0, 0, 12));
        row.addView(b, Ui.lpMargins(c, 32, 32, 0, 0, 12, 0));

        TextView t = Ui.text(c, l.options.get(idx), 14, text, Fonts.inter(c, 400));
        Ui.lineHeight(t, 1.375f);
        row.addView(t, new LinearLayout.LayoutParams(0, -2, 1f));

        if (!answered) {
            row.setClickable(true);
            Ui.pressScale(row, 0.98f);
            row.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) { onSelect(idx); }
            });
        }
        return row;
    }

    private void buildComplete() {
        LinearLayout col = new LinearLayout(c);
        col.setOrientation(LinearLayout.VERTICAL);
        col.setGravity(Gravity.CENTER_HORIZONTAL);
        col.setPadding(Ui.dp(c, 24), Ui.dp(c, 20), Ui.dp(c, 24), Ui.dp(c, 40));

        CouncilCharacterView big = new CouncilCharacterView(c, Ui.dp(c, 140));
        big.setEra(co.era);
        big.setMood(CouncilCharacterView.HAPPY);
        col.addView(big, Ui.lpMargins(c, -2, -2, 0, 0, 0, 8));

        TextView trophy = Ui.text(c, "🏆", 36, Palette.FOREGROUND, null);
        col.addView(trophy, Ui.lpMargins(c, -2, -2, 0, -12, 0, 12));

        TextView h = Ui.text(c, "Council Complete!", 24, Palette.FOREGROUND, Fonts.playfair(c, 700));
        col.addView(h, Ui.lpMargins(c, -2, -2, 0, 0, 0, 4));
        TextView sub = Ui.text(c, charName + " is proud of your progress.", 12, Palette.MUTED, Fonts.inter(c, 400));
        col.addView(sub, Ui.lpMargins(c, -2, -2, 0, 0, 0, 24));

        LinearLayout card = new LinearLayout(c);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(Ui.dp(c, 20), Ui.dp(c, 20), Ui.dp(c, 20), Ui.dp(c, 20));
        card.setBackground(Ui.box(c, Palette.CARD, Ui.alpha(era, 0x55), 1, 16));

        card.addView(statRow("XP Earned", Ui.text(c, "+" + xpGained + " XP", 20, Palette.GOLD, Fonts.inter(c, 700))),
                Ui.lpMargins(c, -1, -2, 0, 0, 0, 12));

        LinearLayout hs = new LinearLayout(c);
        for (int i = 0; i < 3; i++) {
            TextView hv = Ui.text(c, HEART, 16, Palette.FOREGROUND, null);
            hv.setAlpha(i < hearts ? 1f : 0.2f);
            hs.addView(hv, Ui.lpMargins(c, -2, -2, i == 0 ? 0 : 4, 0, 0, 0));
        }
        card.addView(statRow("Hearts", hs), Ui.lpMargins(c, -1, -2, 0, 0, 0, 12));

        int n = lessons.size();
        card.addView(statRow("Lessons", Ui.text(c, n + "/" + n, 14, era, Fonts.inter(c, 700))), Ui.lp(-1, -2));
        col.addView(card, Ui.lpMargins(c, -1, -2, 0, 0, 0, 20));

        TextView go = Ui.glowButton(c, "Continue →", Palette.GOLD, Palette.BACKGROUND, 16,
                Fonts.inter(c, 700), 16, 0f, Ui.alpha(Palette.GOLD, 0x66));
        go.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { finishCouncil(); }
        });
        col.addView(go, Ui.lp(-1, -2));
        content.addView(col, Ui.lp(-1, -2));
    }

    private View statRow(String label, View right) {
        LinearLayout r = new LinearLayout(c);
        r.setGravity(Gravity.CENTER_VERTICAL);
        r.addView(Ui.text(c, label, 14, Palette.MUTED, Fonts.inter(c, 400)), new LinearLayout.LayoutParams(0, -2, 1f));
        r.addView(right);
        return r;
    }
}
