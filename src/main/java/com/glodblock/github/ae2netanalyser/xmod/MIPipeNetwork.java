package com.glodblock.github.ae2netanalyser.xmod;

import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IManagedGridNode;
import aztech.modern_industrialization.compat.ae2.pipe.MENetworkNode;
import aztech.modern_industrialization.pipes.api.PipeEndpointType;
import aztech.modern_industrialization.pipes.api.PipeNetwork;
import aztech.modern_industrialization.pipes.api.PipeNetworkNode;
import aztech.modern_industrialization.pipes.impl.PipeBlockEntity;
import com.glodblock.github.ae2netanalyser.common.me.NetworkData;
import com.glodblock.github.ae2netanalyser.common.me.netdata.LinkFlag;
import com.glodblock.github.ae2netanalyser.common.me.netdata.NodeFlag;
import com.glodblock.github.ae2netanalyser.common.me.netdata.State;
import com.glodblock.github.ae2netanalyser.util.ExternalNetwork;
import com.glodblock.github.glodium.reflect.ReflectKit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

import java.lang.reflect.Field;
import java.util.Set;

public class MIPipeNetwork implements ExternalNetwork {

    public static final ExternalNetwork INSTANCE = new MIPipeNetwork();
    private static Field MENetworkNode_mainNode;
    private static Field PipeNetworkNode_network;
    private static boolean CAN_LOAD = true;

    static {
        try {
            MENetworkNode_mainNode = ReflectKit.reflectField(MENetworkNode.class, "mainNode");
            PipeNetworkNode_network = ReflectKit.reflectField(PipeNetworkNode.class, "network");
        } catch (Exception ignored) {
            MENetworkNode_mainNode = null;
            PipeNetworkNode_network = null;
            CAN_LOAD = false;
        }
    }

    @Override
    public IGridNode findGridNode(Level world, BlockPos pos) {
        var tile = world.getBlockEntity(pos);
        if (tile instanceof PipeBlockEntity pipe) {
            var nodes = pipe.getNodes();
            for (var node : nodes) {
                if (node instanceof MENetworkNode) {
                    PipeNetwork network = ReflectKit.readField(node, PipeNetworkNode_network);
                    if (network != null) {
                        for (var posNode : network.iterateTickingNodes()) {
                            if (posNode.getNode() instanceof MENetworkNode) {
                                IManagedGridNode gridNode = ReflectKit.readField(posNode.getNode(), MENetworkNode_mainNode);
                                if (gridNode != null) {
                                    return gridNode.getNode();
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public NetworkData.ANode wrapGridNode(IGridNode node) {
        if (node != null && node.getOwner() instanceof MENetworkNode networkNode) {
            PipeNetwork network = ReflectKit.readField(networkNode, PipeNetworkNode_network);
            if (network != null) {
                for (var nodePos : network.iterateTickingNodes()) {
                    if (nodePos.getNode() == networkNode) {
                        var state = node.isOnline() ? new State<>(NodeFlag.NORMAL) : new State<>(NodeFlag.MISSING);
                        return new NetworkData.ANode(nodePos.getPos(), state);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void addExtraInfo(IGrid grid, Set<NetworkData.ANode> nodes, Set<NetworkData.ALink> links) {
        if (grid != null) {
            grid.getMachines(MENetworkNode.class).stream()
                    .findAny()
                    .ifPresent(pipeNode -> {
                        IManagedGridNode gridNode = ReflectKit.readField(pipeNode, MENetworkNode_mainNode);
                        PipeNetwork network = ReflectKit.readField(pipeNode, PipeNetworkNode_network);
                        var state = new State<>(NodeFlag.NORMAL);
                        if (gridNode == null || !gridNode.isOnline()) {
                            state = new State<>(NodeFlag.MISSING);
                        }
                        for (var entry : network.getRawNodeMap().entrySet()) {
                            var pos = entry.getKey();
                            var aNode = new NetworkData.ANode(pos, state);
                            nodes.add(aNode);
                            var connections = entry.getValue().getConnections(pos);
                            for (var dir : Direction.values()) {
                                if (connections[dir.get3DDataValue()] == PipeEndpointType.PIPE) {
                                    links.add(new NetworkData.ALink(aNode, new NetworkData.ANode(pos.relative(dir), state), (short) 0, new State<>(LinkFlag.NORMAL)));
                                }
                            }
                        }
                    });
        }
    }

    @Override
    public boolean isValid() {
        return CAN_LOAD;
    }

}
