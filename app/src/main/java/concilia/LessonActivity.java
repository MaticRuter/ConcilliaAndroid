package concilia;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import concilia.data.Council;
import concilia.data.CouncilsData;
import concilia.data.Lesson;
import concilia.data.ProgressStore;
import concilia.ui.Fonts;
import concilia.ui.Palette;
import concilia.ui.Ui;
import concilia.ui.views.BackButtonView;

import java.util.Locale;

/** Council detail page: hero, key decision, overview, heresy, key figure, quote, lessons (LessonScreen.tsx). */
public class LessonActivity extends Activity {
    private boolean shouldRefreshLessons = false;
    public static final String EXTRA_COUNCIL_ID = "council_id";

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context c = this;
        final Council co = CouncilsData.byId(getIntent().getIntExtra(EXTRA_COUNCIL_ID, 1));
        if (co == null) { finish(); return; }
        final int era = co.eraColor;

        LinearLayout root = new LinearLayout(c);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Palette.BACKGROUND);
        Ui.applyInsets(root);

        // ── header ──
        LinearLayout header = new LinearLayout(c);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(Ui.dp(c, 16), Ui.dp(c, 16), Ui.dp(c, 16), Ui.dp(c, 16));

        BackButtonView back = new BackButtonView(c, 8, 1.5f);
        back.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { finish(); }
        });
        header.addView(back, Ui.lpMargins(c, -2, -2, 0, 0, 12, 0));

        TextView label = Ui.text(c, ("Council " + co.number + " · " + co.year).toUpperCase(Locale.ROOT),
                12, era, Fonts.inter(c, 500));
        Ui.tracking(label, 0.1f);
        header.addView(label, new LinearLayout.LayoutParams(0, -2, 1f));

        TextView eraPill = Ui.text(c, co.era, 12, era, Fonts.inter(c, 400));
        eraPill.setPadding(Ui.dp(c, 8), Ui.dp(c, 4), Ui.dp(c, 8), Ui.dp(c, 4));
        eraPill.setBackground(Ui.box(c, Ui.alpha(era, 0x22), 0, 0, 999));
        header.addView(eraPill);

        root.addView(header, Ui.lp(-1, -2));
        root.addView(Ui.hairline(c, Palette.BORDER));

        // ── scrolling content ──
        ScrollView scroll = new ScrollView(c);
        scroll.setVerticalScrollBarEnabled(false);
        LinearLayout body = new LinearLayout(c);
        body.setOrientation(LinearLayout.VERTICAL);

        // hero
        LinearLayout hero = new LinearLayout(c);
        hero.setOrientation(LinearLayout.VERTICAL);
        hero.setPadding(Ui.dp(c, 24), Ui.dp(c, 32), Ui.dp(c, 24), Ui.dp(c, 24));
        hero.setBackground(Ui.vGradient(Ui.alpha(era, 0x22), Ui.alpha(era, 0x00)));

        TextView cross = Ui.text(c, "✝", 36, era, null);
        cross.setAlpha(0.4f);
        cross.setGravity(Gravity.CENTER);
        hero.addView(cross, Ui.lpMargins(c, -1, -2, 0, 0, 0, 16));

        TextView name = Ui.text(c, co.name, 24, Palette.FOREGROUND, Fonts.playfair(c, 700));
        name.setGravity(Gravity.CENTER);
        Ui.lineHeight(name, 1.25f);
        hero.addView(name, Ui.lpMargins(c, -1, -2, 0, 0, 0, 8));

        LinearLayout meta = new LinearLayout(c);
        meta.setGravity(Gravity.CENTER);
        meta.addView(Ui.text(c, co.location, 14, Palette.MUTED, Fonts.inter(c, 400)));
        View dot = new View(c);
        dot.setBackground(Ui.box(c, Palette.BORDER, 0, 0, 999));
        meta.addView(dot, Ui.lpMargins(c, 4, 4, 12, 0, 12, 0));
        meta.addView(Ui.text(c, co.year, 14, era, Fonts.inter(c, 500)));
        hero.addView(meta, Ui.lpMargins(c, -1, -2, 0, 0, 0, 24));

        LinearLayout decision = new LinearLayout(c);
        decision.setOrientation(LinearLayout.VERTICAL);
        decision.setPadding(Ui.dp(c, 16), Ui.dp(c, 16), Ui.dp(c, 16), Ui.dp(c, 16));
        decision.setBackground(Ui.box(c, Ui.alpha(era, 0x15), Ui.alpha(era, 0x44), 1, 12));

        TextView dl = sectionLabel(c, "Key Decision", era);
        decision.addView(dl, Ui.lpMargins(c, -2, -2, 0, 0, 0, 6));
        TextView dt = Ui.text(c, co.decision, 14, Palette.FOREGROUND, Fonts.inter(c, 400));
        Ui.lineHeight(dt, 1.625f);
        decision.addView(dt);
        hero.addView(decision, Ui.lp(-1, -2));
        body.addView(hero, Ui.lp(-1, -2));

        // sections
        LinearLayout sec = new LinearLayout(c);
        sec.setOrientation(LinearLayout.VERTICAL);
        sec.setPadding(Ui.dp(c, 24), 0, Ui.dp(c, 24), Ui.dp(c, 24));

        // overview
        sec.addView(sectionLabel(c, "Overview", Palette.MUTED), Ui.lpMargins(c, -2, -2, 0, 0, 0, 8));
        TextView ov = Ui.text(c, co.summary, 14, Palette.SOFT_TEXT, Fonts.inter(c, 400));
        Ui.lineHeight(ov, 1.625f);
        sec.addView(ov, Ui.lpMargins(c, -1, -2, 0, 0, 0, 20));

        // heresy
        LinearLayout heresy = infoRow(c);
        TextView hi = Ui.text(c, "⚠", 18, Palette.FOREGROUND, null);
        hi.setGravity(Gravity.CENTER);
        hi.setBackground(Ui.box(c, 0x227A1C1C, 0, 0, 8));
        heresy.addView(hi, Ui.lpMargins(c, 40, 40, 0, 0, 12, 0));
        LinearLayout ht = new LinearLayout(c);
        ht.setOrientation(LinearLayout.VERTICAL);
        ht.addView(sectionLabel(c, "Heresy Addressed", Palette.MUTED), Ui.lpMargins(c, -2, -2, 0, 0, 0, 2));
        ht.addView(Ui.text(c, co.heresy, 14, Palette.FOREGROUND, Fonts.inter(c, 500)));
        heresy.addView(ht, new LinearLayout.LayoutParams(0, -2, 1f));
        sec.addView(heresy, Ui.lpMargins(c, -1, -2, 0, 0, 0, 20));

        // key figure
        LinearLayout fig = infoRow(c);
        TextView fi = Ui.text(c, "👤", 18, era, null);
        fi.setGravity(Gravity.CENTER);
        fi.setBackground(Ui.box(c, Ui.alpha(era, 0x22), 0, 0, 999));
        fig.addView(fi, Ui.lpMargins(c, 40, 40, 0, 0, 12, 0));
        LinearLayout ft = new LinearLayout(c);
        ft.setOrientation(LinearLayout.VERTICAL);
        ft.addView(sectionLabel(c, "Key Figure", Palette.MUTED), Ui.lpMargins(c, -2, -2, 0, 0, 0, 2));
        ft.addView(Ui.text(c, co.keyFigure, 14, Palette.FOREGROUND, Fonts.inter(c, 500)));
        ft.addView(Ui.text(c, co.keyFigureRole, 12, Palette.MUTED, Fonts.inter(c, 400)));
        fig.addView(ft, new LinearLayout.LayoutParams(0, -2, 1f));
        sec.addView(fig, Ui.lpMargins(c, -1, -2, 0, 0, 0, 20));

        // quote (big opening quote mark floats over the top-left edge)
        FrameLayout quoteWrap = new FrameLayout(c);
        quoteWrap.setClipChildren(false);
        LinearLayout quote = new LinearLayout(c);
        quote.setOrientation(LinearLayout.VERTICAL);
        quote.setPadding(Ui.dp(c, 16), Ui.dp(c, 24), Ui.dp(c, 16), Ui.dp(c, 16));
        quote.setBackground(Ui.box(c, Palette.CARD, Palette.BORDER, 1, 12));
        TextView qt = Ui.text(c, co.quote, 14, Palette.SOFT_TEXT, Fonts.fell(c, true));
        Ui.lineHeight(qt, 1.625f);
        quote.addView(qt);
        TextView qs = Ui.text(c, "— " + co.quoteSource, 12, Palette.MUTED, Fonts.inter(c, 400));
        quote.addView(qs, Ui.lpMargins(c, -2, -2, 0, 8, 0, 0));
        quoteWrap.addView(quote, new FrameLayout.LayoutParams(-1, -2));
        TextView mark = Ui.text(c, "“", 30, Ui.alpha(era, 0x88), Fonts.georgiaBold());
        FrameLayout.LayoutParams mlp = new FrameLayout.LayoutParams(-2, -2);
        mlp.leftMargin = Ui.dp(c, 16);
        mlp.topMargin = Ui.dp(c, 2);
        quoteWrap.addView(mark, mlp);
        sec.addView(quoteWrap, Ui.lpMargins(c, -1, -2, 0, 4, 0, 20));

        // lessons list
        // lessons list (prefer JSON when assets/councils/council_N.json exists)



        // ── Lessons list (high-level units from JSON) ──
        final java.util.List<Lesson> all = CouncilsData.lessonsFor(c, co);

        final java.util.List<Lesson> units = new java.util.ArrayList<>();
        final java.util.List<Integer> unitLessonIds = new java.util.ArrayList<>();
        for (Lesson l : all) {
            if (l.type == Lesson.Type.INFO) {
                units.add(l);
                try {
                    String[] parts = l.id.split("-");
                    unitLessonIds.add(Integer.parseInt(parts[1]));
                } catch (Exception e) {
                    unitLessonIds.add(units.size());
                }
            }
        }

        final ProgressStore progress = ProgressStore.get(c);
        final int nextId = progress.nextLessonId(co.id, units.size());


        sec.addView(sectionLabel(c, "Lessons in this Council", Palette.MUTED), Ui.lpMargins(c, -2, -2, 0, 0, 0, 12));

        for (int i = 0; i < units.size(); i++) {
            final Lesson unit = units.get(i);
            final int lessonId = unitLessonIds.get(i);
            final boolean completed = progress.isLessonCompleted(co.id, lessonId);
            final boolean locked = !completed && lessonId != nextId;

            LinearLayout row = infoRow(c);
            row.setAlpha(locked ? 0.45f : 1f);

            String badge = locked ? "🔒" : (completed ? "✓" : String.valueOf(i + 1));
            TextView num = Ui.text(c, badge, 13, era, Fonts.inter(c, 700));
            num.setGravity(Gravity.CENTER);
            num.setBackground(Ui.box(c, Ui.alpha(era, 0x22), 0, 0, 999));
            row.addView(num, Ui.lpMargins(c, 32, 32, 0, 0, 12, 0));

            LinearLayout mid = new LinearLayout(c);
            mid.setOrientation(LinearLayout.VERTICAL);
            TextView lt = Ui.text(c, unit.title, 14, Palette.FOREGROUND, Fonts.inter(c, 500));
            lt.setSingleLine(true);
            lt.setEllipsize(TextUtils.TruncateAt.END);
            mid.addView(lt);
            String status = completed ? "Completed" : (locked ? "Locked" : "Start lesson");
            mid.addView(Ui.text(c, status, 12, Palette.MUTED, Fonts.inter(c, 400)));
            row.addView(mid, new LinearLayout.LayoutParams(0, -2, 1f));

            if (!locked) {
                row.setClickable(true);
                Ui.pressScale(row, 0.98f);
                row.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        shouldRefreshLessons = true;
                        Intent intent = new Intent(LessonActivity.this, QuizActivity.class);
                        intent.putExtra(EXTRA_COUNCIL_ID, co.id);
                        intent.putExtra("lesson_id", lessonId);
                        startActivity(intent);
                    }
                });
            }

            sec.addView(row, Ui.lpMargins(c, -1, -2, 0, 0, 0, i == units.size() - 1 ? 0 : 8));
        }

        // Pomembno: dodaj sec v body in scroll v root
        body.addView(sec, Ui.lp(-1, -2));
        scroll.addView(body, new FrameLayout.LayoutParams(-1, -2));
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1f));

        setContentView(root);
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (shouldRefreshLessons) {
            shouldRefreshLessons = false;
            recreate();
        }
    }


    /** 12sp uppercase tracking-widest label. */
    private static TextView sectionLabel(Context c, String s, int color) {
        TextView t = Ui.text(c, s.toUpperCase(Locale.ROOT), 12, color, Fonts.inter(c, 600));
        Ui.tracking(t, 0.1f);
        return t;
    }

    /** Rounded card row (bg-card, border, radius 12, padding 12). */
    private static LinearLayout infoRow(Context c) {
        LinearLayout row = new LinearLayout(c);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(Ui.dp(c, 12), Ui.dp(c, 12), Ui.dp(c, 12), Ui.dp(c, 12));
        row.setBackground(Ui.box(c, Palette.CARD, Palette.BORDER, 1, 12));
        return row;
    }
}
