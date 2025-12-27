package com.glodblock.github.ae2netanalyser.common.me.ticker;

import appeng.api.networking.IGridNode;
import appeng.parts.AEBasePart;
import com.glodblock.github.ae2netanalyser.network.AEANetworkHandler;
import com.glodblock.github.ae2netanalyser.network.packets.SProfileDataUpdate;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

import java.util.ArrayList;
import java.util.List;

public class RequestBox {

    private static final Object2ReferenceMap<Player, ProfilerJob> WAITING = new Object2ReferenceOpenHashMap<>();

    public static void init() {
        NeoForge.EVENT_BUS.register(RequestBox.class);
    }

    @SubscribeEvent
    public static void clear(ServerStartingEvent event) {
        WAITING.clear();
    }

    public static boolean requestProfile(Player player, int duration) {
        if (WAITING.containsKey(player)) {
            return false;
        }
        WAITING.put(player, new ProfilerJob(duration));
        return true;
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
                List<Player> toRemove = new ArrayList<>();
                for (var e : WAITING.entrySet()) {
                    if (e.getValue().tick(gp, ns, tick)) {
                        toRemove.add(e.getKey());
                        sendData(e.getKey(), e.getValue().generateData());
                    }
                }
                for (var player : toRemove) {
                    WAITING.remove(player);
                }
            }
        }
    }

    private static void sendData(Player player, ProfileData data) {
        if (player instanceof ServerPlayer server) {
            AEANetworkHandler.INSTANCE.sendTo(new SProfileDataUpdate(data), server);
        }
    }

}
