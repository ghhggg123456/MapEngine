package io.github.ghhggg123456.mapengine;

import io.github.ghhggg123456.mapengine.client.TooltipColorHandler;
import io.github.ghhggg123456.mapengine.shader.ShaderGuard;
import io.github.ghhggg123456.mapengine.shader.ShaderPayload;
import io.github.ghhggg123456.mapengine.tooltip.TooltipColorsPayload;
import io.github.ghhggg123456.mapengine.ui.InterfaceHandler;
import io.github.ghhggg123456.mapengine.ui.InterfaceStatePayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = "mapengine", bus = EventBusSubscriber.Bus.MOD)
public final class MapEngineNetwork {

    private MapEngineNetwork() {}

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar r = event.registrar("1");
        r.playToClient(TooltipColorsPayload.TYPE, TooltipColorsPayload.STREAM_CODEC, TooltipColorHandler::handle);
        r.playToClient(InterfaceStatePayload.TYPE, InterfaceStatePayload.STREAM_CODEC, InterfaceHandler::handle);
        r.playToClient(ShaderPayload.TYPE, ShaderPayload.STREAM_CODEC, ShaderGuard::handle);
    }
}