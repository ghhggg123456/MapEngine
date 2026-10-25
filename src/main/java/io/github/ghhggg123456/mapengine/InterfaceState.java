package io.github.ghhggg123456.mapengine.ui;

public final class InterfaceState {
    public static final int     DEF_COLOR   = 0xFFFFFF;
    public static final int     DEF_OPACITY = 100;
    public static final boolean DEF_VISIBLE = true;

    private static final int N = InterfaceElement.values().length;
    private static final int[] colors       = new int[N];
    private static final int[] opacities    = new int[N];
    private static final boolean[] visibles = new boolean[N];

    static { resetAll(); }

    private InterfaceState() {}

    public static int color(InterfaceElement e) { return colors[e.ordinal()]; }
    public static int opacity(InterfaceElement e) { return opacities[e.ordinal()]; }
    public static boolean visible(InterfaceElement e) { return visibles[e.ordinal()]; }

    public static boolean isCustomized(InterfaceElement e) {
        int i = e.ordinal();
        return colors[i] != DEF_COLOR
            || opacities[i] != DEF_OPACITY
            || visibles[i] != DEF_VISIBLE;
    }

    public static boolean isFullyInvisible(InterfaceElement e) {
        return !visibles[e.ordinal()] || opacities[e.ordinal()] == 0;
    }

    public static void setColor(InterfaceElement e, int rgb) { colors[e.ordinal()] = rgb & 0xFFFFFF; }
    public static void setOpacity(InterfaceElement e, int pct) { opacities[e.ordinal()] = Math.max(0, Math.min(100, pct)); }
    public static void setVisible(InterfaceElement e, boolean v) { visibles[e.ordinal()] = v; }

    public static void reset(InterfaceElement e) {
        int i = e.ordinal();
        colors[i]    = DEF_COLOR;
        opacities[i] = DEF_OPACITY;
        visibles[i]  = DEF_VISIBLE;
    }

    public static void resetAll() {
        for (InterfaceElement e : InterfaceElement.values()) {
            reset(e);
        }
    }

    public static float[] rgba(InterfaceElement e) {
        int c   = colors[e.ordinal()];
        int pct = opacities[e.ordinal()];
        return new float[] {
            ((c >> 16) & 0xFF) / 255f,
            ((c >>  8) & 0xFF) / 255f,
            ( c        & 0xFF) / 255f,
            pct / 100f
        };
    }

    public static int alpha(InterfaceElement e) {
        int pct = opacities[e.ordinal()];
        if (pct >= 100) return 255;
        if (pct <= 0)   return 0;
        return pct * 255 / 100;
    }

    public static int textColor(InterfaceElement e) {
        int c = colors[e.ordinal()];
        int a = alpha(e);
        if (a == 0) a = 1;
        return (a << 24) | c;
    }

    public static void load(int[] c, int[] o, boolean[] v) {
        int n = Math.min(N, c.length);
        for (int i = 0; i < n; i++) {
            colors[i]    = c[i] & 0xFFFFFF;
            opacities[i] = Math.max(0, Math.min(100, o[i]));
            visibles[i]  = v[i];
        }
    }
}