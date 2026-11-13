package concilia;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import concilia.data.Council;
import concilia.data.CouncilsData;
import concilia.data.ProgressStore;
import concilia.ui.Fonts;
import concilia.ui.Palette;
import concilia.ui.Ui;
import concilia.ui.views.JourneyMapView;

import java.util.Locale;
import java.util.Set;

/** Main page: the winding journey through the 21 councils (TimelineScreen.tsx). */
public class TimelineActivity extends Activity {

    private JourneyMapView map;
    private ScrollView scroll;
    private TextView streakText, xpText;
    private int lastActiveIdx = -1;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context c = this;

        LinearLayout root = new LinearLayout(c);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Palette.BACKGROUND);
        Ui.applyInsets(root);

        // ── header ──
        LinearLayout header = new LinearLayout(c);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(Ui.dp(c, 16), Ui.dp(c, 12), Ui.dp(c, 16), Ui.dp(c, 12));

        TextView brand = Ui.text(c, "Concilia", 20, Palette.GOLD, Fonts.playfair(c, 700));
        header.addView(brand, new LinearLayout.LayoutParams(0, -2, 1f));

        header.addView(Ui.text(c, "🔥", 14, Palette.FOREGROUND, null));
        streakText = Ui.text(c, "", 14, Palette.FOREGROUND, Fonts.inter(c, 600));
        header.addView(streakText, Ui.lpMargins(c, -2, -2, 6, 0, 16, 0));

        header.addView(Ui.text(c, "✦", 13, Palette.GOLD, null));
        xpText = Ui.text(c, "", 14, Palette.FOREGROUND, Fonts.inter(c, 600));
        header.addView(xpText, Ui.lpMargins(c, -2, -2, 6, 0, 16, 0));

        TextView profile = Ui.text(c, "👤", 13, Palette.FOREGROUND, null);
        profile.setGravity(Gravity.CENTER);
        profile.setBackground(Ui.box(c, Palette.CARD, Palette.BORDER, 1, 999));
        profile.setContentDescription("Profile");
        profile.setClickable(true);
        Ui.pressScale(profile, 0.92f);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                startActivity(new Intent(TimelineActivity.this, ProfileActivity.class));
            }
        });
        header.addView(profile, new LinearLayout.LayoutParams(Ui.dp(c, 32), Ui.dp(c, 32)));

        root.addView(header, Ui.lp(-1, -2));
        root.addView(Ui.hairline(c, Palette.BORDER));

        // ── journey map in a scroll view ──
        scroll = new ScrollView(c);
        scroll.setVerticalScrollBarEnabled(false);
        scroll.setOverScrollMode(View.OVER_SCROLL_NEVER);
        map = new JourneyMapView(c);
        map.setListener(new JourneyMapView.Listener() {
            @Override public void onCouncilClick(Council council) {
                Intent i = new Intent(TimelineActivity.this, LessonActivity.class);
                i.putExtra(LessonActivity.EXTRA_COUNCIL_ID, council.id);
                startActivity(i);
            }
        });
        scroll.addView(map, new FrameLayout.LayoutParams(-1, -2));
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1f));

        setContentView(root);
    }

    @Override protected void onResume() {
        super.onResume();
        refresh();
    }

    @Override protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    private void refresh() {
        ProgressStore store = ProgressStore.get(this);
        streakText.setText(String.format(Locale.US, "%d", store.getStreak()));
        xpText.setText(String.format(Locale.US, "%d XP", store.getXp()));

        int n = CouncilsData.COUNCILS.size();
        boolean[] done = new boolean[n], locked = new boolean[n];
        Set<Integer> ids = store.getCompletedIds();
        for (int i = 0; i < n; i++) {
            done[i] = ids.contains(CouncilsData.COUNCILS.get(i).id);
            locked[i] = store.isLocked(i);
        }
        map.setData(CouncilsData.COUNCILS, done, locked);

        // scroll so the knight sits ~45% down the screen (like the Figma) - only on first show or
        // when the active council changed, so coming back from a lesson doesn't yank the map around
        if (map.getCurrentIdx() != lastActiveIdx) {
            lastActiveIdx = map.getCurrentIdx();
            scroll.post(new Runnable() {
                @Override public void run() {
                    int y = Math.max(0, Math.round(map.getActiveY() - scroll.getHeight() * 0.45f));
                    scroll.scrollTo(0, y);
                }
            });
        }
    }
}
