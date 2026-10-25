package io.github.ghhggg123456.mapengine.shader;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EventBusSubscriber(modid = "mapengine", value = Dist.CLIENT)
public final class ShaderGuard {

    public static final ResourceLocation CHAIN =
        ResourceLocation.fromNamespaceAndPath("mapengine", "shaders/post/shader_chain.json");

    private static final String[] UNIFORM_NAMES = {
        "Radius", "PixelSize", "TintColor", "Saturation", "Hue",
        "Contrast", "Brightness", "Posterize", "NoiseSize", "NoiseRate",
        "Chromatic", "Blur", "MotionStrength", "MotionX", "MotionY",
        "VignetteColor", "VignetteSize", "OutlineColor", "OutlineSize",
        "GameTime"
    };

    private static PostChain cachedChain = null;
    private static final Map<String, List<Object>> UNIFORMS = new HashMap<>();
    private static boolean firstFrameAfterLoad = false;
    private static long chainStartMs = 0L;

    private static boolean haveLastCam = false;
    private static float lastYaw = 0f, lastPitch = 0f;
    private static float motionX = 0f, motionY = 0f;

    @SubscribeEvent
    public static void onStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_WEATHER) return;

        var gr = Minecraft.getInstance().gameRenderer;

        if (ShaderState.isAllNeutral()) {
            if (gr.currentEffect() != null) {
                gr.shutdownEffect();
                cachedChain = null;
                UNIFORMS.clear();
                firstFrameAfterLoad = false;
                chainStartMs = 0L;
            }
            return;
        }

        if (gr.currentEffect() == null) {
            gr.loadEffect(CHAIN);
            PostChain chain = gr.currentEffect();
            if (chain != null) {
                cachedChain = chain;
                cacheUniforms(chain);
                firstFrameAfterLoad = true;
                chainStartMs = System.currentTimeMillis();
                updateCameraVelocity();
                pushUniforms();
            }
            return;
        }

        PostChain chain = gr.currentEffect();
        if (chain != cachedChain) {
            cachedChain = chain;
            cacheUniforms(chain);
            firstFrameAfterLoad = true;
            chainStartMs = System.currentTimeMillis();
        }
        updateCameraVelocity();
        pushUniforms();
    }

    @SubscribeEvent
    public static void onLogout(ClientPlayerNetworkEvent.LoggingOut e) {
        ShaderState.reset();
        cachedChain = null;
        UNIFORMS.clear();
        firstFrameAfterLoad = false;
        chainStartMs = 0L;
        haveLastCam = false;
    }

    public static void handle(ShaderPayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> ShaderState.load(payload));
    }

    private static void updateCameraVelocity() {
        Camera cam = Minecraft.getInstance().gameRenderer.getMainCamera();
        float yaw = cam.getYRot();
        float pitch = cam.getXRot();

        if (!haveLastCam) {
            lastYaw = yaw;
            lastPitch = pitch;
            haveLastCam = true;
            motionX = 0f;
            motionY = 0f;
            return;
        }

        motionX = (yaw - lastYaw) / 90f;
        motionY = (lastPitch - pitch) / 90f;
        lastYaw = yaw;
        lastPitch = pitch;
    }

    private static void cacheUniforms(PostChain chain) {
        UNIFORMS.clear();
        try {
            Object passesObj = tryGetField(chain, "passes", "postPasses", "effectPasses");
            if (!(passesObj instanceof List<?> passes)) return;

            for (Object pass : passes) {
                Object eff = invokeNoArg(pass, "getEffect");
                if (eff == null) eff = tryGetField(pass, "effect", "shaderEffect");
                if (eff == null) continue;

                for (String name : UNIFORM_NAMES) {
                    Object u = tryInvoke(eff, "getUniform", name);
                    if (u != null) {
                        UNIFORMS.computeIfAbsent(name, k -> new ArrayList<>()).add(u);
                    }
                }
            }
        } catch (Throwable ignored) {}
    }

    private static void pushUniforms() {
        float gameTime = chainStartMs == 0L
            ? 0f
            : (System.currentTimeMillis() - chainStartMs) / 1000f;

        setFloatAll("GameTime",       gameTime);
        setFloatAll("Radius",         ShaderState.blur() * 0.16f);
        setFloatAll("PixelSize",      ShaderState.pixelSize());
        setVec3All ("TintColor",      ShaderState.tintR(), ShaderState.tintG(), ShaderState.tintB());
        setVec3All ("Saturation",     ShaderState.satR(),  ShaderState.satG(),  ShaderState.satB());
        setFloatAll("Hue",            ShaderState.hue());
        setFloatAll("Contrast",       ShaderState.contrast());
        setFloatAll("Brightness",     ShaderState.brightness());
        setFloatAll("Posterize",      (float) ShaderState.posterize());
        setFloatAll("NoiseSize",      (float) ShaderState.noiseSize());
        setFloatAll("NoiseRate",      (float) ShaderState.noiseRate());
        setFloatAll("Chromatic",      ShaderState.chromaticStrength());
        setFloatAll("Blur",           ShaderState.blur());

        float motionStr = firstFrameAfterLoad ? 0f : ShaderState.motionStrength();
        setFloatAll("MotionStrength", motionStr);
        setFloatAll("MotionX",        motionX);
        setFloatAll("MotionY",        motionY);
        firstFrameAfterLoad = false;

        setVec3All ("VignetteColor",  ShaderState.vigR(), ShaderState.vigG(), ShaderState.vigB());
        setFloatAll("VignetteSize",   ShaderState.vignetteSize());
        setVec3All ("OutlineColor",   ShaderState.outR(), ShaderState.outG(), ShaderState.outB());
        setFloatAll("OutlineSize",    ShaderState.outlineSize());
    }

    private static void setFloatAll(String name, float v) {
        List<Object> list = UNIFORMS.get(name);
        if (list == null) return;
        for (Object u : list) applySet(u, v);
    }

    private static void setVec3All(String name, float x, float y, float z) {
        List<Object> list = UNIFORMS.get(name);
        if (list == null) return;
        for (Object u : list) applyVec3(u, x, y, z);
    }

    private static boolean applySet(Object uniform, float v) {
        Class<?> cls = uniform.getClass();
        while (cls != null) {
            for (Method m : cls.getDeclaredMethods()) {
                if (!m.getName().equals("set")) continue;
                Class<?>[] p = m.getParameterTypes();
                try {
                    if (p.length == 1 && p[0] == float.class) {
                        m.setAccessible(true);
                        m.invoke(uniform, v);
                        return true;
                    }
                } catch (Throwable ignored) {}
            }
            cls = cls.getSuperclass();
        }
        return false;
    }

    private static boolean applyVec3(Object uniform, float x, float y, float z) {
        Class<?> cls = uniform.getClass();
        while (cls != null) {
            for (Method m : cls.getDeclaredMethods()) {
                if (!m.getName().equals("set")) continue;
                Class<?>[] p = m.getParameterTypes();
                try {
                    if (p.length == 3 && p[0] == float.class && p[1] == float.class && p[2] == float.class) {
                        m.setAccessible(true);
                        m.invoke(uniform, x, y, z);
                        return true;
                    }
                    if (p.length == 1 && p[0] == float[].class) {
                        m.setAccessible(true);
                        m.invoke(uniform, (Object) new float[]{x, y, z});
                        return true;
                    }
                } catch (Throwable ignored) {}
            }
            cls = cls.getSuperclass();
        }
        return false;
    }

    private static Object tryGetField(Object obj, String... names) {
        Class<?> cls = obj.getClass();
        while (cls != null) {
            for (String n : names) {
                try {
                    Field f = cls.getDeclaredField(n);
                    f.setAccessible(true);
                    return f.get(obj);
                } catch (NoSuchFieldException ignored) {
                } catch (Throwable ignored) { return null; }
            }
            cls = cls.getSuperclass();
        }
        return null;
    }

    private static Object invokeNoArg(Object obj, String name) {
        try {
            Method m = obj.getClass().getMethod(name);
            m.setAccessible(true);
            return m.invoke(obj);
        } catch (Throwable ignored) { return null; }
    }

    private static Object tryInvoke(Object obj, String name, Object... args) {
        for (Method m : obj.getClass().getMethods()) {
            if (!m.getName().equals(name)) continue;
            if (m.getParameterCount() != args.length) continue;
            try {
                m.setAccessible(true);
                return m.invoke(obj, args);
            } catch (Throwable ignored) {}
        }
        return null;
    }
}