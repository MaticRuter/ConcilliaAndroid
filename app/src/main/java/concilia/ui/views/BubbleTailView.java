package concilia.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

import concilia.ui.Ui;

/** The little left-pointing triangle on the bishop's speech bubble (12 x 16 dp). */
public class BubbleTailView extends View {
    private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path path = new Path();
    private final float dp;

    public BubbleTailView(Context ctx) {
        super(ctx);
        dp = Ui.density(ctx);
    }

    public void setColor(int color) { p.setColor(color); invalidate(); }

    @Override protected void onMeasure(int w, int h) {
        setMeasuredDimension(Math.round(12 * dp), Math.round(16 * dp));
    }

    @Override protected void onDraw(Canvas c) {
        path.reset();
        path.moveTo(0, 8 * dp);
        path.lineTo(12 * dp, 0);
        path.lineTo(12 * dp, 16 * dp);
        path.close();
        p.setStyle(Paint.Style.FILL);
        c.drawPath(path, p);
    }
}
