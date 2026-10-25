package io.github.ghhggg123456.mapengine.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.ghhggg123456.mapengine.ui.InterfaceElement;
import io.github.ghhggg123456.mapengine.ui.InterfaceState;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatComponent.class)
public class ChatMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void mapengine$onRenderPre(GuiGraphics guiGraphics, int tickCount, int mouseX, int mouseY, boolean focused, CallbackInfo ci) {
        InterfaceElement el = InterfaceElement.CHAT;
        
        if (InterfaceState.isFullyInvisible(el)) {
            ci.cancel();
            return;
        }

        if (InterfaceState.isCustomized(el)) {
            float[] c = InterfaceState.rgba(el);
            RenderSystem.setShaderColor(c[0], c[1], c[2], c[3]);
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void mapengine$onRenderPost(GuiGraphics guiGraphics, int tickCount, int mouseX, int mouseY, boolean focused, CallbackInfo ci) {
        if (InterfaceState.isCustomized(InterfaceElement.CHAT)) {
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        }
    }
}