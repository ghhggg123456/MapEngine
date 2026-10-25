package io.github.ghhggg123456.mapengine.tooltip;

import io.github.ghhggg123456.mapengine.MapengineMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record TooltipColorsPayload(int bg, int borderStart, int borderEnd)
        implements CustomPacketPayload {

    public static final Type<TooltipColorsPayload> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(MapengineMod.MODID, "tooltip_colors"));

    public static final StreamCodec<FriendlyByteBuf, TooltipColorsPayload> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.INT, TooltipColorsPayload::bg,
            ByteBufCodecs.INT, TooltipColorsPayload::borderStart,
            ByteBufCodecs.INT, TooltipColorsPayload::borderEnd,
            TooltipColorsPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}