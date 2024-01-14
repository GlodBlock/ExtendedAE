package com.glodblock.github.ae2netanalyser.common.me.tracker;

import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class PlayerTracker {

    private static final Reference2ObjectMap<Player, Tracker> TRACKERS = new Reference2ObjectOpenHashMap<>();

    public static void init() {
        MinecraftForge.EVENT_BUS.register(PlayerTracker.class);
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

    @SubscribeEvent
    public static void clear(ServerStartingEvent event) {
        TRACKERS.clear();
    }

    @SubscribeEvent
    public static void playerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        remove(event.getEntity());
    }

    @SubscribeEvent
    public static void playChangeWorld(PlayerEvent.PlayerChangedDimensionEvent event) {
        remove(event.getEntity());
    }

    @SubscribeEvent
    public static void playRespawn(PlayerEvent.PlayerRespawnEvent event) {
        remove(event.getEntity());
    }

    private static void remove(Player player) {
        TRACKERS.remove(player);
    }

    public record Tracker(GlobalPos pos, long time) {

    }

}
