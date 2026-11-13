package concilia.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import android.view.View;

import concilia.ui.Palette;
import concilia.ui.Ui;

/** The 36dp square "‹" back button used in the lesson / quiz / profile headers. */
public class BackButtonView extends View {
    private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path chevron = new Path();
    private final float dp;
    private final float strokeDp;

    /** radiusDp: 8 on lesson/profile, 12 on the quiz. strokeDp: 1.5 or 2 (chevron thickness). */
    public BackButtonView(Context ctx, float radiusDp, float strokeDp) {
        super(ctx);
        dp = Ui.density(ctx);
        this.strokeDp = strokeDp;
        setBackground(Ui.box(ctx, Palette.CARD, Palette.BORDER, 1, radiusDp));
        setClickable(true);
        setContentDescription("Back");
        Ui.pressScale(this, 0.92f);
    }

    @Override protected void onMeasure(int w, int h) {
        int s = Math.round(36 * dp);
        setMeasuredDimension(s, s);
    }

    @Override protected void onDraw(Canvas c) {
        // original 16x16 icon: M10 12 L6 8 L10 4, centred in the button
        float ox = (getWidth() - 16 * dp) / 2f, oy = (getHeight() - 16 * dp) / 2f;
        chevron.reset();
        chevron.moveTo(ox + 10 * dp, oy + 12 * dp);
        chevron.lineTo(ox + 6 * dp, oy + 8 * dp);
        chevron.lineTo(ox + 10 * dp, oy + 4 * dp);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeCap(Paint.Cap.ROUND);
        p.setStrokeJoin(Paint.Join.ROUND);
        p.setStrokeWidth(strokeDp * dp);
        p.setColor(Palette.MUTED);
        c.drawPath(chevron, p);
    }
}
