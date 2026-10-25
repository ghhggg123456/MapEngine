package io.github.ghhggg123456.mapengine.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.ghhggg123456.mapengine.ui.InterfaceElement;
import io.github.ghhggg123456.mapengine.ui.InterfaceState;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class HandMixin {

    @Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
    private void mapengine$onRenderHandPre(
            AbstractClientPlayer player, float partialTicks, float pitch,
            InteractionHand hand, float swingProgress, ItemStack stack,
            float equippedProgress, PoseStack poseStack,
            MultiBufferSource buffer, int combinedLight, CallbackInfo ci) {

        InterfaceElement el = hand == InteractionHand.MAIN_HAND
            ? InterfaceElement.RIGHT_HAND
            : InterfaceElement.LEFT_HAND;

        if (InterfaceState.isFullyInvisible(el)) {
            ci.cancel();
            return;
        }

        if (InterfaceState.isCustomized(el)) {
            float[] c = InterfaceState.rgba(el);
            RenderSystem.setShaderColor(c[0], c[1], c[2], c[3]);
        }
    }

    @Inject(method = "renderArmWithItem", at = @At("RETURN"))
    private void mapengine$onRenderHandPost(
            AbstractClientPlayer player, float partialTicks, float pitch,
            InteractionHand hand, float swingProgress, ItemStack stack,
            float equippedProgress, PoseStack poseStack,
            MultiBufferSource buffer, int combinedLight, CallbackInfo ci) {

        InterfaceElement el = hand == InteractionHand.MAIN_HAND
            ? InterfaceElement.RIGHT_HAND
            : InterfaceElement.LEFT_HAND;

        if (InterfaceState.isCustomized(el)) {
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        }
    }
}