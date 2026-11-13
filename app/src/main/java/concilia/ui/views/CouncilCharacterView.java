package concilia.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

import concilia.ui.SvgDraw;

/**
 * The bishop who talks to you in the quiz (CouncilCharacter.tsx).
 * Robe colours depend on the era, face depends on the mood.
 */
public class CouncilCharacterView extends View {
    public static final int NEUTRAL = 0, HAPPY = 1, SAD = 2, THINKING = 3;

    private static final float VB_W = 200f;
    private static final float VB_H = 284f; // 280 + shadow at the bottom

    private final float sizePx;
    private String era = "Patristic Age";
    private int mood = NEUTRAL;

    public CouncilCharacterView(Context ctx, int sizePx) {
        super(ctx);
        this.sizePx = sizePx;
    }

    public void setEra(String era) { this.era = era; invalidate(); }

    public void setMood(int mood) {
        if (mood != this.mood) { this.mood = mood; invalidate(); }
    }

    @Override protected void onMeasure(int w, int h) {
        setMeasuredDimension(Math.round(sizePx), Math.round(sizePx * VB_H / VB_W));
    }

    /** {robe, trim, mitre} per era - straight from ROBE_COLORS. */
    private static String[] colors(String era) {
        switch (era) {
            case "Byzantine Age":   return new String[]{"#2A1A4A", "#9B6FD4", "#D4B8F0"};
            case "Medieval Church": return new String[]{"#1A3D2B", "#4FC878", "#C8EDD8"};
            case "Late Medieval":   return new String[]{"#3D1A1A", "#E63946", "#FFD6D6"};
            case "Reformation Era": return new String[]{"#1A2A3D", "#4A90D9", "#D6E8FF"};
            case "Modern Church":   return new String[]{"#1A2530", "#7FBBCC", "#E0F4F8"};
            default:                return new String[]{"#3D2B1A", "#C9A84C", "#E8DCC8"};
        }
    }

    @Override protected void onDraw(Canvas canvas) {
        float s = sizePx / VB_W;
        canvas.save();
        canvas.scale(s, s);
        SvgDraw g = new SvgDraw(canvas);
        String[] col = colors(era);
        String robe = col[0], trim = col[1], mitre = col[2];
        String skin = "#D4A87A";

        // shadow
        g.ellipse(100, 275, 55, 8, "#000000", 0.2f);

        // robe
        g.path("M 60 220 Q 50 260 45 275 L 155 275 Q 150 260 140 220 Q 130 200 100 198 Q 70 200 60 220Z", robe, 1f);
        g.path("M 88 200 Q 88 210 86 225 L 92 225 Q 94 212 100 210 Q 106 212 108 225 L 114 225 Q 112 210 112 200Z", trim, 0.8f);
        g.strokePath("M 70 230 Q 68 250 65 270", trim, 1.5f, 0.3f);
        g.strokePath("M 130 230 Q 132 250 135 270", trim, 1.5f, 0.3f);

        // sleeves + hands
        g.path("M 60 220 Q 40 230 30 240 Q 32 248 38 250 Q 50 244 64 232Z", robe, 1f);
        g.ellipse(34, 248, 8, 6, skin, 1f);
        g.path("M 140 220 Q 160 230 170 240 Q 168 248 162 250 Q 150 244 136 232Z", robe, 1f);
        g.ellipse(166, 248, 8, 6, skin, 1f);

        // collar + pectoral cross
        g.ellipse(100, 200, 28, 12, robe, 1f);
        g.ellipse(100, 199, 24, 10, trim, 0.5f);
        g.rect(97, 192, 6, 18, 2, trim, 1f);
        g.rect(91, 197, 18, 6, 2, trim, 1f);

        // neck
        g.rect(90, 186, 20, 16, 6, skin, 1f);

        // head
        g.ellipse(100, 155, 36, 38, skin, 1f);
        // beard
        g.path("M 72 170 Q 70 188 100 192 Q 130 188 128 170 Q 118 178 100 179 Q 82 178 72 170Z", "#9A7A5A", 0.9f);
        g.strokePath("M 85 174 Q 100 180 115 174", "#C4A07A", 1.5f, 0.5f);

        // ears
        g.ellipse(64, 152, 7, 9, skin, 1f);
        g.ellipse(136, 152, 7, 9, skin, 1f);

        // nose
        g.strokePath("M 97 152 Q 94 162 97 165 Q 100 167 103 165 Q 106 162 103 152", "#B8855A", 1.5f, 1f);

        drawEyes(g);
        drawMouth(g);

        // mitre
        g.rect(67, 118, 66, 14, 4, trim, 0.8f);
        g.path("M 70 118 Q 68 88 80 65 Q 88 50 95 48 Q 100 46 100 46 L 100 118Z", mitre, 1f);
        g.path("M 130 118 Q 132 88 120 65 Q 112 50 105 48 Q 100 46 100 46 L 100 118Z", mitre, 1f);
        g.line(100, 46, 100, 118, trim, 2f, 0.4f);
        g.rect(97, 72, 6, 22, 1.5f, trim, 0.7f);
        g.rect(91, 79, 18, 6, 1.5f, trim, 0.7f);
        g.rect(67, 128, 10, 28, 4, trim, 0.6f);
        g.rect(123, 128, 10, 28, 4, trim, 0.6f);

        canvas.restore();
    }

    private void drawEyes(SvgDraw g) {
        String ink = "#2A1A0A";
        switch (mood) {
            case HAPPY:
                g.strokePath("M 78 148 Q 84 142 90 148", ink, 2.5f, 1f);
                g.strokePath("M 110 148 Q 116 142 122 148", ink, 2.5f, 1f);
                break;
            case SAD:
                g.ellipse(84, 149, 5, 4, ink, 1f);
                g.ellipse(116, 149, 5, 4, ink, 1f);
                g.strokePath("M 78 142 Q 84 145 90 143", ink, 2f, 1f);
                g.strokePath("M 110 143 Q 116 145 122 142", ink, 2f, 1f);
                break;
            case THINKING:
                g.ellipse(84, 149, 5, 4.5f, ink, 1f);
                g.ellipse(116, 149, 5, 4.5f, ink, 1f);
                g.strokePath("M 78 142 Q 84 138 90 141", ink, 2f, 1f);
                g.strokePath("M 110 141 Q 116 141 122 141", ink, 2f, 1f);
                break;
            default:
                g.ellipse(84, 149, 5, 5, ink, 1f);
                g.ellipse(116, 149, 5, 5, ink, 1f);
                g.ellipse(86, 147, 2, 2, "#FFFFFF", 0.5f);
                g.ellipse(118, 147, 2, 2, "#FFFFFF", 0.5f);
                break;
        }
    }

    private void drawMouth(SvgDraw g) {
        String ink = "#2A1A0A";
        switch (mood) {
            case HAPPY:
                g.strokePath("M 88 165 Q 100 174 112 165", ink, 2.5f, 1f);
                break;
            case SAD:
                g.strokePath("M 88 170 Q 100 163 112 170", ink, 2.5f, 1f);
                break;
            case THINKING:
                g.strokePath("M 92 167 Q 100 167 108 167", ink, 2f, 1f);
                g.circle(118, 140, 2.5f, "#C9A84C", 0.9f);
                g.circle(125, 133, 2f, "#C9A84C", 0.7f);
                g.circle(130, 127, 1.5f, "#C9A84C", 0.5f);
                break;
            default:
                g.strokePath("M 92 167 Q 100 170 108 167", ink, 2f, 1f);
                break;
        }
    }
}
