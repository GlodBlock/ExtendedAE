package com.glodblock.github.ae2netanalyser.common.me.ticker;

import appeng.api.networking.IGridNode;
import appeng.parts.AEBasePart;
import com.glodblock.github.ae2netanalyser.network.AEANetworkHandler;
import com.glodblock.github.ae2netanalyser.network.packets.SProfileDataUpdate;
import com.glodblock.github.glodium.Glodium;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestBox {

    private static final Map<Player, ProfilerJob> WAITING = new ConcurrentHashMap<>();

    public static void init() {
        NeoForge.EVENT_BUS.register(RequestBox.class);
    }

    @SubscribeEvent
    public static void clear(ServerStartingEvent event) {
        WAITING.clear();
    }

    @SubscribeEvent
    public static void check(LevelTickEvent.Pre event) {
        if (hasJob()) {
            List<Player> toRemove = new ArrayList<>();
            for (var e : WAITING.entrySet()) {
                if (e.getValue().isFinished()) {
                    toRemove.add(e.getKey());
                    sendData(e.getKey(), e.getValue().generateData());
                }
            }
            for (var e : toRemove) {
                WAITING.remove(e);
            }
        }
    }

    @SubscribeEvent
    public static void playerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        WAITING.remove(event.getEntity());
    }

    public static boolean checkPermission(Player player) {
        var server = Glodium.INSTANCE.getServer();
        if (server != null) {
            if (server.isSingleplayer()) {
                return true;
            } else {
                return server.getPlayerList().isOp(player.getGameProfile());
            }
        }
        return false;
    }

    public static RespondCode requestProfile(Player player, int duration) {
        if (WAITING.containsKey(player)) {
            return RespondCode.WAIT;
        }
        if (!checkPermission(player)) {
            return RespondCode.DENY;
        }
        WAITING.put(player, new ProfilerJob(duration * 1000L * 1000L * 1000L));
        return RespondCode.OK;
    }

    public static boolean cancelProfile(Player player) {
        if (WAITING.containsKey(player)) {
            WAITING.remove(player);
            return true;
        }
        return false;
    }

    public static boolean hasJob() {
        return !WAITING.isEmpty();
    }

    public static void acceptTick(long ns, long tick, IGridNode node) {
        if (WAITING.isEmpty()) {
            return;
        }
        var world = node.getLevel();
        var host = node.getOwner();
        if (world != null && host != null && ns > 0 && tick > 0) {
            BlockPos pos = null;
            if (host instanceof BlockEntity te) {
                pos = te.getBlockPos();
            } else if (host instanceof AEBasePart part) {
                if (part.getHost() != null) {
                    pos = part.getBlockEntity().getBlockPos();
                }
            }
            if (pos != null) {
                var gp = GlobalPos.of(world.dimension(), pos);
                for (var e : WAITING.entrySet()) {
                    e.getValue().tick(gp, ns, tick);
                }
            }
        }
    }

    private static void sendData(Player player, ProfileData data) {
        if (player instanceof ServerPlayer server) {
            AEANetworkHandler.INSTANCE.sendTo(new SProfileDataUpdate(data), server);
            player.displayClientMessage(Component.translatable("chat.ae2netanalyser.tick_analyser.finish"), false);
        }
    }

    public enum RespondCode {
        OK, WAIT, DENY
    }

}
