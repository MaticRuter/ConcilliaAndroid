package concilia;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import concilia.data.ProgressStore;
import concilia.ui.Fonts;
import concilia.ui.Palette;
import concilia.ui.Ui;
import concilia.ui.views.HomeBackdropView;

import java.util.Locale;
import android.content.Intent;
import android.os.Build;

/** Front page: "Concilia - The 21 Ecumenical Councils" + Begin the Journey (HomeScreen.tsx). */
public class HomeActivity extends Activity {

    private TextView streakValue, xpValue;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context c = this;



        FrameLayout root = new FrameLayout(c);
        root.setBackgroundColor(Palette.BACKGROUND);
        Ui.applyInsets(root);
        root.addView(new HomeBackdropView(c), new FrameLayout.LayoutParams(-1, -1));

        // centred column, max 384dp wide (max-w-sm) with 24dp side padding
        int screenW = getResources().getDisplayMetrics().widthPixels;
        int colW = Math.min(Ui.dp(c, 384 + 48), screenW);

        LinearLayout col = new LinearLayout(c);
        col.setOrientation(LinearLayout.VERTICAL);
        col.setGravity(Gravity.CENTER_HORIZONTAL);
        col.setPadding(Ui.dp(c, 24), 0, Ui.dp(c, 24), 0);
        col.setClipChildren(false);

        // era badge pill
        TextView badge = Ui.text(c, "325 AD — 1965 AD", 12, Palette.GOLD, Fonts.inter(c, 400));
        Ui.tracking(badge, 0.1f);
        badge.setAllCaps(true);
        badge.setPadding(Ui.dp(c, 12), Ui.dp(c, 4), Ui.dp(c, 12), Ui.dp(c, 4));
        badge.setBackground(Ui.box(c, Ui.alpha(Palette.GOLD, 0x11), Ui.alpha(Palette.GOLD, 0x33), 1, 999));
        col.addView(badge, Ui.lpMargins(c, -2, -2, 0, 0, 0, 32));

        // title
        TextView title = Ui.text(c, "Concilia", 48, Palette.FOREGROUND, Fonts.playfair(c, 700));
        title.setGravity(Gravity.CENTER);
        Ui.lineHeight(title, 1.25f);
        col.addView(title, Ui.lpMargins(c, -2, -2, 0, 0, 0, 8));

        TextView sub = Ui.text(c, "The 21 Ecumenical Councils", 18, Palette.GOLD, Fonts.fell(c, true));
        sub.setGravity(Gravity.CENTER);
        col.addView(sub, Ui.lpMargins(c, -2, -2, 0, 0, 0, 8));

        TextView desc = Ui.text(c, "Journey through 1,640 years of Church history,\none council at a time.",
                14, Palette.MUTED, Fonts.inter(c, 400));
        desc.setGravity(Gravity.CENTER);
        Ui.lineHeight(desc, 1.43f);
        col.addView(desc, Ui.lpMargins(c, -2, -2, 0, 0, 0, 40));

        // stats row: streak | xp | councils
        LinearLayout stats = new LinearLayout(c);
        stats.setOrientation(LinearLayout.HORIZONTAL);
        stats.setGravity(Gravity.CENTER);
        TextView[] holder = new TextView[2];
        stats.addView(statItem(c, "🔥", "0", "day streak", holder, 0));
        stats.addView(divider(c), Ui.lpMargins(c, 1, -1, 24, 0, 24, 0));
        stats.addView(statItem(c, "✦", "0", "total XP", holder, 1));
        stats.addView(divider(c), Ui.lpMargins(c, 1, -1, 24, 0, 24, 0));
        stats.addView(statItem(c, "⛪", "21", "councils", null, -1));
        streakValue = holder[0];
        xpValue = holder[1];
        col.addView(stats, Ui.lpMargins(c, -2, -2, 0, 0, 0, 40));

        // CTA
        TextView cta = Ui.glowButton(c, "Begin the Journey", Palette.GOLD, Palette.BACKGROUND, 16,
                Fonts.inter(c, 600), 12, 0.025f, Ui.alpha(Palette.GOLD, 0x33));
        cta.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, TimelineActivity.class));
            }
        });
        col.addView(cta, Ui.lp(-1, -2));

        TextView hint = Ui.text(c, "Start with the Council of Nicaea, 325 AD", 12, Palette.MUTED, Fonts.inter(c, 400));
        hint.setGravity(Gravity.CENTER);
        col.addView(hint, Ui.lpMargins(c, -2, -2, 0, 16, 0, 0));

        FrameLayout.LayoutParams colLp = new FrameLayout.LayoutParams(colW, -2, Gravity.CENTER);
        root.addView(col, colLp);
        setContentView(root);

        Intent musicIntent = new Intent(this, MusicService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(musicIntent);
        } else {
            startService(musicIntent);
        }
    }

    @Override protected void onResume() {
        super.onResume();
        ProgressStore store = ProgressStore.get(this);
        streakValue.setText(String.format(Locale.US, "%d", store.getStreak()));
        xpValue.setText(String.format(Locale.US, "%d", store.getXp()));
    }

    private View divider(Context c) {
        View v = new View(c);
        v.setBackgroundColor(Palette.BORDER);
        return v;
    }

    private View statItem(Context c, String icon, String value, String label, TextView[] out, int slot) {
        LinearLayout row = new LinearLayout(c);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);

        TextView ic = Ui.text(c, icon, 20, Palette.FOREGROUND, null);
        row.addView(ic, Ui.lpMargins(c, -2, -2, 0, 0, 8, 0));

        LinearLayout txt = new LinearLayout(c);
        txt.setOrientation(LinearLayout.VERTICAL);
        TextView v = Ui.text(c, value, 18, Palette.FOREGROUND, Fonts.inter(c, 600));
        TextView l = Ui.text(c, label, 12, Palette.MUTED, Fonts.inter(c, 400));
        txt.addView(v);
        txt.addView(l);
        row.addView(txt);
        if (out != null && slot >= 0) out[slot] = v;
        return row;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        Intent musicIntent = new Intent(this, MusicService.class);
        stopService(musicIntent);
    }
}
