package concilia.ui.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

import concilia.ui.Palette;
import concilia.ui.Ui;

/** Rounded progress bar (quiz header, profile XP bar, era bars). Animates width changes. */
public class ProgressBarView extends View {
    private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF r = new RectF();
    private final float dp;
    private int fillColor;
    private int trackColor = Palette.CARD;
    private float progress;       // 0..1 currently drawn
    private float minProgress;    // quiz bar never shows less than 4%
    private boolean glow;
    private ValueAnimator anim;

    public ProgressBarView(Context ctx, int fillColor) {
        super(ctx);
        dp = Ui.density(ctx);
        this.fillColor = fillColor;
    }

    public ProgressBarView setTrackColor(int c) { trackColor = c; return this; }
    public ProgressBarView setMin(float m) { minProgress = m; return this; }
    public ProgressBarView setGlow(boolean g) { glow = g; return this; }

    /** Jump straight to a value. */
    public void setProgress(float target) {
        if (anim != null) anim.cancel();
        progress = clamp(target);
        invalidate();
    }

    /** Animate to a value (500ms, like transition-all duration-500). */
    public void animateTo(float target) {
        if (anim != null) anim.cancel();
        anim = ValueAnimator.ofFloat(progress, clamp(target));
        anim.setDuration(500);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override public void onAnimationUpdate(ValueAnimator a) {
                progress = (float) a.getAnimatedValue();
                invalidate();
            }
        });
        anim.start();
    }

    private static float clamp(float v) { return Math.max(0f, Math.min(1f, v)); }

    @Override protected void onDetachedFromWindow() {
        if (anim != null) anim.cancel();
        super.onDetachedFromWindow();
    }

    @Override protected void onDraw(Canvas c) {
        float w = getWidth(), h = getHeight(), rad = h / 2f;
        p.setStyle(Paint.Style.FILL);
        p.setColor(trackColor);
        r.set(0, 0, w, h);
        c.drawRoundRect(r, rad, rad, p);

        float fw = Math.max(minProgress, progress) * w;
        if (fw <= 0f) return;
        if (glow) {
            p.setColor(Ui.alpha(fillColor, 0x44));
            r.set(-2 * dp, -2 * dp, fw + 2 * dp, h + 2 * dp);
            c.drawRoundRect(r, rad + 2 * dp, rad + 2 * dp, p);
        }
        p.setColor(fillColor);
        r.set(0, 0, fw, h);
        c.drawRoundRect(r, rad, rad, p);
    }
}
