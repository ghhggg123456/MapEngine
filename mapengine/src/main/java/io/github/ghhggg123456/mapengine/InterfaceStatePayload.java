package io.github.ghhggg123456.mapengine.ui;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record InterfaceStatePayload(int[] colors, int[] opacities, boolean[] visibles)
        implements CustomPacketPayload {

    public static final Type<InterfaceStatePayload> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath("mapengine", "interface_state"));

    public static final StreamCodec<FriendlyByteBuf, InterfaceStatePayload> STREAM_CODEC =
        StreamCodec.of(
            (buf, p) -> {
                int n = p.colors().length;
                buf.writeVarInt(n);
                for (int i = 0; i < n; i++) {
                    buf.writeInt(p.colors()[i]);
                    buf.writeByte(p.opacities()[i]);
                    buf.writeBoolean(p.visibles()[i]);
                }
            },
            buf -> {
                int n = buf.readVarInt();
                int[] c = new int[n];
                int[] o = new int[n];
                boolean[] v = new boolean[n];
                for (int i = 0; i < n; i++) {
                    c[i] = buf.readInt();
                    o[i] = buf.readByte();
                    v[i] = buf.readBoolean();
                }
                return new InterfaceStatePayload(c, o, v);
            }
        );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}