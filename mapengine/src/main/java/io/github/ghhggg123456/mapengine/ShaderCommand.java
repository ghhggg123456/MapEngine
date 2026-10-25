package io.github.ghhggg123456.mapengine.shader;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Collection;

@EventBusSubscriber(modid = "mapengine")
public final class ShaderCommand {

    private static final SimpleCommandExceptionType ERR_COLOR =
        new SimpleCommandExceptionType(Component.literal("Invalid color (use RRGGBB or #RRGGBB)"));

    @SubscribeEvent
    public static void register(RegisterCommandsEvent ev) {
        ev.getDispatcher().register(
            Commands.literal("shader")
                .requires(src -> src.hasPermission(2))
                .then(Commands.argument("targets", EntityArgument.players())

                    .then(Commands.literal("reset")
                        .executes(ctx -> resetAll(EntityArgument.getPlayers(ctx, "targets")))
                        .then(Commands.literal("outline")    .executes(ctx -> resetEffect(EntityArgument.getPlayers(ctx, "targets"), "outline")))
                        .then(Commands.literal("vignette")   .executes(ctx -> resetEffect(EntityArgument.getPlayers(ctx, "targets"), "vignette")))
                        .then(Commands.literal("tint")       .executes(ctx -> resetEffect(EntityArgument.getPlayers(ctx, "targets"), "tint")))
                        .then(Commands.literal("saturation") .executes(ctx -> resetEffect(EntityArgument.getPlayers(ctx, "targets"), "saturation")))
                        .then(Commands.literal("hue")        .executes(ctx -> resetEffect(EntityArgument.getPlayers(ctx, "targets"), "hue")))
                        .then(Commands.literal("contrast")   .executes(ctx -> resetEffect(EntityArgument.getPlayers(ctx, "targets"), "contrast")))
                        .then(Commands.literal("brightness") .executes(ctx -> resetEffect(EntityArgument.getPlayers(ctx, "targets"), "brightness")))
                        .then(Commands.literal("posterize")  .executes(ctx -> resetEffect(EntityArgument.getPlayers(ctx, "targets"), "posterize")))
                        .then(Commands.literal("pixelize")   .executes(ctx -> resetEffect(EntityArgument.getPlayers(ctx, "targets"), "pixelize")))
                        .then(Commands.literal("noise")      .executes(ctx -> resetEffect(EntityArgument.getPlayers(ctx, "targets"), "noise")))
                        .then(Commands.literal("blur")       .executes(ctx -> resetEffect(EntityArgument.getPlayers(ctx, "targets"), "blur")))
                        .then(Commands.literal("chromatic")  .executes(ctx -> resetEffect(EntityArgument.getPlayers(ctx, "targets"), "chromatic")))
                        .then(Commands.literal("motion")     .executes(ctx -> resetEffect(EntityArgument.getPlayers(ctx, "targets"), "motion")))
                    )

                    .then(Commands.literal("outline")
                        .then(Commands.argument("size", FloatArgumentType.floatArg(0f, 100f))
                            .then(Commands.argument("color", StringArgumentType.word())
                                .executes(ctx -> doOutline(
                                    EntityArgument.getPlayers(ctx, "targets"),
                                    FloatArgumentType.getFloat(ctx, "size"),
                                    StringArgumentType.getString(ctx, "color"))))))

                    .then(Commands.literal("vignette")
                        .then(Commands.argument("size", FloatArgumentType.floatArg(0f, 100f))
                            .then(Commands.argument("color", StringArgumentType.word())
                                .executes(ctx -> doVignette(
                                    EntityArgument.getPlayers(ctx, "targets"),
                                    FloatArgumentType.getFloat(ctx, "size"),
                                    StringArgumentType.getString(ctx, "color"))))))

                    .then(Commands.literal("tint")
                        .then(Commands.argument("r", FloatArgumentType.floatArg(0f, 200f))
                            .then(Commands.argument("g", FloatArgumentType.floatArg(0f, 200f))
                                .then(Commands.argument("b", FloatArgumentType.floatArg(0f, 200f))
                                    .executes(ctx -> doTint(
                                        EntityArgument.getPlayers(ctx, "targets"),
                                        FloatArgumentType.getFloat(ctx, "r"),
                                        FloatArgumentType.getFloat(ctx, "g"),
                                        FloatArgumentType.getFloat(ctx, "b")))))))

                    .then(Commands.literal("saturation")
                        .then(Commands.argument("r", FloatArgumentType.floatArg(0f, 200f))
                            .then(Commands.argument("g", FloatArgumentType.floatArg(0f, 200f))
                                .then(Commands.argument("b", FloatArgumentType.floatArg(0f, 200f))
                                    .executes(ctx -> doSaturation(
                                        EntityArgument.getPlayers(ctx, "targets"),
                                        FloatArgumentType.getFloat(ctx, "r"),
                                        FloatArgumentType.getFloat(ctx, "g"),
                                        FloatArgumentType.getFloat(ctx, "b")))))))

                    .then(Commands.literal("hue")
                        .then(Commands.argument("degrees", FloatArgumentType.floatArg(0f, 360f))
                            .executes(ctx -> doHue(
                                EntityArgument.getPlayers(ctx, "targets"),
                                FloatArgumentType.getFloat(ctx, "degrees")))))

                    .then(Commands.literal("contrast")
                        .then(Commands.argument("value", FloatArgumentType.floatArg(0f, 200f))
                            .executes(ctx -> doContrast(
                                EntityArgument.getPlayers(ctx, "targets"),
                                FloatArgumentType.getFloat(ctx, "value")))))

                    .then(Commands.literal("brightness")
                        .then(Commands.argument("value", FloatArgumentType.floatArg(0f, 200f))
                            .executes(ctx -> doBrightness(
                                EntityArgument.getPlayers(ctx, "targets"),
                                FloatArgumentType.getFloat(ctx, "value")))))

                    .then(Commands.literal("posterize")
                        .then(Commands.argument("levels", IntegerArgumentType.integer(0, 100))
                            .executes(ctx -> doPosterize(
                                EntityArgument.getPlayers(ctx, "targets"),
                                IntegerArgumentType.getInteger(ctx, "levels")))))

                    .then(Commands.literal("pixelize")
                        .then(Commands.argument("size", FloatArgumentType.floatArg(1f, 128f))
                            .executes(ctx -> doPixelize(
                                EntityArgument.getPlayers(ctx, "targets"),
                                FloatArgumentType.getFloat(ctx, "size")))))

                    .then(Commands.literal("noise")
                        .then(Commands.argument("size", IntegerArgumentType.integer(1))
                            .then(Commands.argument("rate", IntegerArgumentType.integer(0, 100))
                                .executes(ctx -> doNoise(
                                    EntityArgument.getPlayers(ctx, "targets"),
                                    IntegerArgumentType.getInteger(ctx, "size"),
                                    IntegerArgumentType.getInteger(ctx, "rate"))))))

                    .then(Commands.literal("blur")
                        .then(Commands.argument("value", FloatArgumentType.floatArg(0f, 100f))
                            .executes(ctx -> doBlur(
                                EntityArgument.getPlayers(ctx, "targets"),
                                FloatArgumentType.getFloat(ctx, "value")))))

                    .then(Commands.literal("chromatic")
                        .then(Commands.argument("strength", FloatArgumentType.floatArg(0f, 100f))
                            .executes(ctx -> doChromatic(
                                EntityArgument.getPlayers(ctx, "targets"),
                                FloatArgumentType.getFloat(ctx, "strength")))))

                    .then(Commands.literal("motion")
                        .then(Commands.argument("strength", FloatArgumentType.floatArg(0f, 100f))
                            .executes(ctx -> doMotion(
                                EntityArgument.getPlayers(ctx, "targets"),
                                FloatArgumentType.getFloat(ctx, "strength")))))
                )
        );
    }

    @SubscribeEvent
    public static void onJoin(PlayerEvent.PlayerLoggedInEvent e) {
        if (e.getEntity() instanceof ServerPlayer p) push(p);
    }

    private static int doOutline(Collection<ServerPlayer> ts, float size, String hex) throws CommandSyntaxException {
        int rgb = parseColor(hex);
        float r = ((rgb >> 16) & 0xFF) / 255f, g = ((rgb >> 8) & 0xFF) / 255f, b = (rgb & 0xFF) / 255f;
        for (ServerPlayer p : ts) { ShaderStateStore.setOutline(p, r, g, b, size); push(p); }
        return ts.size();
    }
    private static int doVignette(Collection<ServerPlayer> ts, float size, String hex) throws CommandSyntaxException {
        int rgb = parseColor(hex);
        float r = ((rgb >> 16) & 0xFF) / 255f, g = ((rgb >> 8) & 0xFF) / 255f, b = (rgb & 0xFF) / 255f;
        for (ServerPlayer p : ts) { ShaderStateStore.setVignette(p, r, g, b, size); push(p); }
        return ts.size();
    }
    private static int doTint(Collection<ServerPlayer> ts, float r, float g, float b) {
        for (ServerPlayer p : ts) { ShaderStateStore.setTint(p, r / 100f, g / 100f, b / 100f); push(p); }
        return ts.size();
    }
    private static int doSaturation(Collection<ServerPlayer> ts, float r, float g, float b) {
        for (ServerPlayer p : ts) { ShaderStateStore.setSaturation(p, r / 100f, g / 100f, b / 100f); push(p); }
        return ts.size();
    }
    private static int doHue(Collection<ServerPlayer> ts, float deg) {
        for (ServerPlayer p : ts) { ShaderStateStore.setHue(p, deg); push(p); }
        return ts.size();
    }
    private static int doContrast(Collection<ServerPlayer> ts, float v) {
        for (ServerPlayer p : ts) { ShaderStateStore.setContrast(p, v / 100f); push(p); }
        return ts.size();
    }
    private static int doBrightness(Collection<ServerPlayer> ts, float v) {
        for (ServerPlayer p : ts) { ShaderStateStore.setBrightness(p, v / 100f); push(p); }
        return ts.size();
    }
    private static int doPosterize(Collection<ServerPlayer> ts, int levels) {
        for (ServerPlayer p : ts) { ShaderStateStore.setPosterize(p, levels); push(p); }
        return ts.size();
    }
    private static int doPixelize(Collection<ServerPlayer> ts, float size) {
        for (ServerPlayer p : ts) { ShaderStateStore.setPixelize(p, size); push(p); }
        return ts.size();
    }
    private static int doNoise(Collection<ServerPlayer> ts, int size, int rate) {
        for (ServerPlayer p : ts) { ShaderStateStore.setNoise(p, size, rate); push(p); }
        return ts.size();
    }
    private static int doBlur(Collection<ServerPlayer> ts, float v) {
        for (ServerPlayer p : ts) { ShaderStateStore.setBlur(p, v); push(p); }
        return ts.size();
    }
    private static int doChromatic(Collection<ServerPlayer> ts, float v) {
        for (ServerPlayer p : ts) { ShaderStateStore.setChromatic(p, v); push(p); }
        return ts.size();
    }
    private static int doMotion(Collection<ServerPlayer> ts, float v) {
        for (ServerPlayer p : ts) { ShaderStateStore.setMotion(p, v); push(p); }
        return ts.size();
    }

    private static int resetAll(Collection<ServerPlayer> ts) {
        for (ServerPlayer p : ts) { ShaderStateStore.reset(p); push(p); }
        return ts.size();
    }
    private static int resetEffect(Collection<ServerPlayer> ts, String effect) {
        for (ServerPlayer p : ts) {
            switch (effect) {
                case "outline"    -> ShaderStateStore.resetOutline(p);
                case "vignette"   -> ShaderStateStore.resetVignette(p);
                case "tint"       -> ShaderStateStore.resetTint(p);
                case "saturation" -> ShaderStateStore.resetSaturation(p);
                case "hue"        -> ShaderStateStore.resetHue(p);
                case "contrast"   -> ShaderStateStore.resetContrast(p);
                case "brightness" -> ShaderStateStore.resetBrightness(p);
                case "posterize"  -> ShaderStateStore.resetPosterize(p);
                case "pixelize"   -> ShaderStateStore.resetPixelize(p);
                case "noise"      -> ShaderStateStore.resetNoise(p);
                case "blur"       -> ShaderStateStore.resetBlur(p);
                case "chromatic"  -> ShaderStateStore.resetChromatic(p);
                case "motion"     -> ShaderStateStore.resetMotion(p);
            }
            push(p);
        }
        return ts.size();
    }

    private static void push(ServerPlayer p) {
        PacketDistributor.sendToPlayer(p, ShaderStateStore.load(p));
    }

    private static int parseColor(String s) throws CommandSyntaxException {
        String hex = s.startsWith("#") ? s.substring(1) : s;
        if (hex.startsWith("0x") || hex.startsWith("0X")) hex = hex.substring(2);
        if (hex.length() != 6) throw ERR_COLOR.create();
        try { return Integer.parseInt(hex, 16); }
        catch (NumberFormatException e) { throw ERR_COLOR.create(); }
    }
}