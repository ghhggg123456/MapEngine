package io.github.ghhggg123456.mapengine.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@EventBusSubscriber(modid = "mapengine", value = Dist.CLIENT)
public final class InterfaceHandler {

    @SubscribeEvent
    public static void onLayerPre(RenderGuiLayerEvent.Pre e) {
        InterfaceElement el = InterfaceElement.byLayer(e.getName());
        if (el == null) return;
        if (el.textBased()) return;

        if (InterfaceState.isFullyInvisible(el)) {
            e.setCanceled(true);
            return;
        }

        float[] c = InterfaceState.rgba(el);
        RenderSystem.setShaderColor(c[0], c[1], c[2], c[3]);
    }

    @SubscribeEvent
    public static void onLayerPost(RenderGuiLayerEvent.Post e) {
        InterfaceElement el = InterfaceElement.byLayer(e.getName());
        if (el == null) return;
        if (el.textBased()) return;
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    @SubscribeEvent
    public static void onScreenPre(ScreenEvent.Render.Pre e) {
        if (!(e.getScreen() instanceof AbstractContainerScreen<?>)) return;

        InterfaceElement el = InterfaceElement.GUI;
        if (!InterfaceState.isCustomized(el)) return;

        if (InterfaceState.isFullyInvisible(el)) {
            e.setCanceled(true);
            return;
        }

        float[] c = InterfaceState.rgba(el);
        RenderSystem.setShaderColor(c[0], c[1], c[2], c[3]);
    }

    @SubscribeEvent
    public static void onScreenPost(ScreenEvent.Render.Post e) {
        if (!(e.getScreen() instanceof AbstractContainerScreen<?>)) return;
        if (!InterfaceState.isCustomized(InterfaceElement.GUI)) return;
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    public static void handle(InterfaceStatePayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> InterfaceState.load(
            payload.colors(), payload.opacities(), payload.visibles()));
    }

    @SubscribeEvent
    public static void onLogout(ClientPlayerNetworkEvent.LoggingOut e) {
        InterfaceState.resetAll();
    }
}