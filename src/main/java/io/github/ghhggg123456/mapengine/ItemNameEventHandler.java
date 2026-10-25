package io.github.ghhggg123456.mapengine.ui;

import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import com.mojang.blaze3d.systems.RenderSystem;

@EventBusSubscriber(modid = "mapengine", value = Dist.CLIENT)
public class ItemNameEventHandler {

    @SubscribeEvent
    public static void onRenderItemName(RenderGuiLayerEvent.Pre event) {
        // Проверяем, что это именно слой с названием предмета
        if (!event.getName().equals(VanillaGuiLayers.SELECTED_ITEM_NAME)) {
            return;
        }

        InterfaceElement el = InterfaceElement.ITEM_NAME;

        // 1. Полное скрытие, если установлено visibility = false или opacity = 0
        if (InterfaceState.isFullyInvisible(el)) {
            event.setCanceled(true);
            return;
        }

        // 2. Изменение цвета и прозрачности
        // Если элемент не был кастомизирован, ничего не делаем
        if (!InterfaceState.isCustomized(el)) {
            return;
        }

        // Если кастомизирован, применяем цвет и альфу через RenderSystem.
        // Это умножит цвета отрисовываемого слоя на заданные значения.
        float[] c = InterfaceState.rgba(el);
        RenderSystem.setShaderColor(c[0], c[1], c[2], c[3]);
    }

    @SubscribeEvent
    public static void onRenderItemNamePost(RenderGuiLayerEvent.Post event) {
        // После отрисовки слоя с названием предмета возвращаем стандартный цвет,
        // чтобы не повлиять на другие элементы HUD.
        if (event.getName().equals(VanillaGuiLayers.SELECTED_ITEM_NAME)) {
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        }
    }
}