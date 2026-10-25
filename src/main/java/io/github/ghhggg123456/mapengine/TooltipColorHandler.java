package io.github.ghhggg123456.mapengine.client;

import io.github.ghhggg123456.mapengine.tooltip.TooltipColors;
import io.github.ghhggg123456.mapengine.tooltip.TooltipColorsPayload;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@EventBusSubscriber(modid = "mapengine", value = Dist.CLIENT)
public class TooltipColorHandler {

    private static final String TAG_BG = "background_color";
    private static final String TAG_BS = "border_start_color";
    private static final String TAG_BE = "border_end_color";

    @SubscribeEvent
    public static void onTooltipColor(RenderTooltipEvent.Color event) {
        int bg = TooltipColors.background();
        int bs = TooltipColors.borderStart();
        int be = TooltipColors.borderEnd();

        // NBT предмета имеет приоритет
        ItemStack stack = event.getItemStack();
        if (stack != null && !stack.isEmpty()) {
            CompoundTag tag = getCustomTag(stack);
            if (tag != null) {
                bg = readColor(tag, TAG_BG, bg);
                bs = readColor(tag, TAG_BS, bs);
                be = readColor(tag, TAG_BE, be);
            }
        }

        event.setBackground(bg);
        event.setBorderStart(bs);
        event.setBorderEnd(be);
    }

    /**
     * В 1.21.1 старые getTag()/getOrCreateTag() удалены — данные предмета
     * хранятся в компоненте DataComponents.CUSTOM_DATA.
     */
    private static CompoundTag getCustomTag(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data != null ? data.copyTag() : null;
    }

    /**
     * Читает цвет из NBT. Поддерживает:
     *  - int:     background_color: -301989888   (0xEE161000 как знаковый int)
     *  - long:    background_color: 3994243088L  (0xEE161000 как беззнаковый long)
     *  - string:  background_color: "0xEE161000"
     */
    private static int readColor(CompoundTag tag, String key, int fallback) {
        Tag raw = tag.get(key);
        if (raw == null) return fallback;

        if (raw instanceof IntTag it) return it.getAsInt();
        if (raw instanceof LongTag lt) return (int) lt.getAsLong();
        if (raw instanceof StringTag st) {
            try {
                String s = st.getAsString();
                String hex = s.startsWith("#") ? s.substring(1) : s;
                if (hex.startsWith("0x") || hex.startsWith("0X")) hex = hex.substring(2);
                return (int) Long.parseLong(hex, 16);
            } catch (NumberFormatException e) {
                return fallback;
            }
        }
        return fallback;
    }

    public static void handle(TooltipColorsPayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() ->
            TooltipColors.set(payload.bg(), payload.borderStart(), payload.borderEnd()));
    }
}