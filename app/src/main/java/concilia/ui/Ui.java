package concilia.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.LinearLayout;
import android.widget.TextView;

/** Small helpers so the screens read like the Figma/Tailwind markup. */
public final class Ui {
    private Ui() {}

    public static float density(Context c) { return c.getResources().getDisplayMetrics().density; }

    public static int dp(Context c, float v) { return Math.round(v * density(c)); }

    /** Replace the alpha channel (0-255) of a colour: the Figma's  color + '22'  trick. */
    public static int alpha(int color, int a) { return (color & 0x00FFFFFF) | ((a & 0xFF) << 24); }

    /** Tailwind-ish rounded box with optional border. */
    public static GradientDrawable box(Context c, int fill, int stroke, float strokeDp, float radiusDp) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(fill);
        d.setCornerRadius(dp(c, radiusDp));
        if (strokeDp > 0) d.setStroke(Math.max(1, dp(c, strokeDp)), stroke);
        return d;
    }

    public static GradientDrawable vGradient(int top, int bottom) {
        return new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{top, bottom});
    }

    public static TextView text(Context c, CharSequence s, float sp, int color, Typeface tf) {
        TextView t = new TextView(c);
        t.setText(s);
        t.setTextSize(sp);
        t.setTextColor(color);
        if (tf != null) t.setTypeface(tf);
        t.setIncludeFontPadding(false);
        return t;
    }

    /** CSS line-height as a multiple of font size (call AFTER size + typeface are set). */
    public static void lineHeight(TextView t, float ratio) {
        float target = t.getTextSize() * ratio;
        float natural = t.getPaint().getFontMetrics(null);
        t.setLineSpacing(target - natural, 1f);
    }

    /** CSS letter-spacing in em (tracking-widest = 0.1, tracking-wide = 0.025). */
    public static void tracking(TextView t, float em) {
        if (Build.VERSION.SDK_INT >= 21) t.setLetterSpacing(em);
    }

    public static LinearLayout.LayoutParams lp(int w, int h) { return new LinearLayout.LayoutParams(w, h); }

    public static LinearLayout.LayoutParams lpMargins(Context c, int w, int h,
                                                      float l, float t, float r, float b) {
        // w / h: -1 = MATCH_PARENT, -2 = WRAP_CONTENT, anything positive is in dp
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(w > 0 ? dp(c, w) : w, h > 0 ? dp(c, h) : h);
        p.setMargins(dp(c, l), dp(c, t), dp(c, r), dp(c, b));
        return p;
    }

    public static View spacer(Context c, float heightDp) {
        View v = new View(c);
        v.setLayoutParams(new LinearLayout.LayoutParams(1, dp(c, heightDp)));
        return v;
    }

    /** 1px horizontal divider (the Figma's border-b / border-t). */
    public static View hairline(Context c, int color) {
        View v = new View(c);
        v.setBackgroundColor(color);
        v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Math.max(1, dp(c, 1))));
        return v;
    }

    /** active:scale-95 / hover:scale feedback. */
    public static void pressScale(View v, final float scale) {
        v.setOnTouchListener(new View.OnTouchListener() {
            @Override public boolean onTouch(View view, MotionEvent e) {
                switch (e.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        view.animate().scaleX(scale).scaleY(scale).setDuration(90).start();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        view.animate().scaleX(1f).scaleY(1f).setDuration(140).start();
                        break;
                    default: break;
                }
                return false; // let the click through
            }
        });
    }

    /** Big call-to-action button with a coloured glow (box-shadow: 0 4px 24px colour33). */
    public static TextView glowButton(Context c, String label, int bg, int fg, float sp,
                                      Typeface tf, float radiusDp, float tracking, int glowColor) {
        TextView b = text(c, label, sp, fg, tf);
        b.setGravity(android.view.Gravity.CENTER);
        b.setPadding(dp(c, 16), dp(c, 16), dp(c, 16), dp(c, 16));
        b.setBackground(box(c, bg, 0, 0, radiusDp));
        tracking(b, tracking);
        if (Build.VERSION.SDK_INT >= 21) b.setElevation(dp(c, 8));
        if (Build.VERSION.SDK_INT >= 28) {
            b.setOutlineSpotShadowColor(glowColor);
            b.setOutlineAmbientShadowColor(glowColor);
        }
        b.setClickable(true);
        pressScale(b, 0.97f);
        return b;
    }

    /** Pads the root by the system bars so content never hides under the status/nav bar. */
    public static void applyInsets(final View root) {
        root.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override public WindowInsets onApplyWindowInsets(View v, WindowInsets in) {
                v.setPadding(in.getSystemWindowInsetLeft(), in.getSystemWindowInsetTop(),
                        in.getSystemWindowInsetRight(), in.getSystemWindowInsetBottom());
                return in.consumeSystemWindowInsets();
            }
        });
    }
}
