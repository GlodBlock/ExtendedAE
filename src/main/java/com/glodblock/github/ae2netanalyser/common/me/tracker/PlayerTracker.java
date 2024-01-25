package com.glodblock.github.ae2netanalyser.common.me.tracker;

import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;

public final class PlayerTracker {

    private static final Reference2ObjectMap<Player, Tracker> TRACKERS = new Reference2ObjectOpenHashMap<>();

    public static void init() {
        ServerLifecycleEvents.SERVER_STARTING.register(PlayerTracker::clear);
    }

    public static boolean needUpdate(Player player, GlobalPos pos) {
        var time = player.level().getGameTime();
        if (TRACKERS.containsKey(player)) {
            var last = TRACKERS.get(player);
            if (!last.pos.equals(pos) || time - last.time > 100) {
                TRACKERS.put(player, new Tracker(pos, time));
                return true;
            }
            return false;
        } else {
            TRACKERS.put(player, new Tracker(pos, time));
            return true;
        }
    }

    public static void clear(MinecraftServer server) {
        TRACKERS.clear();
    }

    public record Tracker(GlobalPos pos, long time) {

    }

}
