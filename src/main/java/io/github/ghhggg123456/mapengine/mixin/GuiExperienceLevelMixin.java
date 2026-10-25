package io.github.ghhggg123456.mapengine.mixin;

import io.github.ghhggg123456.mapengine.ui.InterfaceElement;
import io.github.ghhggg123456.mapengine.ui.InterfaceState;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Gui.class)
public class GuiExperienceLevelMixin {

    @Redirect(
        method = "renderExperienceLevel",
        at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)I"),
        require = 0
    )
    private int mapengine$drawLevel(GuiGraphics gfx, Font font, String text,
                                    int x, int y, int color, boolean dropShadow) {
        InterfaceElement el = InterfaceElement.EXPERIENCE_LVL;

        if (!InterfaceState.isCustomized(el)) {
            return gfx.drawString(font, text, x, y, color, dropShadow);
        }

        if (InterfaceState.isFullyInvisible(el)) {
            return 0;
        }

        int alpha = InterfaceState.alpha(el);
        int originalRgb = color & 0xFFFFFF;
        int finalRgb = originalRgb == 0 ? 0 : InterfaceState.color(el);
        int finalColor = (alpha << 24) | finalRgb;

        return gfx.drawString(font, text, x, y, finalColor, dropShadow);
    }
}