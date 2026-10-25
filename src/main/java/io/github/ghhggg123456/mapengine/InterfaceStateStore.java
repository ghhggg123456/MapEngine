package io.github.ghhggg123456.mapengine.ui;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public final class InterfaceStateStore {

    private static final String PREFIX = "mapengine_iface_";

    private InterfaceStateStore() {}

    private static String key(InterfaceElement e, String prop) {
        return PREFIX + e.key() + "_" + prop;
    }

    private static int clampPct(int v) {
        return Math.max(0, Math.min(100, v));
    }

    public static int getColor(ServerPlayer p, InterfaceElement e) {
        CompoundTag t = p.getPersistentData();
        String k = key(e, "color");
        return t.contains(k) ? (t.getInt(k) & 0xFFFFFF) : InterfaceState.DEF_COLOR;
    }

    public static int getOpacity(ServerPlayer p, InterfaceElement e) {
        CompoundTag t = p.getPersistentData();
        String k = key(e, "opacity");
        return t.contains(k) ? clampPct(t.getInt(k)) : InterfaceState.DEF_OPACITY;
    }

    public static boolean getVisible(ServerPlayer p, InterfaceElement e) {
        CompoundTag t = p.getPersistentData();
        String k = key(e, "visible");
        return !t.contains(k) || t.getBoolean(k);
    }

    public static void setColor(ServerPlayer p, InterfaceElement e, int rgb) {
        p.getPersistentData().putInt(key(e, "color"), rgb & 0xFFFFFF);
    }

    public static void setOpacity(ServerPlayer p, InterfaceElement e, int pct) {
        p.getPersistentData().putInt(key(e, "opacity"), clampPct(pct));
    }

    public static void setVisible(ServerPlayer p, InterfaceElement e, boolean v) {
        p.getPersistentData().putBoolean(key(e, "visible"), v);
    }

    public static void reset(ServerPlayer p, InterfaceElement e) {
        CompoundTag t = p.getPersistentData();
        t.remove(key(e, "color"));
        t.remove(key(e, "opacity"));
        t.remove(key(e, "visible"));
    }
}