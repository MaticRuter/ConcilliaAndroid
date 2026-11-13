package concilia.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

import concilia.ui.SvgDraw;

/**
 * The little crusader that stands on the timeline (CrusaderKnight.tsx).
 * Drawn from the original SVG shapes in an 80 x 120 box; the sword blade pokes below
 * y=120, so the view is a little taller than the SVG box to avoid clipping it.
 */
public class CrusaderKnightView extends View {
    private static final float VB_W = 80f;
    private static final float VB_H = 142f; // 120 + room for the blade (it ends at y=140)

    private final float sizePx;
    private boolean facingLeft;

    /** @param sizePx  width in pixels (Figma: size=64 -> 64dp wide, 96dp tall body) */
    public CrusaderKnightView(Context ctx, int sizePx) {
        super(ctx);
        this.sizePx = sizePx;
    }

    public void setFacingLeft(boolean left) {
        if (left != facingLeft) { facingLeft = left; invalidate(); }
    }

    @Override protected void onMeasure(int wSpec, int hSpec) {
        setMeasuredDimension(Math.round(sizePx), Math.round(sizePx * VB_H / VB_W));
    }

    @Override protected void onDraw(Canvas canvas) {
        float s = sizePx / VB_W;
        canvas.save();
        if (facingLeft) canvas.scale(-1f, 1f, getWidth() / 2f, 0f);
        canvas.scale(s, s);
        SvgDraw g = new SvgDraw(canvas);

        // shadow
        g.ellipse(40, 118, 22, 5, "#000000", 0.18f);

        // back leg
        g.rect(33, 82, 10, 22, 4, "#8A8A8A", 1f);
        g.rect(32, 100, 12, 8, 3, "#6A6A6A", 1f);
        g.path("M 31 106 Q 30 112 28 114 L 43 114 L 44 108 Z", "#3A2A1A", 1f);
        g.rect(27, 112, 16, 3, 1.5f, "#2A1A0A", 1f);

        // surcoat
        g.path("M 20 58 Q 18 80 20 95 L 60 95 Q 62 80 60 58 Q 50 52 40 52 Q 30 52 20 58Z", "#F5F0E8", 1f);
        g.strokePath("M 20 58 Q 19 75 20 95", "#D8D0C0", 1.5f, 1f);
        g.strokePath("M 60 58 Q 61 75 60 95", "#D8D0C0", 1.5f, 1f);

        // red cross
        g.rect(36, 60, 8, 26, 2, "#CC2222", 1f);
        g.rect(26, 70, 28, 8, 2, "#CC2222", 1f);

        // belt + buckle
        g.rect(19, 82, 42, 6, 3, "#5A3A1A", 1f);
        g.rect(36, 82, 8, 6, 2, "#8A6A2A", 1f);
        g.rect(37, 83, 6, 4, 1, "#C9A84C", 1f);

        // chain mail on the sides
        g.path("M 20 60 Q 14 65 13 80 L 20 80Z", "#A0A0A0", 1f);
        g.path("M 60 60 Q 66 65 67 80 L 60 80Z", "#A0A0A0", 1f);
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 2; col++) {
                g.circle(14 + col * 3, 63 + row * 5, 1.2f, "#888888", 0.5f);
                g.circle(63 + col * 3, 63 + row * 5, 1.2f, "#888888", 0.5f);
            }
        }

        // front leg
        g.rect(37, 82, 10, 22, 4, "#9A9A9A", 1f);
        g.rect(36, 100, 12, 8, 3, "#7A7A7A", 1f);
        g.path("M 35 106 Q 34 112 32 114 L 47 114 L 48 108 Z", "#3A2A1A", 1f);
        g.rect(31, 112, 16, 3, 1.5f, "#2A1A0A", 1f);

        // sword arm
        g.rect(57, 56, 9, 18, 4, "#9A9A9A", 1f);
        g.ellipse(61, 58, 8, 5, "#C0C0C0", 1f);
        g.rect(58, 72, 8, 16, 3, "#888888", 1f);
        g.rect(57, 86, 10, 8, 3, "#7A7A7A", 1f);
        g.rect(61, 90, 4, 22, 2, "#6A4A1A", 1f);
        g.rect(55, 90, 16, 4, 2, "#C9A84C", 1f);
        g.path("M 62 112 L 61 140 L 63 140 Z", "#D0D0D8", 1f);
        g.strokePath("M 62 112 L 63 140", "#A0A0B0", 0.5f, 1f);

        // shield arm
        g.rect(14, 56, 9, 18, 4, "#9A9A9A", 1f);
        g.ellipse(19, 58, 8, 5, "#C0C0C0", 1f);
        g.path("M 4 64 L 4 80 Q 4 90 14 96 Q 14 90 16 80 L 16 64 Z", "#CC2222", 1f);
        g.rect(8, 66, 4, 22, 1.5f, "#F5F0E8", 1f);
        g.rect(4, 73, 12, 4, 1.5f, "#F5F0E8", 1f);
        g.strokePath("M 4 64 L 4 80 Q 4 90 14 96 Q 14 90 16 80 L 16 64 Z", "#8A1A1A", 1.5f, 1f);

        // coif (neck)
        g.rect(31, 43, 18, 16, 6, "#A0A0A0", 1f);
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                g.circle(33 + col * 4, 45 + row * 4, 1f, "#888888", 0.5f);
            }
        }

        // great helm
        g.rect(24, 24, 32, 28, 6, "#C0C0C0", 1f);
        g.ellipse(40, 26, 16, 8, "#D0D0D0", 1f);
        g.rect(27, 36, 26, 4, 2, "#1A1208", 1f);
        g.rect(28, 36, 24, 1.5f, 1, "#2A1A0A", 0.5f);
        g.circle(30, 44, 1.5f, "#1A1208", 1f);
        g.circle(30, 48, 1.5f, "#1A1208", 1f);
        g.circle(34, 46, 1.5f, "#1A1208", 1f);
        g.circleStroke(26, 28, 1.5f, "#A0A0A0", "#888888", 0.5f);
        g.circleStroke(54, 28, 1.5f, "#A0A0A0", "#888888", 0.5f);
        g.circleStroke(26, 48, 1.5f, "#A0A0A0", "#888888", 0.5f);
        g.circleStroke(54, 48, 1.5f, "#A0A0A0", "#888888", 0.5f);
        g.rect(38, 28, 4, 20, 2, "#B0B0B0", 1f);

        // plume
        g.path("M 40 20 Q 36 10 32 4 Q 36 8 40 16 Q 44 8 48 4 Q 44 10 40 20Z", "#CC2222", 0.9f);
        g.strokePath("M 40 20 Q 38 12 35 6", "#AA1818", 1f, 0.6f);
        g.strokePath("M 40 20 Q 42 12 45 6", "#AA1818", 1f, 0.6f);

        canvas.restore();
    }
}
