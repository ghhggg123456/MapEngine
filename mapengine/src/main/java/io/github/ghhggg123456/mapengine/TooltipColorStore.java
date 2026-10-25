package io.github.ghhggg123456.mapengine.tooltip;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;

public final class TooltipColorStore {

    private static final String KEY = "mapengine_tooltip_colors";

    private TooltipColorStore() {}

    /** Возвращает [bg, borderStart, borderEnd] — либо сохранённые, либо дефолт. */
    public static int[] get(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();
        if (data.contains(KEY, Tag.TAG_INT_ARRAY)) {
            int[] c = data.getIntArray(KEY);
            if (c.length == 3) return c;
        }
        return new int[]{
            TooltipColors.DEFAULT_BG,
            TooltipColors.DEFAULT_BORDER,
            TooltipColors.DEFAULT_BORDER
        };
    }

    public static void set(ServerPlayer player, int bg, int bs, int be) {
        player.getPersistentData().putIntArray(KEY, new int[]{bg, bs, be});
    }

    public static void reset(ServerPlayer player) {
        player.getPersistentData().remove(KEY);
    }
}