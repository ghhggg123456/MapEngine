package io.github.ghhggg123456.mapengine.tooltip;

public final class TooltipColors {
    public static final int DEFAULT_BG     = 0xF0100010; // ваниль-ish
    public static final int DEFAULT_BORDER = 0x505000FF;

    private static int background = DEFAULT_BG;
    private static int borderStart = DEFAULT_BORDER;
    private static int borderEnd = DEFAULT_BORDER;

    private TooltipColors() {}

    public static int background() { return background; }
    public static int borderStart() { return borderStart; }
    public static int borderEnd() { return borderEnd; }

    public static void set(int bg, int bs, int be) {
        background = bg;
        borderStart = bs;
        borderEnd = be;
    }

    public static void reset() {
        background = DEFAULT_BG;
        borderStart = DEFAULT_BORDER;
        borderEnd = DEFAULT_BORDER;
    }
}