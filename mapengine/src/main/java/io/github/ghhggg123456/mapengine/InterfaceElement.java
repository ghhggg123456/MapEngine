package io.github.ghhggg123456.mapengine.ui;

import net.minecraft.resources.ResourceLocation;

public enum InterfaceElement {
    AIR            ("air",              "minecraft:air_level",         false, false),
    ARMOR          ("armor",            "minecraft:armor_level",       false, false),
    FOOD           ("food",             "minecraft:food_level",        false, false),
    HEALTH         ("health",           "minecraft:player_health",     false, false),
    EXPERIENCE_BAR ("experience_bar",   "minecraft:experience_bar",    false, false),
    EXPERIENCE_LVL ("experience_level", "minecraft:experience_level",  true,  false),
    HOTBAR         ("hotbar",           "minecraft:hotbar",            false, false),
    GUI            ("gui",              "mapengine:container_gui",     false, true),
    ANNOUNCEMENTS  ("announcements",    "mapengine:announcements",     false, false),
    ITEM_NAME      ("item_name",        "minecraft:item_name",         true,  false),
    CHAT           ("chat",             "minecraft:chat",              false, false),
    RIGHT_HAND     ("right_hand",       "mapengine:right_hand",        false, false),
    LEFT_HAND      ("left_hand",        "mapengine:left_hand",         false, false),
    RECIPEBOOK     ("recipebook",       "mapengine:recipebook",        false, true);

    private final String key;
    private final ResourceLocation layerId;
    private final boolean textBased;
    private final boolean screen;

    InterfaceElement(String key, String layer, boolean textBased, boolean screen) {
        this.key = key;
        this.layerId = ResourceLocation.parse(layer);
        this.textBased = textBased;
        this.screen = screen;
    }

    public String key() { return key; }
    public ResourceLocation layerId() { return layerId; }
    public boolean textBased() { return textBased; }
    public boolean isScreen() { return screen; }

    public static InterfaceElement byKey(String k) {
        for (InterfaceElement e : values())
            if (e.key.equalsIgnoreCase(k)) return e;
        return null;
    }

    public static InterfaceElement byLayer(ResourceLocation id) {
        for (InterfaceElement e : values())
            if (!e.screen && !e.textBased && e.layerId.equals(id)) return e;
        return null;
    }
}