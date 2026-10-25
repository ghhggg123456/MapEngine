package io.github.ghhggg123456.mapengine.shader;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public final class ShaderStateStore {
    private static final String K = "mapengine_shader_";

    private ShaderStateStore() {}

    public static void setTint(ServerPlayer p, float r, float g, float b) {
        CompoundTag t = p.getPersistentData();
        t.putFloat(K + "tintR", r); t.putFloat(K + "tintG", g); t.putFloat(K + "tintB", b);
    }
    public static void setSaturation(ServerPlayer p, float r, float g, float b) {
        CompoundTag t = p.getPersistentData();
        t.putFloat(K + "satR", r); t.putFloat(K + "satG", g); t.putFloat(K + "satB", b);
    }
    public static void setHue(ServerPlayer p, float v)        { p.getPersistentData().putFloat(K + "hue", v); }
    public static void setContrast(ServerPlayer p, float v)   { p.getPersistentData().putFloat(K + "contrast", v); }
    public static void setBrightness(ServerPlayer p, float v) { p.getPersistentData().putFloat(K + "brightness", v); }
    public static void setPosterize(ServerPlayer p, int v)    { p.getPersistentData().putInt(K + "posterize", v); }
    public static void setPixelize(ServerPlayer p, float v)   { p.getPersistentData().putFloat(K + "pixel", v); }
    public static void setNoise(ServerPlayer p, int size, int rate) {
        CompoundTag t = p.getPersistentData();
        t.putInt(K + "noiseSize", size); t.putInt(K + "noiseRate", rate);
    }
    public static void setBlur(ServerPlayer p, float v)       { p.getPersistentData().putFloat(K + "blur", v); }
    public static void setVignette(ServerPlayer p, float r, float g, float b, float size) {
        CompoundTag t = p.getPersistentData();
        t.putFloat(K + "vigR", r); t.putFloat(K + "vigG", g); t.putFloat(K + "vigB", b);
        t.putFloat(K + "vigSize", size);
    }
    public static void setOutline(ServerPlayer p, float r, float g, float b, float size) {
        CompoundTag t = p.getPersistentData();
        t.putFloat(K + "outR", r); t.putFloat(K + "outG", g); t.putFloat(K + "outB", b);
        t.putFloat(K + "outSize", size);
    }
    public static void setChromatic(ServerPlayer p, float v)  { p.getPersistentData().putFloat(K + "chromatic", v); }
    public static void setMotion(ServerPlayer p, float v)     { p.getPersistentData().putFloat(K + "motion", v); }

    public static void resetTint(ServerPlayer p)        { remove(p, "tintR", "tintG", "tintB"); }
    public static void resetSaturation(ServerPlayer p)  { remove(p, "satR", "satG", "satB"); }
    public static void resetHue(ServerPlayer p)         { remove(p, "hue"); }
    public static void resetContrast(ServerPlayer p)    { remove(p, "contrast"); }
    public static void resetBrightness(ServerPlayer p)  { remove(p, "brightness"); }
    public static void resetPosterize(ServerPlayer p)   { remove(p, "posterize"); }
    public static void resetPixelize(ServerPlayer p)    { remove(p, "pixel"); }
    public static void resetNoise(ServerPlayer p)       { remove(p, "noiseSize", "noiseRate"); }
    public static void resetBlur(ServerPlayer p)        { remove(p, "blur"); }
    public static void resetVignette(ServerPlayer p)    { remove(p, "vigR", "vigG", "vigB", "vigSize"); }
    public static void resetOutline(ServerPlayer p)     { remove(p, "outR", "outG", "outB", "outSize"); }
    public static void resetChromatic(ServerPlayer p)   { remove(p, "chromatic"); }
    public static void resetMotion(ServerPlayer p)      { remove(p, "motion"); }

    public static void reset(ServerPlayer p) {
        remove(p,
            "pixel", "tintR", "tintG", "tintB", "satR", "satG", "satB",
            "hue", "contrast", "brightness", "posterize",
            "noiseSize", "noiseRate", "blur",
            "vigR", "vigG", "vigB", "vigSize",
            "outR", "outG", "outB", "outSize",
            "chromatic", "motion");
    }

    private static void remove(ServerPlayer p, String... keys) {
        CompoundTag t = p.getPersistentData();
        for (String k : keys) t.remove(K + k);
    }

    public static ShaderPayload load(ServerPlayer p) {
        CompoundTag t = p.getPersistentData();
        return new ShaderPayload(
            t.contains(K + "pixel")       ? t.getFloat(K + "pixel") : 1f,
            t.contains(K + "tintR")       ? t.getFloat(K + "tintR") : 1f,
            t.contains(K + "tintG")       ? t.getFloat(K + "tintG") : 1f,
            t.contains(K + "tintB")       ? t.getFloat(K + "tintB") : 1f,
            t.contains(K + "satR")        ? t.getFloat(K + "satR") : 1f,
            t.contains(K + "satG")        ? t.getFloat(K + "satG") : 1f,
            t.contains(K + "satB")        ? t.getFloat(K + "satB") : 1f,
            t.contains(K + "hue")         ? t.getFloat(K + "hue") : 0f,
            t.contains(K + "contrast")    ? t.getFloat(K + "contrast") : 1f,
            t.contains(K + "brightness")  ? t.getFloat(K + "brightness") : 1f,
            t.contains(K + "posterize")   ? t.getInt  (K + "posterize") : 0,
            t.contains(K + "noiseSize")   ? t.getInt  (K + "noiseSize") : 0,
            t.contains(K + "noiseRate")   ? t.getInt  (K + "noiseRate") : 0,
            t.contains(K + "blur")        ? t.getFloat(K + "blur") : 0f,
            t.contains(K + "vigR")        ? t.getFloat(K + "vigR") : 0f,
            t.contains(K + "vigG")        ? t.getFloat(K + "vigG") : 0f,
            t.contains(K + "vigB")        ? t.getFloat(K + "vigB") : 0f,
            t.contains(K + "vigSize")     ? t.getFloat(K + "vigSize") : 0f,
            t.contains(K + "outR")        ? t.getFloat(K + "outR") : 1f,
            t.contains(K + "outG")        ? t.getFloat(K + "outG") : 0f,
            t.contains(K + "outB")        ? t.getFloat(K + "outB") : 0f,
            t.contains(K + "outSize")     ? t.getFloat(K + "outSize") : 0f,
            t.contains(K + "chromatic")   ? t.getFloat(K + "chromatic") : 0f,
            t.contains(K + "motion")      ? t.getFloat(K + "motion") : 0f
        );
    }
}