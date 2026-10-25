package io.github.ghhggg123456.mapengine.tooltip;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
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
public final class TooltipCommand {

    private static final SimpleCommandExceptionType ERR_COLOR =
        new SimpleCommandExceptionType(Component.literal("Invalid color (use AARRGGBB or RRGGBB)"));

    @SubscribeEvent
    public static void register(RegisterCommandsEvent ev) {
        ev.getDispatcher().register(
            Commands.literal("tooltip")
                .requires(src -> src.hasPermission(2))
                .then(Commands.argument("targets", EntityArgument.players())
                    .then(Commands.literal("reset")
                        .executes(ctx -> applyReset(ctx, EntityArgument.getPlayers(ctx, "targets"))))
                    .then(Commands.argument("background", StringArgumentType.word())
                        .then(Commands.argument("borderStart", StringArgumentType.word())
                            .then(Commands.argument("borderEnd", StringArgumentType.word())
                                .executes(ctx -> {
                                    int bg = parseColor(StringArgumentType.getString(ctx, "background"));
                                    int bs = parseColor(StringArgumentType.getString(ctx, "borderStart"));
                                    int be = parseColor(StringArgumentType.getString(ctx, "borderEnd"));
                                    return apply(EntityArgument.getPlayers(ctx, "targets"), bg, bs, be);
                                })
                            )
                        )
                    )
                )
        );
    }

    @SubscribeEvent
    public static void onJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer p)) return;
        int[] c = TooltipColorStore.get(p);
        PacketDistributor.sendToPlayer(p, new TooltipColorsPayload(c[0], c[1], c[2]));
    }

    private static int apply(Collection<ServerPlayer> targets, int bg, int bs, int be) {
        for (ServerPlayer p : targets) {
            TooltipColorStore.set(p, bg, bs, be);
            PacketDistributor.sendToPlayer(p, new TooltipColorsPayload(bg, bs, be));
        }
        return targets.size();
    }

    private static int applyReset(CommandContext<CommandSourceStack> ctx,
                                  Collection<ServerPlayer> targets) {
        for (ServerPlayer p : targets) {
            TooltipColorStore.reset(p);
            PacketDistributor.sendToPlayer(p, new TooltipColorsPayload(
                TooltipColors.DEFAULT_BG,
                TooltipColors.DEFAULT_BORDER,
                TooltipColors.DEFAULT_BORDER));
        }
        return targets.size();
    }

    private static int parseColor(String s) throws CommandSyntaxException {
        String hex = s.startsWith("#") ? s.substring(1) : s;
        if (hex.startsWith("0x") || hex.startsWith("0X")) hex = hex.substring(2);
        long value;
        try {
            value = Long.parseLong(hex, 16);
        } catch (NumberFormatException e) {
            throw ERR_COLOR.create();
        }
        if (hex.length() == 6) {
            value |= 0xFF000000L;
        } else if (hex.length() != 8) {
            throw ERR_COLOR.create();
        }
        return (int) value;
    }
}