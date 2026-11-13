package concilia;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import concilia.data.Council;
import concilia.data.CouncilsData;
import concilia.data.Era;
import concilia.data.ProgressStore;
import concilia.ui.Fonts;
import concilia.ui.Palette;
import concilia.ui.Ui;
import concilia.ui.views.BackButtonView;
import concilia.ui.views.ProgressBarView;

import java.util.Locale;
import java.util.Set;

/** "Your Progress": level, XP, stats, progress per era, achievements (ProfileScreen.tsx). */
public class ProfileActivity extends Activity {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context c = this;
        ProgressStore store = ProgressStore.get(c);
        Set<Integer> done = store.getCompletedIds();
        int xp = store.getXp(), streak = store.getStreak();
        int level = xp / 100 + 1, xpInLevel = xp % 100;

        LinearLayout root = new LinearLayout(c);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Palette.BACKGROUND);
        Ui.applyInsets(root);

        // header
        LinearLayout header = new LinearLayout(c);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(Ui.dp(c, 16), Ui.dp(c, 16), Ui.dp(c, 16), Ui.dp(c, 16));
        BackButtonView back = new BackButtonView(c, 8, 1.5f);
        back.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { finish(); }
        });
        header.addView(back, Ui.lpMargins(c, -2, -2, 0, 0, 12, 0));
        header.addView(Ui.text(c, "Your Progress", 18, Palette.FOREGROUND, Fonts.playfair(c, 700)));
        root.addView(header, Ui.lp(-1, -2));
        root.addView(Ui.hairline(c, Palette.BORDER));

        ScrollView scroll = new ScrollView(c);
        scroll.setVerticalScrollBarEnabled(false);
        LinearLayout body = new LinearLayout(c);
        body.setOrientation(LinearLayout.VERTICAL);
        body.setPadding(Ui.dp(c, 24), Ui.dp(c, 24), Ui.dp(c, 24), Ui.dp(c, 48));

        // ── avatar + level + XP bar ──
        LinearLayout top = new LinearLayout(c);
        top.setOrientation(LinearLayout.VERTICAL);
        top.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView avatar = Ui.text(c, "☩", 30, Palette.FOREGROUND, null);
        avatar.setGravity(Gravity.CENTER);
        avatar.setBackground(Ui.box(c, Palette.CARD, Ui.alpha(Palette.GOLD, 0x44), 2, 999));
        top.addView(avatar, Ui.lpMargins(c, 80, 80, 0, 0, 0, 12));

        TextView title = Ui.text(c, "Acolyte", 18, Palette.FOREGROUND, Fonts.playfair(c, 700));
        top.addView(title);
        TextView lvl = Ui.text(c, ("Level " + level + " Scholar").toUpperCase(Locale.ROOT), 12, Palette.MUTED, Fonts.inter(c, 400));
        Ui.tracking(lvl, 0.1f);
        top.addView(lvl);

        LinearLayout xpRow = new LinearLayout(c);
        xpRow.addView(Ui.text(c, xpInLevel + " XP", 12, Palette.MUTED, Fonts.inter(c, 400)), new LinearLayout.LayoutParams(0, -2, 1f));
        TextView toNext = Ui.text(c, "100 XP to Level " + (level + 1), 12, Palette.MUTED, Fonts.inter(c, 400));
        xpRow.addView(toNext);
        top.addView(xpRow, Ui.lpMargins(c, -1, -2, 0, 16, 0, 4));

        ProgressBarView xpBar = new ProgressBarView(c, Palette.GOLD).setGlow(true);
        xpBar.setProgress(xpInLevel / 100f);
        top.addView(xpBar, Ui.lp(-1, Ui.dp(c, 8)));
        body.addView(top, Ui.lpMargins(c, -1, -2, 0, 0, 0, 24));

        // ── stats grid ──
        LinearLayout grid = new LinearLayout(c);
        String[][] stats = {
                {"🔥", String.valueOf(streak), "Day Streak"},
                {"✦", String.valueOf(xp), "Total XP"},
                {"⛪", done.size() + "/21", "Councils"},
        };
        for (int i = 0; i < 3; i++) {
            LinearLayout cell = new LinearLayout(c);
            cell.setOrientation(LinearLayout.VERTICAL);
            cell.setGravity(Gravity.CENTER_HORIZONTAL);
            cell.setPadding(Ui.dp(c, 12), Ui.dp(c, 12), Ui.dp(c, 12), Ui.dp(c, 12));
            cell.setBackground(Ui.box(c, Palette.CARD, Palette.BORDER, 1, 12));
            cell.addView(Ui.text(c, stats[i][0], 24, i == 1 ? Palette.GOLD : Palette.FOREGROUND, null), Ui.lpMargins(c, -2, -2, 0, 0, 0, 4));
            cell.addView(Ui.text(c, stats[i][1], 18, Palette.FOREGROUND, Fonts.inter(c, 700)));
            cell.addView(Ui.text(c, stats[i][2], 12, Palette.MUTED, Fonts.inter(c, 400)));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, -2, 1f);
            if (i > 0) lp.leftMargin = Ui.dp(c, 12);
            grid.addView(cell, lp);
        }
        body.addView(grid, Ui.lpMargins(c, -1, -2, 0, 0, 0, 24));

        // ── progress by era ──
        body.addView(label(c, "Progress by Era"), Ui.lpMargins(c, -2, -2, 0, 0, 0, 12));
        LinearLayout eras = new LinearLayout(c);
        eras.setOrientation(LinearLayout.VERTICAL);
        for (int i = 0; i < CouncilsData.ERAS.size(); i++) {
            Era e = CouncilsData.ERAS.get(i);
            int total = e.councils.length, got = 0;
            for (int n : e.councils) {
                Council co = councilByNumber(n);
                if (co != null && done.contains(co.id)) got++;
            }
            LinearLayout line = new LinearLayout(c);
            line.addView(Ui.text(c, e.name, 12, e.color, Fonts.inter(c, 400)), new LinearLayout.LayoutParams(0, -2, 1f));
            line.addView(Ui.text(c, got + "/" + total, 12, Palette.MUTED, Fonts.inter(c, 400)));
            eras.addView(line, Ui.lpMargins(c, -1, -2, 0, i == 0 ? 0 : 12, 0, 4));
            ProgressBarView bar = new ProgressBarView(c, e.color);
            bar.setProgress(total == 0 ? 0f : got / (float) total);
            eras.addView(bar, Ui.lp(-1, Ui.dp(c, 6)));
        }
        body.addView(eras, Ui.lpMargins(c, -1, -2, 0, 0, 0, 24));

        // ── achievements ──
        body.addView(label(c, "Achievements"), Ui.lpMargins(c, -2, -2, 0, 0, 0, 12));
        boolean first = !done.isEmpty();
        boolean patristic = allDone(done, CouncilsData.ERAS.get(0));
        boolean medieval = allDone(done, CouncilsData.ERAS.get(2));
        boolean trent = done.contains(19);
        boolean all = done.size() >= 21;
        Object[][] ach = {
                {"First Council", "Completed Nicaea I", "🌟", first},
                {"Church Father", "Complete all Patristic Age councils", "✝", patristic},
                {"Scholastic", "Complete all Medieval councils", "📖", medieval},
                {"Counter-Reformer", "Complete the Council of Trent", "⛪", trent},
                {"Ecumenical", "Complete all 21 councils", "🏛", all},
        };
        LinearLayout list = new LinearLayout(c);
        list.setOrientation(LinearLayout.VERTICAL);
        for (int i = 0; i < ach.length; i++) {
            boolean earned = (Boolean) ach[i][3];
            LinearLayout row = new LinearLayout(c);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(Ui.dp(c, 12), Ui.dp(c, 12), Ui.dp(c, 12), Ui.dp(c, 12));
            row.setBackground(Ui.box(c, earned ? Palette.CARD : Palette.CARD_LOCKED,
                    earned ? Ui.alpha(Palette.GOLD, 0x44) : Palette.BORDER, 1, 12));
            row.setAlpha(earned ? 1f : 0.5f);

            TextView ic = Ui.text(c, (String) ach[i][2], 18, Palette.FOREGROUND, null);
            ic.setGravity(Gravity.CENTER);
            ic.setBackground(Ui.box(c, earned ? Ui.alpha(Palette.GOLD, 0x22) : Palette.BORDER, 0, 0, 999));
            row.addView(ic, Ui.lpMargins(c, 40, 40, 0, 0, 12, 0));

            LinearLayout tx = new LinearLayout(c);
            tx.setOrientation(LinearLayout.VERTICAL);
            tx.addView(Ui.text(c, (String) ach[i][0], 14, earned ? Palette.FOREGROUND : Palette.MUTED, Fonts.inter(c, 500)));
            tx.addView(Ui.text(c, (String) ach[i][1], 12, Palette.MUTED, Fonts.inter(c, 400)));
            row.addView(tx, new LinearLayout.LayoutParams(0, -2, 1f));

            if (earned) {
                TextView pill = Ui.text(c, "Earned", 12, Palette.GOLD, Fonts.inter(c, 400));
                pill.setPadding(Ui.dp(c, 8), Ui.dp(c, 2), Ui.dp(c, 8), Ui.dp(c, 2));
                pill.setBackground(Ui.box(c, Ui.alpha(Palette.GOLD, 0x22), 0, 0, 999));
                row.addView(pill);
            }
            list.addView(row, Ui.lpMargins(c, -1, -2, 0, i == 0 ? 0 : 8, 0, 0));
        }
        body.addView(list, Ui.lpMargins(c, -1, -2, 0, 0, 0, 24));

        // ── footer quote ──
        TextView q = Ui.text(c, "\"The Church is not a democracy, but she has a voice in every age.\"",
                14, Palette.MUTED, Fonts.fell(c, true));
        q.setGravity(Gravity.CENTER);
        q.setPadding(Ui.dp(c, 16), Ui.dp(c, 16), Ui.dp(c, 16), Ui.dp(c, 16));
        q.setBackground(Ui.box(c, Palette.CARD, Palette.BORDER, 1, 12));
        body.addView(q, Ui.lp(-1, -2));

        scroll.addView(body, new FrameLayout.LayoutParams(-1, -2));
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1f));
        setContentView(root);
    }

    private static Council councilByNumber(int n) {
        for (Council co : CouncilsData.COUNCILS) if (co.number == n) return co;
        return null;
    }

    private static boolean allDone(Set<Integer> done, Era e) {
        for (int n : e.councils) {
            Council co = councilByNumber(n);
            if (co == null || !done.contains(co.id)) return false;
        }
        return true;
    }

    private static TextView label(Context c, String s) {
        TextView t = Ui.text(c, s.toUpperCase(Locale.ROOT), 12, Palette.MUTED, Fonts.inter(c, 600));
        Ui.tracking(t, 0.1f);
        return t;
    }
}
