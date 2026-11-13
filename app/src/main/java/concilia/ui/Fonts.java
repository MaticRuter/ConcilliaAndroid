package concilia.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;

import java.util.HashMap;
import java.util.Map;

/**
 * Loads the three Figma typefaces from  app/src/main/assets/fonts/ .
 * If a file is missing the app does NOT crash - it falls back to a system font
 * (serif for Playfair / IM Fell, sans-serif for Inter), so you can add fonts later.
 *
 * Expected files (see SETUP_GUIDE.md):
 *   Inter-Regular/Medium/SemiBold/Bold/ExtraBold.otf   (already included)
 *   PlayfairDisplay-Regular.ttf, -SemiBold.ttf, -Bold.ttf
 *   IMFellEnglish-Regular.ttf,   IMFellEnglish-Italic.ttf
 */
public final class Fonts {
    private Fonts() {}

    private static final Map<String, Typeface> CACHE = new HashMap<>();

    private static Typeface load(Context ctx, String file, Typeface fallback) {
        Typeface cached = CACHE.get(file);
        if (cached != null) return cached;
        Typeface tf;
        try {
            tf = Typeface.createFromAsset(ctx.getApplicationContext().getAssets(), "fonts/" + file);
        } catch (Exception e) {
            tf = fallback;
        }
        CACHE.put(file, tf);
        return tf;
    }

    private static Typeface sys(String family, int weight, boolean italic) {
        if (Build.VERSION.SDK_INT >= 28) {
            return Typeface.create(Typeface.create(family, Typeface.NORMAL), weight, italic);
        }
        int style = weight >= 600 ? (italic ? Typeface.BOLD_ITALIC : Typeface.BOLD)
                : (italic ? Typeface.ITALIC : Typeface.NORMAL);
        return Typeface.create(family, style);
    }

    /** Inter. weight: 400, 500, 600, 700 or 800. */
    public static Typeface inter(Context ctx, int weight) {
        String file;
        switch (weight) {
            case 500: file = "Inter-Medium.otf"; break;
            case 600: file = "Inter-SemiBold.otf"; break;
            case 700: file = "Inter-Bold.otf"; break;
            case 800: file = "Inter-ExtraBold.otf"; break;
            default:  file = "Inter-Regular.otf"; break;
        }
        return load(ctx, file, sys("sans-serif", weight, false));
    }

    /** Playfair Display. weight: 400 (regular), 600 (semibold) or 700 (bold). */
    public static Typeface playfair(Context ctx, int weight) {
        if (weight >= 700) return load(ctx, "PlayfairDisplay-Bold.ttf", sys("serif", 700, false));
        if (weight >= 600) return load(ctx, "PlayfairDisplay-SemiBold.ttf", sys("serif", 600, false));
        return load(ctx, "PlayfairDisplay-Regular.ttf", sys("serif", 400, false));
    }

    /** IM Fell English (the "manuscript" body face). */
    public static Typeface fell(Context ctx, boolean italic) {
        return italic
                ? load(ctx, "IMFellEnglish-Italic.ttf", sys("serif", 400, true))
                : load(ctx, "IMFellEnglish-Regular.ttf", sys("serif", 400, false));
    }

    /** Georgia stand-in used for the big quotation mark on the lesson screen. */
    public static Typeface georgiaBold() {
        return Typeface.create("serif", Typeface.BOLD);
    }
}
