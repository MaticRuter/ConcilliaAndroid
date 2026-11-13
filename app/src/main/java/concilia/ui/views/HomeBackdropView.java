package concilia.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;

import concilia.ui.Palette;
import concilia.ui.Ui;

/**
 * Everything decorative on the front page (HomeScreen.tsx):
 * faint diamond texture, the big faint cross-in-a-circle, and the 21-dot timeline preview
 * along the bottom edge (first dot tall + gold, the rest small and dim).
 */
public class HomeBackdropView extends View {
    private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path diamond = new Path();
    private final RectF rect = new RectF();
    private final float dp;

    public HomeBackdropView(Context ctx) {
        super(ctx);
        dp = Ui.density(ctx);
    }

    @Override protected void onDraw(Canvas c) {
        float w = getWidth(), h = getHeight();
        p.setStyle(Paint.Style.FILL);

        // 1. diamond texture: 60px tile, 4% layer opacity x 30% fill opacity
        float tile = 60f * dp;
        diamond.reset();
        diamond.moveTo(tile / 2, 0);
        diamond.lineTo(tile, tile / 2);
        diamond.lineTo(tile / 2, tile);
        diamond.lineTo(0, tile / 2);
        diamond.close();
        p.setColor(Ui.alpha(Palette.GOLD, Math.round(255 * 0.04f * 0.3f)));
        for (float y = 0; y < h; y += tile) {
            for (float x = 0; x < w; x += tile) {
                c.save();
                c.translate(x, y);
                c.drawPath(diamond, p);
                c.restore();
            }
        }

        // 2. cross motif, 120dp, 10% opacity, 48dp from the top, centred
        float s = 120f * dp;
        float ox = (w - s) / 2f, oy = 48f * dp;
        float u = s / 120f;
        p.setColor(Ui.alpha(Palette.GOLD, Math.round(255 * 0.10f)));
        rect.set(ox + 52 * u, oy + 10 * u, ox + 68 * u, oy + 110 * u);
        c.drawRoundRect(rect, 2 * u, 2 * u, p);
        rect.set(ox + 10 * u, oy + 42 * u, ox + 110 * u, oy + 58 * u);
        c.drawRoundRect(rect, 2 * u, 2 * u, p);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(1f * u);
        c.drawCircle(ox + 60 * u, oy + 60 * u, 55 * u, p);

        // 3. timeline preview dots (6dp wide, 6dp gap, bottom aligned, 16dp bottom padding)
        p.setStyle(Paint.Style.FILL);
        float dotW = 6f * dp, gap = 6f * dp;
        float total = 21 * dotW + 20 * gap;
        float x = (w - total) / 2f;
        float bottom = h - 16f * dp;
        for (int i = 0; i < 21; i++) {
            float dh = (i == 0 ? 28f : 8f + (float) Math.sin(i * 0.7) * 4f) * dp;
            int col = i == 0 ? Palette.GOLD : Ui.alpha(Palette.BORDER, Math.round(255 * 0.6f));
            p.setColor(col);
            rect.set(x, bottom - dh, x + dotW, bottom);
            c.drawRoundRect(rect, dotW / 2f, dotW / 2f, p);
            x += dotW + gap;
        }
    }
}
