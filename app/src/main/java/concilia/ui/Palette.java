package concilia.ui;

/** Colour tokens copied 1:1 from the Figma export (index.css + inline styles). */
public final class Palette {
    private Palette() {}

    public static final int BACKGROUND   = 0xFF0D0B08;
    public static final int FOREGROUND   = 0xFFE8DCC8; // main text
    public static final int CARD         = 0xFF1A1510;
    public static final int CARD_DARK    = 0xFF14110D; // quiz question box
    public static final int CARD_LOCKED  = 0xFF12100D; // locked / inactive node
    public static final int CARD_FADED   = 0xFF0F0D0A; // locked council label
    public static final int GOLD         = 0xFFC9A84C; // primary
    public static final int MUTED        = 0xFF8A7A62; // muted-foreground
    public static final int SOFT_TEXT    = 0xFFB8A88A; // secondary-foreground
    public static final int BORDER       = 0xFF2E261A;
    public static final int NODE_BORDER  = 0xFF3A3020; // unlocked-but-not-active node ring
    public static final int ACCENT       = 0xFF7B4F2E;

    // Quiz feedback colours
    public static final int OK_BG        = 0xFF1A3D2B;
    public static final int OK_BORDER    = 0xFF40916C;
    public static final int OK_TEXT      = 0xFFD8F3DC;
    public static final int OK_SOFT      = 0xFF95D5B2;
    public static final int OK_PANEL     = 0xFF162E22;
    public static final int OK_PANEL_BD  = 0xFF2D6A4F;
    public static final int BAD_BG       = 0xFF3D1A1A;
    public static final int BAD_BORDER   = 0xFFE63946;
    public static final int BAD_TEXT     = 0xFFFFD0D5;
    public static final int BAD_SOFT     = 0xFFFFBAC4;
    public static final int BAD_PANEL    = 0xFF2E1414;
    public static final int BAD_PANEL_BD = 0xFFC62828;
    public static final int DIM_TEXT     = 0xFF3A2E22; // un-chosen options after answering
    public static final int DIM_BORDER   = 0xFF1F1A12;
}
