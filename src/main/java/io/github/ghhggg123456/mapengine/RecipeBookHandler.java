package io.github.ghhggg123456.mapengine.recipebook;

import io.github.ghhggg123456.mapengine.ui.InterfaceElement;
import io.github.ghhggg123456.mapengine.ui.InterfaceState;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.lwjgl.glfw.GLFW;

import java.util.Map;
import java.util.WeakHashMap;

@EventBusSubscriber(modid = "mapengine", value = Dist.CLIENT)
public final class RecipeBookHandler {

    private static final Map<Screen, ImageButton> RECIPE_BUTTONS = new WeakHashMap<>();

    @SubscribeEvent
    public static void onRender(ScreenEvent.Render.Pre event) {
        Screen screen = event.getScreen();
        if (!(screen instanceof AbstractContainerScreen<?>)) return;

        ImageButton button = RECIPE_BUTTONS.get(screen);
        if (button == null) {
            button = findRecipeButton(screen);
            if (button != null) {
                RECIPE_BUTTONS.put(screen, button);
            }
        }

        if (button != null) {
            button.visible = !InterfaceState.isFullyInvisible(InterfaceElement.RECIPEBOOK);
        }
    }

    private static ImageButton findRecipeButton(Screen screen) {
        for (GuiEventListener child : screen.children()) {
            if (child instanceof ImageButton btn) {
                if (btn.getWidth() == 20 && btn.getHeight() == 18) {
                    return btn;
                }
            }
        }
        return null;
    }

    @SubscribeEvent
    public static void onKey(ScreenEvent.KeyPressed.Pre event) {
        if (!InterfaceState.isFullyInvisible(InterfaceElement.RECIPEBOOK)) return;
        if (!(event.getScreen() instanceof AbstractContainerScreen<?>)) return;
        if (event.getKeyCode() == GLFW.GLFW_KEY_R && event.getModifiers() == 0) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        RECIPE_BUTTONS.clear();
    }
}