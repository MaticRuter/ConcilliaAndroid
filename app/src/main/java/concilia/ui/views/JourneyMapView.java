package concilia.ui.views;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import concilia.data.Council;
import concilia.data.CouncilsData;
import concilia.data.Era;
import concilia.ui.Fonts;
import concilia.ui.Palette;
import concilia.ui.Ui;

import java.util.List;
import java.util.Locale;

/**
 * The Duolingo-style winding journey map (TimelineScreen.tsx):
 * era-tinted bands, a curved path zig-zagging left/right, a node + label card per council,
 * and the crusader knight floating above the active council.
 *
 * Child views are positioned by hand in onLayout so everything lines up with the path.
 */
public class JourneyMapView extends ViewGroup {

    public interface Listener { void onCouncilClick(Council council); }

    // ── layout constants (dp), same numbers as the Figma ──
    private static final float NODE_STEP = 148f;
    private static final float Y_START = 60f;
    private static final float X_LEFT = 88f / 390f;    // fractions of the screen width
    private static final float X_RIGHT = 302f / 390f;
    private static final float CARD_W = 152f;
    private static final float KNIGHT_W = 64f;

    private final float dp;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path path = new Path();

    private List<Council> councils;
    private boolean[] completed, locked;
    private int currentIdx;
    private Listener listener;
    private CrusaderKnightView knight;
    private ObjectAnimator floatAnim;

    public JourneyMapView(Context c) { this(c, null); }

    public JourneyMapView(Context c, AttributeSet a) {
        super(c, a);
        dp = Ui.density(c);
        setWillNotDraw(false);
        setClipChildren(false);
    }

    public void setListener(Listener l) { this.listener = l; }

    /** (Re)build the map. completed/locked are indexed like the councils list. */
    public void setData(List<Council> councils, boolean[] completed, boolean[] locked) {
        this.councils = councils;
        this.completed = completed;
        this.locked = locked;

        // active council = first one that is neither completed nor locked (else the last one)
        currentIdx = councils.size() - 1;
        for (int i = 0; i < councils.size(); i++) {
            if (!completed[i] && !locked[i]) { currentIdx = i; break; }
        }

        if (floatAnim != null) { floatAnim.cancel(); floatAnim = null; }
        removeAllViews();
        Context ctx = getContext();

        for (int i = 0; i < councils.size(); i++) {
            final Council co = councils.get(i);
            Era era = CouncilsData.eraFor(co);
            boolean isActive = i == currentIdx;

            NodeView node = new NodeView(ctx);
            node.configure(era.color, co.number, completed[i], isActive, locked[i]);
            if (!locked[i]) {
                node.setOnClickListener(new OnClickListener() {
                    @Override public void onClick(View v) { if (listener != null) listener.onCouncilClick(co); }
                });
            }
            addView(node);
            addView(buildCard(ctx, co, era.color, i, isActive));
        }

        knight = new CrusaderKnightView(ctx, Math.round(KNIGHT_W * dp));
        knight.setFacingLeft(currentIdx % 2 != 0);
        addView(knight);

        floatAnim = ObjectAnimator.ofFloat(knight, "translationY", 0f, -5f * dp);
        floatAnim.setDuration(1200);
        floatAnim.setRepeatCount(ObjectAnimator.INFINITE);
        floatAnim.setRepeatMode(ObjectAnimator.REVERSE);
        floatAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        floatAnim.start();

        requestLayout();
        invalidate();
    }

    @Override protected void onDetachedFromWindow() {
        if (floatAnim != null) floatAnim.cancel();
        super.onDetachedFromWindow();
    }

    @Override protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (floatAnim != null && !floatAnim.isStarted()) floatAnim.start();
    }

    // ── label card next to every node ─────────────────────────────────────
    private View buildCard(Context ctx, final Council co, int color, int idx, boolean isActive) {
        boolean done = completed[idx], lock = locked[idx];

        LinearLayout card = new LinearLayout(ctx);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(Ui.dp(ctx, 12), Ui.dp(ctx, 10), Ui.dp(ctx, 12), Ui.dp(ctx, 10));
        int bg = done ? Ui.alpha(color, 0x18) : isActive ? Palette.CARD : Palette.CARD_FADED;
        int bd = done ? Ui.alpha(color, 0x55) : isActive ? Ui.alpha(color, 0x44) : Palette.BORDER;
        card.setBackground(Ui.box(ctx, bg, bd, 1, 14));
        card.setAlpha(lock ? 0.35f : 1f);

        TextView year = Ui.text(ctx, co.year.toUpperCase(Locale.ROOT), 9, color, Fonts.inter(ctx, 700));
        Ui.tracking(year, 0.12f);
        card.addView(year, Ui.lpMargins(ctx, -2, -2, 0, 0, 0, 3));

        TextView name = Ui.text(ctx, shortName(co.name), 11, Palette.FOREGROUND, Fonts.playfair(ctx, 700));
        Ui.lineHeight(name, 1.35f);
        card.addView(name, Ui.lpMargins(ctx, -1, -2, 0, 0, 0, 2));

        TextView loc = Ui.text(ctx, co.location.split(",")[0], 9, Palette.MUTED, Fonts.inter(ctx, 400));
        card.addView(loc, Ui.lp(-1, -2));

        if (!lock) {
            LinearLayout pips = new LinearLayout(ctx);
            pips.setOrientation(LinearLayout.HORIZONTAL);
            for (int i = 0; i < co.lessons.size(); i++) {
                View pip = new View(ctx);
                pip.setBackground(Ui.box(ctx, done ? color : Palette.BORDER, 0, 0, 2));
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, Ui.dp(ctx, 3), 1f);
                if (i > 0) lp.leftMargin = Ui.dp(ctx, 3);
                pips.addView(pip, lp);
            }
            card.addView(pips, Ui.lpMargins(ctx, -1, -2, 0, 5, 0, 0));
        }

        if (!lock) {
            card.setOnClickListener(new OnClickListener() {
                @Override public void onClick(View v) { if (listener != null) listener.onCouncilClick(co); }
            });
        }
        return card;
    }

    /** "Council of Nicaea" -> "Nicaea", "First ..." -> "I ..." (JS .replace = first match only). */
    private static String shortName(String n) {
        n = replaceFirst(n, "Council of ", "");
        n = replaceFirst(n, "First ", "I ");
        n = replaceFirst(n, "Second ", "II ");
        n = replaceFirst(n, "Third ", "III ");
        n = replaceFirst(n, "Fourth ", "IV ");
        n = replaceFirst(n, "Fifth ", "V ");
        return n;
    }

    private static String replaceFirst(String s, String find, String with) {
        int i = s.indexOf(find);
        return i < 0 ? s : s.substring(0, i) + with + s.substring(i + find.length());
    }

    // ── geometry ─────────────────────────────────────────────────────────
    private float nodeX(int idx, float width) { return (idx % 2 == 0 ? X_LEFT : X_RIGHT) * width; }

    private float nodeYpx(int idx) { return (Y_START + idx * NODE_STEP) * dp; }

    public int getCurrentIdx() { return currentIdx; }

    /** y (px) of the active council - the activity scrolls here. */
    public float getActiveY() { return nodeYpx(currentIdx); }

    private float totalHeightPx() {
        int n = councils == null ? 0 : councils.size();
        return (Y_START + n * NODE_STEP + 120f) * dp;
    }

    @Override protected void onMeasure(int wSpec, int hSpec) {
        int w = MeasureSpec.getSize(wSpec);
        int h = Math.round(totalHeightPx());
        int nodeSpec = MeasureSpec.makeMeasureSpec(Math.round(NodeView.SIZE_DP * dp), MeasureSpec.EXACTLY);
        int cardW = MeasureSpec.makeMeasureSpec(Math.round(CARD_W * dp), MeasureSpec.EXACTLY);
        int free = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            if (v instanceof NodeView) v.measure(nodeSpec, nodeSpec);
            else if (v instanceof CrusaderKnightView) v.measure(free, free);
            else v.measure(cardW, free);
        }
        setMeasuredDimension(w, h);
    }

    @Override protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (councils == null) return;
        float w = r - l;
        for (int i = 0; i < councils.size(); i++) {
            float cx = nodeX(i, w), cy = nodeYpx(i);
            View node = getChildAt(i * 2);
            View card = getChildAt(i * 2 + 1);

            node.layout(Math.round(cx - node.getMeasuredWidth() / 2f), Math.round(cy - node.getMeasuredHeight() / 2f),
                    Math.round(cx + node.getMeasuredWidth() / 2f), Math.round(cy + node.getMeasuredHeight() / 2f));

            // card sits on the inner side: left node -> card to its right, right node -> card to its left
            float cw = card.getMeasuredWidth(), ch = card.getMeasuredHeight();
            float left = (i % 2 == 0) ? cx + 41f * dp : cx - 41f * dp - cw;
            card.layout(Math.round(left), Math.round(cy - ch / 2f), Math.round(left + cw), Math.round(cy + ch / 2f));
        }
        if (knight != null) {
            float kx = nodeX(currentIdx, w);
            int kl = Math.round(kx - knight.getMeasuredWidth() / 2f);
            int kt = Math.round(nodeYpx(currentIdx) - 58f * dp);
            knight.layout(kl, kt, kl + knight.getMeasuredWidth(), kt + knight.getMeasuredHeight());
        }
    }

    // ── background layer: era bands, path, path dots, "Finis" ─────────────
    @Override protected void onDraw(Canvas c) {
        if (councils == null) return;
        float w = getWidth();
        Context ctx = getContext();

        // era bands
        for (Era era : CouncilsData.ERAS) {
            int first = Integer.MAX_VALUE, last = -1;
            for (int num : era.councils) {
                for (int i = 0; i < councils.size(); i++) {
                    if (councils.get(i).number == num) { first = Math.min(first, i); last = Math.max(last, i); }
                }
            }
            if (last < 0) continue;
            float top = nodeYpx(first) - NODE_STEP * 0.45f * dp;
            float bot = nodeYpx(last) + NODE_STEP * 0.55f * dp;

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Ui.alpha(era.color, 0x20));
            c.drawRect(0, top, w, bot, paint);
            paint.setColor(Ui.alpha(era.color, 0x22));
            c.drawRect(0, top, w, top + Math.max(1f, dp), paint);

            // era name (left) + roman range (right)
            paint.setTextSize(10f * dp);
            paint.setColor(era.color);
            paint.setTextAlign(Paint.Align.LEFT);
            paint.setTypeface(Fonts.inter(ctx, 700));
            paint.setLetterSpacing(0.12f);
            paint.setAlpha(Math.round(255 * 0.8f));
            c.drawText(era.name.toUpperCase(Locale.ROOT), 12f * dp, top + 10f * dp - paint.ascent(), paint);

            paint.setTypeface(Fonts.inter(ctx, 400));
            paint.setLetterSpacing(0f);
            paint.setTextAlign(Paint.Align.RIGHT);
            paint.setAlpha(Math.round(255 * 0.5f));
            c.drawText(romanRange(era), w - 12f * dp, top + 8f * dp - paint.ascent(), paint);
            paint.setAlpha(255);
        }
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setLetterSpacing(0f);

        // path
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(12f * dp);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setColor(Palette.BORDER);
        buildPath(councils.size(), w);
        c.drawPath(path, paint);
        if (currentIdx > 0) {
            paint.setColor(Palette.GOLD);
            paint.setAlpha(Math.round(255 * 0.35f));
            buildPath(currentIdx + 1, w);
            c.drawPath(path, paint);
            paint.setAlpha(255);
        }

        // little dots on every curve midpoint
        for (int i = 1; i < councils.size(); i++) {
            float mx = (nodeX(i - 1, w) + nodeX(i, w)) / 2f;
            float my = (nodeYpx(i - 1) + nodeYpx(i)) / 2f;
            boolean reached = i <= currentIdx;
            int a = Math.round(255 * (reached ? 0.6f : 0.4f));
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Ui.alpha(reached ? Palette.GOLD : Palette.CARD, a));
            c.drawCircle(mx, my, 4f * dp, paint);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1.5f * dp);
            paint.setColor(Ui.alpha(reached ? Palette.GOLD : Palette.BORDER, a));
            c.drawCircle(mx, my, 4f * dp, paint);
        }

        // "— Finis —"
        paint.setStyle(Paint.Style.FILL);
        paint.setTypeface(Fonts.inter(ctx, 400));
        paint.setTextSize(10f * dp);
        paint.setLetterSpacing(0.2f);
        paint.setColor(Palette.BORDER);
        paint.setTextAlign(Paint.Align.CENTER);
        c.drawText("— FINIS —", w / 2f, getHeight() - 20f * dp - paint.descent(), paint);
        paint.setLetterSpacing(0f);
        paint.setTextAlign(Paint.Align.LEFT);
    }

    private void buildPath(int count, float w) {
        path.reset();
        if (count == 0) return;
        path.moveTo(nodeX(0, w), nodeYpx(0));
        for (int i = 1; i < count; i++) {
            float x0 = nodeX(i - 1, w), y0 = nodeYpx(i - 1);
            float x1 = nodeX(i, w), y1 = nodeYpx(i);
            float cy = (y0 + y1) / 2f;
            path.cubicTo(x0, cy, x1, cy, x1, y1);
        }
    }

    private static String romanRange(Era e) {
        switch (e.name) {
            case "Patristic Age":   return "I–VII";
            case "Byzantine Age":   return "VIII";
            case "Medieval Church": return "IX–XIV";
            case "Late Medieval":   return "XV–XVII";
            case "Reformation Era": return "XVIII–XIX";
            case "Modern Church":   return "XX–XXI";
            default:                return "";
        }
    }
}
