package concilia.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import java.util.HashMap;
import java.util.Map;

/**
 * Tiny helper that lets the Figma's SVG artwork be drawn on an Android Canvas almost verbatim.
 * Understands the absolute path commands the Figma SVGs use: M L Q C Z.
 * Work in the SVG's own viewBox units - scale the canvas first.
 */
public final class SvgDraw {
    private static final Map<String, Path> CACHE = new HashMap<>();

    private final Canvas canvas;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rect = new RectF();

    public SvgDraw(Canvas canvas) { this.canvas = canvas; }

    private static int c(String hex) { return Color.parseColor(hex); }

    private int apply(String hex, float opacity, Paint.Style style, float strokeW) {
        int col = c(hex);
        paint.setStyle(style);
        paint.setStrokeWidth(strokeW);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setColor(col);
        paint.setAlpha(Math.round(Color.alpha(col) * opacity));
        return col;
    }

    // ── shapes ──────────────────────────────────────────────────────────────
    public void rect(float x, float y, float w, float h, float r, String fill, float opacity) {
        apply(fill, opacity, Paint.Style.FILL, 0);
        rect.set(x, y, x + w, y + h);
        canvas.drawRoundRect(rect, r, r, paint);
    }

    public void ellipse(float cx, float cy, float rx, float ry, String fill, float opacity) {
        apply(fill, opacity, Paint.Style.FILL, 0);
        rect.set(cx - rx, cy - ry, cx + rx, cy + ry);
        canvas.drawOval(rect, paint);
    }

    public void circle(float cx, float cy, float r, String fill, float opacity) {
        apply(fill, opacity, Paint.Style.FILL, 0);
        canvas.drawCircle(cx, cy, r, paint);
    }

    public void circleStroke(float cx, float cy, float r, String fill, String stroke, float sw) {
        apply(fill, 1f, Paint.Style.FILL, 0);
        canvas.drawCircle(cx, cy, r, paint);
        apply(stroke, 1f, Paint.Style.STROKE, sw);
        canvas.drawCircle(cx, cy, r, paint);
    }

    public void line(float x1, float y1, float x2, float y2, String stroke, float sw, float opacity) {
        apply(stroke, opacity, Paint.Style.STROKE, sw);
        canvas.drawLine(x1, y1, x2, y2, paint);
    }

    public void path(String d, String fill, float opacity) {
        apply(fill, opacity, Paint.Style.FILL, 0);
        canvas.drawPath(parse(d), paint);
    }

    public void strokePath(String d, String stroke, float sw, float opacity) {
        apply(stroke, opacity, Paint.Style.STROKE, sw);
        canvas.drawPath(parse(d), paint);
    }

    // ── path parser ────────────────────────────────────────────────────────
    public static Path parse(String d) {
        Path cached = CACHE.get(d);
        if (cached != null) return cached;

        Path p = new Path();
        int i = 0, n = d.length();
        char cmd = 0;
        float[] a = new float[6];
        while (i < n) {
            char ch = d.charAt(i);
            if (ch == ' ' || ch == ',') { i++; continue; }
            if (Character.isLetter(ch)) {
                cmd = ch; i++;
                if (cmd == 'Z' || cmd == 'z') { p.close(); }
                continue;
            }
            int need = (cmd == 'M' || cmd == 'L') ? 2 : (cmd == 'Q') ? 4 : (cmd == 'C') ? 6 : 0;
            if (need == 0) { i++; continue; }
            for (int k = 0; k < need; k++) {
                while (i < n && (d.charAt(i) == ' ' || d.charAt(i) == ',')) i++;
                int s = i;
                if (i < n && d.charAt(i) == '-') i++;
                while (i < n && (Character.isDigit(d.charAt(i)) || d.charAt(i) == '.')) i++;
                a[k] = Float.parseFloat(d.substring(s, i));
            }
            switch (cmd) {
                case 'M': p.moveTo(a[0], a[1]); cmd = 'L'; break; // extra pairs after M are lineTo
                case 'L': p.lineTo(a[0], a[1]); break;
                case 'Q': p.quadTo(a[0], a[1], a[2], a[3]); break;
                case 'C': p.cubicTo(a[0], a[1], a[2], a[3], a[4], a[5]); break;
                default: break;
            }
        }
        CACHE.put(d, p);
        return p;
    }
}
