package io.github.ghhggg123456.mapengine.shader;

public final class ShaderState {
    private static volatile float pixelSize = 1f;
    private static volatile float tintR = 1f, tintG = 1f, tintB = 1f;
    private static volatile float satR = 1f, satG = 1f, satB = 1f;
    private static volatile float hue = 0f;
    private static volatile float contrast = 1f;
    private static volatile float brightness = 1f;
    private static volatile int   posterize = 0;
    private static volatile int   noiseSize = 0, noiseRate = 0;
    private static volatile float blur = 0f;
    private static volatile float vigR = 0f, vigG = 0f, vigB = 0f, vignetteSize = 0f;
    private static volatile float outR = 1f, outG = 0f, outB = 0f, outlineSize = 0f;
    private static volatile float chromaticStrength = 0f;
    private static volatile float motionStrength = 0f;

    private ShaderState() {}

    public static float pixelSize()    { return pixelSize; }
    public static float tintR()        { return tintR; }
    public static float tintG()        { return tintG; }
    public static float tintB()        { return tintB; }
    public static float satR()         { return satR; }
    public static float satG()         { return satG; }
    public static float satB()         { return satB; }
    public static float hue()          { return hue; }
    public static float contrast()     { return contrast; }
    public static float brightness()   { return brightness; }
    public static int   posterize()    { return posterize; }
    public static int   noiseSize()    { return noiseSize; }
    public static int   noiseRate()    { return noiseRate; }
    public static float blur()         { return blur; }
    public static float vigR()         { return vigR; }
    public static float vigG()         { return vigG; }
    public static float vigB()         { return vigB; }
    public static float vignetteSize() { return vignetteSize; }
    public static float outR()         { return outR; }
    public static float outG()         { return outG; }
    public static float outB()         { return outB; }
    public static float outlineSize()  { return outlineSize; }
    public static float chromaticStrength() { return chromaticStrength; }
    public static float motionStrength()    { return motionStrength; }

    public static boolean isAllNeutral() {
        return pixelSize <= 1.0f
            && tintR == 1f && tintG == 1f && tintB == 1f
            && satR == 1f && satG == 1f && satB == 1f
            && hue == 0f
            && contrast == 1f
            && brightness == 1f
            && posterize <= 1
            && noiseSize <= 0
            && blur <= 0f
            && vignetteSize <= 0f
            && outlineSize <= 0f
            && chromaticStrength <= 0f
            && motionStrength <= 0f;
    }

    public static void reset() {
        pixelSize = 1f;
        tintR = tintG = tintB = 1f;
        satR = satG = satB = 1f;
        hue = 0f;
        contrast = 1f;
        brightness = 1f;
        posterize = 0;
        noiseSize = 0; noiseRate = 0;
        blur = 0f;
        vigR = vigG = vigB = 0f; vignetteSize = 0f;
        outR = 1f; outG = 0f; outB = 0f; outlineSize = 0f;
        chromaticStrength = 0f;
        motionStrength = 0f;
    }

    public static void load(ShaderPayload p) {
        pixelSize = Math.max(1f, p.pixelSize());
        tintR = p.tintR(); tintG = p.tintG(); tintB = p.tintB();
        satR = p.satR(); satG = p.satG(); satB = p.satB();
        hue = p.hue();
        contrast = p.contrast();
        brightness = p.brightness();
        posterize = p.posterize();
        noiseSize = p.noiseSize();
        noiseRate = p.noiseRate();
        blur = Math.max(0f, p.blur());
        vigR = p.vigR(); vigG = p.vigG(); vigB = p.vigB();
        vignetteSize = p.vignetteSize();
        outR = p.outR(); outG = p.outG(); outB = p.outB();
        outlineSize = p.outlineSize();
        chromaticStrength = p.chromaticStrength();
        motionStrength = p.motionStrength();
    }
}