package io.github.ghhggg123456.mapengine.ui;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Arrays;

@EventBusSubscriber(modid = "mapengine")
public final class InterfaceCommand {

    private static final SimpleCommandExceptionType ERR_ELEMENT =
        new SimpleCommandExceptionType(Component.literal("Unknown interface element"));
    private static final SimpleCommandExceptionType ERR_COLOR =
        new SimpleCommandExceptionType(Component.literal("Invalid color, use RRGGBB or #RRGGBB"));
    private static final SimpleCommandExceptionType ERR_PERCENT =
        new SimpleCommandExceptionType(Component.literal("Invalid percent, use 0-100 or 0%-100%"));

    @SubscribeEvent
    public static void register(RegisterCommandsEvent ev) {
        ev.getDispatcher().register(
            Commands.literal("interface")
                .requires(src -> src.hasPermission(2))
                .then(Commands.argument("targets", EntityArgument.players())
                    .then(Commands.argument("element", StringArgumentType.word())
                        .suggests((ctx, b) -> SharedSuggestionProvider.suggest(
                            Arrays.stream(InterfaceElement.values())
                                .map(InterfaceElement::key).toList(), b))

                        .then(Commands.literal("color")
                            .then(Commands.argument("color", StringArgumentType.string())
                                .executes(ctx -> {
                                    var players = EntityArgument.getPlayers(ctx, "targets");
                                    InterfaceElement el = element(ctx);
                                    int rgb = parseColor(StringArgumentType.getString(ctx, "color"));
                                    for (ServerPlayer p : players) {
                                        InterfaceStateStore.setColor(p, el, rgb);
                                        push(p);
                                    }
                                    return players.size();
                                })))

                        .then(Commands.literal("opacity")
                            .then(Commands.argument("value", StringArgumentType.string())
                                .executes(ctx -> {
                                    var players = EntityArgument.getPlayers(ctx, "targets");
                                    InterfaceElement el = element(ctx);
                                    int pct = parsePercent(StringArgumentType.getString(ctx, "value"));
                                    for (ServerPlayer p : players) {
                                        InterfaceStateStore.setOpacity(p, el, pct);
                                        push(p);
                                    }
                                    return players.size();
                                })))

                        .then(Commands.literal("visibility")
                            .then(Commands.argument("value", BoolArgumentType.bool())
                                .executes(ctx -> {
                                    var players = EntityArgument.getPlayers(ctx, "targets");
                                    InterfaceElement el = element(ctx);
                                    boolean v = BoolArgumentType.getBool(ctx, "value");
                                    for (ServerPlayer p : players) {
                                        InterfaceStateStore.setVisible(p, el, v);
                                        push(p);
                                    }
                                    return players.size();
                                })))

                        .then(Commands.literal("reset")
                            .executes(ctx -> {
                                var players = EntityArgument.getPlayers(ctx, "targets");
                                InterfaceElement el = element(ctx);
                                for (ServerPlayer p : players) {
                                    InterfaceStateStore.reset(p, el);
                                    push(p);
                                }
                                return players.size();
                            }))
                    )
                )
        );
    }

    @SubscribeEvent
    public static void onJoin(PlayerEvent.PlayerLoggedInEvent e) {
        if (e.getEntity() instanceof ServerPlayer p) push(p);
    }

    private static void push(ServerPlayer p) {
        InterfaceElement[] all = InterfaceElement.values();
        int n = all.length;
        int[] c = new int[n];
        int[] o = new int[n];
        boolean[] v = new boolean[n];
        for (int i = 0; i < n; i++) {
            c[i] = InterfaceStateStore.getColor(p, all[i]);
            o[i] = InterfaceStateStore.getOpacity(p, all[i]);
            v[i] = InterfaceStateStore.getVisible(p, all[i]);
        }
        PacketDistributor.sendToPlayer(p, new InterfaceStatePayload(c, o, v));
    }

    private static InterfaceElement element(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        InterfaceElement el = InterfaceElement.byKey(StringArgumentType.getString(ctx, "element"));
        if (el == null) throw ERR_ELEMENT.create();
        return el;
    }

    private static int parseColor(String s) throws CommandSyntaxException {
        String hex = s;
        if (hex.startsWith("#")) hex = hex.substring(1);
        if (hex.startsWith("0x") || hex.startsWith("0X")) hex = hex.substring(2);
        if (hex.length() != 6) throw ERR_COLOR.create();
        try {
            return Integer.parseInt(hex, 16);
        } catch (NumberFormatException e) {
            throw ERR_COLOR.create();
        }
    }

    private static int parsePercent(String s) throws CommandSyntaxException {
        String t = s.endsWith("%") ? s.substring(0, s.length() - 1) : s;
        try {
            int v = Integer.parseInt(t);
            if (v < 0 || v > 100) throw ERR_PERCENT.create();
            return v;
        } catch (NumberFormatException e) {
            throw ERR_PERCENT.create();
        }
    }
}