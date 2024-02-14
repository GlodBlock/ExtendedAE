package com.glodblock.github.ae2netanalyser.common.me.p2p;

import appeng.api.networking.IGrid;
import appeng.api.parts.IPartItem;
import appeng.me.service.P2PService;
import appeng.parts.p2p.P2PTunnelPart;
import com.glodblock.github.ae2netanalyser.common.me.p2p.p2pdata.P2PLocation;
import com.glodblock.github.ae2netanalyser.util.AeReflect;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class GridP2PCache {

    private final Object2ObjectMap<P2PLocation, P2PTunnelPart<?>> cache = new Object2ObjectOpenHashMap<>();
    private final ObjectSet<P2PLocation> updateSet = new ObjectOpenHashSet<>();
    private final IGrid grid;
    private final Player player;
    @Nullable
    private ResourceLocation type;

    public GridP2PCache(IGrid grid, Player player, @Nullable ResourceLocation type) {
        this.grid = grid;
        this.player = player;
        this.type = type;
        this.init();
    }

    private void init() {
        synchronized (this.cache) {
            this.cache.clear();
            this.updateSet.clear();
            for (var clazz : this.grid.getMachineClasses()) {
                if (P2PTunnelPart.class.isAssignableFrom(clazz)) {
                    var objs = this.grid.getMachines(clazz);
                    for (var obj : objs) {
                        var p2p = (P2PTunnelPart<?>) obj;
                        if (this.checkType(p2p)) {
                            this.cache.put(P2PLocation.of(p2p), p2p);
                        }
                    }
                }
            }
        }
    }

    public List<P2PNode> get() {
        this.init();
        return this.cache.values().stream()
                .map(p2p -> checkType(p2p) ? P2PNode.of(p2p) : null)
                .filter(Objects::nonNull)
                .toList();
    }

    public List<P2PNode> getUpdate() {
        var set = this.updateSet.stream()
                .map(this.cache::get)
                .filter(Objects::nonNull)
                .map(P2PNode::of)
                .toList();
        this.updateSet.clear();
        return set;
    }

    public void markDirty(P2PLocation pos, P2PTunnelPart<?> p2p) {
        synchronized (this.cache) {
            if (this.checkType(p2p)) {
                this.cache.put(pos, p2p);
                this.updateSet.add(pos);
            }
        }
    }

    public Pair<P2PTunnelPart<?>, P2PTunnelPart<?>> linkP2P(P2PLocation a, P2PLocation b) {
        var input = this.cache.get(a);
        var output = this.cache.get(b);
        if (input == null || output == null) {
            return null;
        }
        if (input.getClass() != output.getClass()) {
            output = changeP2PType(output, IPartItem.getId(input.getPartItem()));
        }
        if (output == null || input == output || input.getMainNode() == null) {
            return null;
        }
        var freq = input.getFrequency();
        var grid = input.getMainNode().getGrid();
        if (grid == null) {
            return null;
        }
        var service = P2PService.get(grid);

        if (freq == 0 || input.isOutput()) {
            freq = service.newFrequency();
        }

        if (service.getInput(freq) != null) {
            var originInput = service.getInput(freq);
            if (originInput != input) {
                this.updateP2P(P2PLocation.of(originInput), originInput, freq, true, input.getName());
            }
        }

        var inputResult = this.updateP2P(a, input, freq, false, input.getName());
        var outputResult = this.updateP2P(b, output, freq, true, input.getName());

        return ObjectObjectImmutablePair.of(inputResult, outputResult);
    }



    private P2PTunnelPart<?> updateP2P(P2PLocation pos, P2PTunnelPart<?> p2p, short freq, boolean output, Component name) {
        AeReflect.setP2POutput(p2p, output);
        p2p.getMainNode().ifPresent(grid -> P2PService.get(grid).updateFreq(p2p, freq));
        AeReflect.setCustomName(p2p, name);
        p2p.onTunnelNetworkChange();
        markDirty(pos, p2p);
        return p2p;
    }

    private P2PTunnelPart<?> changeP2PType(P2PTunnelPart<?> p2p, ResourceLocation newType) {
        var oldType = IPartItem.getId(p2p.getPartItem());
        if (oldType.equals(newType)) {
            return null;
        }
        var cable = p2p.getHost();
        cable.removePartFromSide(p2p.getSide());
        var newPart = cable.addPart(IPartItem.byId(newType), p2p.getSide(), this.player);
        if (newPart instanceof P2PTunnelPart<?> newP2P) {
            AeReflect.setP2POutput(newP2P, p2p.isOutput());
            newP2P.onTunnelNetworkChange();
            newP2P.getMainNode().ifPresent(grid -> P2PService.get(grid).updateFreq(newP2P, p2p.getFrequency()));
            return newP2P;
        }
        return null;
    }

    public boolean checkType(@Nullable P2PTunnelPart<?> p2p) {
        if (p2p == null) {
            return false;
        }
        if (this.type != null) {
            var p2pType = IPartItem.getId(p2p.getPartItem());
            return this.type.equals(p2pType);
        }
        return true;
    }

    public Player getPlayer() {
        return this.player;
    }

}
