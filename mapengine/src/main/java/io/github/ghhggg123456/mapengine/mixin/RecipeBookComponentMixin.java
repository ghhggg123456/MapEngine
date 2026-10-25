package io.github.ghhggg123456.mapengine.mixin;

import io.github.ghhggg123456.mapengine.ui.InterfaceElement;
import io.github.ghhggg123456.mapengine.ui.InterfaceState;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RecipeBookComponent.class)
public class RecipeBookComponentMixin {

    private boolean isDisabled() {
        return InterfaceState.isFullyInvisible(InterfaceElement.RECIPEBOOK);
    }

    @Inject(method = "toggleVisibility", at = @At("HEAD"), cancellable = true, require = 0)
    private void onToggleVisibility(CallbackInfo ci) {
        if (isDisabled()) ci.cancel();
    }

    @Inject(method = "tryPlaceRecipe", at = @At("HEAD"), cancellable = true, require = 0)
    private void onTryPlaceRecipe(CallbackInfo ci) {
        if (isDisabled()) ci.cancel();
    }

    @Inject(method = "handlePlaceRecipe", at = @At("HEAD"), cancellable = true, require = 0)
    private void onHandlePlaceRecipe(CallbackInfo ci) {
        if (isDisabled()) ci.cancel();
    }
}