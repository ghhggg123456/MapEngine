package io.github.ghhggg123456.mapengine.shader;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ShaderPayload(
    float pixelSize,
    float tintR, float tintG, float tintB,
    float satR, float satG, float satB,
    float hue,
    float contrast, float brightness,
    int   posterize,
    int   noiseSize, int noiseRate,
    float blur,
    float vigR, float vigG, float vigB, float vignetteSize,
    float outR, float outG, float outB, float outlineSize,
    float chromaticStrength,
    float motionStrength
) implements CustomPacketPayload {

    public static final Type<ShaderPayload> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath("mapengine", "shader"));

    public static final StreamCodec<FriendlyByteBuf, ShaderPayload> STREAM_CODEC =
        StreamCodec.of(
            (buf, p) -> {
                buf.writeFloat(p.pixelSize());
                buf.writeFloat(p.tintR()); buf.writeFloat(p.tintG()); buf.writeFloat(p.tintB());
                buf.writeFloat(p.satR());  buf.writeFloat(p.satG());  buf.writeFloat(p.satB());
                buf.writeFloat(p.hue());
                buf.writeFloat(p.contrast()); buf.writeFloat(p.brightness());
                buf.writeVarInt(p.posterize());
                buf.writeVarInt(p.noiseSize()); buf.writeVarInt(p.noiseRate());
                buf.writeFloat(p.blur());
                buf.writeFloat(p.vigR()); buf.writeFloat(p.vigG()); buf.writeFloat(p.vigB());
                buf.writeFloat(p.vignetteSize());
                buf.writeFloat(p.outR()); buf.writeFloat(p.outG()); buf.writeFloat(p.outB());
                buf.writeFloat(p.outlineSize());
                buf.writeFloat(p.chromaticStrength());
                buf.writeFloat(p.motionStrength());
            },
            buf -> new ShaderPayload(
                buf.readFloat(),
                buf.readFloat(), buf.readFloat(), buf.readFloat(),
                buf.readFloat(), buf.readFloat(), buf.readFloat(),
                buf.readFloat(),
                buf.readFloat(), buf.readFloat(),
                buf.readVarInt(),
                buf.readVarInt(), buf.readVarInt(),
                buf.readFloat(),
                buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(),
                buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(),
                buf.readFloat(),
                buf.readFloat()
            )
        );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}