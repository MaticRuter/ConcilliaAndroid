package concilia.ui.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import concilia.ui.Fonts;
import concilia.ui.Palette;
import concilia.ui.Ui;

/**
 * One round council node on the journey map: number / check / lock, era-coloured border,
 * soft glow, and the two pulsing rings around the ACTIVE council.
 * The view is 112dp square so the rings can grow without being clipped; the circle itself is 62dp.
 */
public class NodeView extends View {
    public static final int SIZE_DP = 112;
    private static final float R = 31f;          // circle radius (62dp node)

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final float dp;

    private int color = Palette.GOLD;
    private int number;
    private boolean completed, active, locked;

    private float t1, t2;
    private ValueAnimator a1, a2;
    private Shader glow;

    public NodeView(Context ctx) {
        super(ctx);
        dp = Ui.density(ctx);
    }

    public void configure(int color, int number, boolean completed, boolean active, boolean locked) {
        this.color = color;
        this.number = number;
        this.completed = completed;
        this.active = active;
        this.locked = locked;
        this.glow = null;
        stopPulse();
        if (active && isAttachedToWindow()) startPulse();
        invalidate();
    }

    @Override protected void onMeasure(int w, int h) {
        int s = Math.round(SIZE_DP * dp);
        setMeasuredDimension(s, s);
    }

    @Override protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (active) startPulse();
    }

    @Override protected void onDetachedFromWindow() {
        stopPulse();
        super.onDetachedFromWindow();
    }

    private void startPulse() {
        stopPulse();
        a1 = pulse(0);
        a2 = pulse(400);
        a1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override public void onAnimationUpdate(ValueAnimator va) { t1 = (float) va.getAnimatedValue(); invalidate(); }
        });
        a2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override public void onAnimationUpdate(ValueAnimator va) { t2 = (float) va.getAnimatedValue(); invalidate(); }
        });
        a1.start();
        a2.start();
    }

    private ValueAnimator pulse(long delay) {
        ValueAnimator a = ValueAnimator.ofFloat(0f, 1f);
        a.setDuration(1000);                       // 0%->50% of the 2s CSS cycle
        a.setStartDelay(delay);
        a.setRepeatCount(ValueAnimator.INFINITE);
        a.setRepeatMode(ValueAnimator.REVERSE);
        a.setInterpolator(new AccelerateDecelerateInterpolator());
        return a;
    }

    private void stopPulse() {
        if (a1 != null) { a1.cancel(); a1 = null; }
        if (a2 != null) { a2.cancel(); a2 = null; }
        t1 = t2 = 0f;
    }

    @Override protected void onDraw(Canvas c) {
        float cx = getWidth() / 2f, cy = getHeight() / 2f;
        float r = R * dp;

        // pulsing rings (active only)
        if (active) {
            drawRing(c, cx, cy, 41f * dp, 2f * dp, t1);
            drawRing(c, cx, cy, 49f * dp, 1.5f * dp, t2);
        }

        // glow
        if (completed || active) {
            float blur = (completed ? 18f : 12f) * dp;
            int a = completed ? 0x55 : 0x44;
            if (glow == null) {
                glow = new RadialGradient(cx, cy, r + blur,
                        new int[]{Ui.alpha(color, a), Ui.alpha(color, a), Ui.alpha(color, 0)},
                        new float[]{0f, r / (r + blur), 1f}, Shader.TileMode.CLAMP);
            }
            paint.setStyle(Paint.Style.FILL);
            paint.setShader(glow);
            c.drawCircle(cx, cy, r + blur, paint);
            paint.setShader(null);
        }

        // fill
        int fill = completed ? color : active ? Palette.CARD : Palette.CARD_LOCKED;
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(fill);
        c.drawCircle(cx, cy, r, paint);

        // border (3dp, drawn inside the circle like CSS border-box)
        int border = completed ? color : active ? color : locked ? Palette.BORDER : Palette.NODE_BORDER;
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3f * dp);
        paint.setColor(border);
        c.drawCircle(cx, cy, r - 1.5f * dp, paint);

        // label
        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.CENTER);
        if (completed) {
            paint.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
            paint.setTextSize(22f * dp);
            paint.setColor(Palette.BACKGROUND);
            drawCentered(c, "✓", cx, cy);
        } else if (locked) {
            paint.setTypeface(android.graphics.Typeface.DEFAULT);
            paint.setTextSize(20f * dp);
            paint.setColor(0xFFFFFFFF);
            paint.setAlpha(Math.round(255 * 0.35f));
            drawCentered(c, "🔒", cx, cy);
        } else {
            paint.setTypeface(Fonts.inter(getContext(), 800));
            paint.setTextSize(13f * dp);
            paint.setColor(color);
            drawCentered(c, String.valueOf(number), cx, cy);
        }
        paint.setAlpha(255);
    }

    private void drawRing(Canvas c, float cx, float cy, float outerR, float stroke, float t) {
        float alpha = 0.5f + (0.2f - 0.5f) * t;
        float scale = 1f + 0.08f * t;
        c.save();
        c.scale(scale, scale, cx, cy);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(stroke);
        paint.setColor(color);
        paint.setAlpha(Math.round(alpha * 255));
        c.drawCircle(cx, cy, outerR - stroke / 2f, paint);
        c.restore();
        paint.setAlpha(255);
    }

    private void drawCentered(Canvas c, String s, float cx, float cy) {
        float y = cy - (paint.ascent() + paint.descent()) / 2f;
        c.drawText(s, cx, y, paint);
    }
}
