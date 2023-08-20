package com.glodblock.github.epp.common.me;

import appeng.api.exceptions.FailedConnectionException;
import appeng.api.features.Locatables;
import appeng.api.networking.GridHelper;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionHost;
import appeng.me.service.helpers.ConnectionWrapper;
import com.glodblock.github.epp.EPP;
import com.glodblock.github.epp.common.tiles.TileWirelessConnector;
import com.glodblock.github.epp.config.EPPConfig;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class WirelessConnect implements IActionHost {

    private static final Set<WirelessConnect> ACTIVE_CONNECTOR = new HashSet<>();
    private static final Locatables.Type<IActionHost> CONNECTORS = new Locatables.Type<>();
    private boolean isDestroyed = false;
    private boolean registered;
    private ConnectionWrapper connection;
    private long thisSide;
    private long otherSide;
    private boolean shutdown;
    private double dis;
    private TileWirelessConnector host;

    static {
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> ACTIVE_CONNECTOR.clear());
        ServerWorldEvents.UNLOAD.register(
                (server, world) -> ACTIVE_CONNECTOR.forEach(o -> o.onUnload(world))
        );
    }

    public WirelessConnect(TileWirelessConnector connector) {
        this.host = connector;
        this.registered = true;
        ACTIVE_CONNECTOR.add(this);
    }

    private void onUnload(ServerWorld world) {
        if (this.host.getWorld() == world) {
            this.destroy();
        }
    }

    public void updateStatus() {
        final long f = this.host.getFrequency();
        if (this.thisSide != f && this.thisSide != -f) {
            if (f != 0) {
                if (this.thisSide != 0) {
                    CONNECTORS.unregister(host.getWorld(), this.thisSide);
                }
                if (this.canUseNode(-f)) {
                    this.otherSide = f;
                    this.thisSide = -f;
                } else if (this.canUseNode(f)) {
                    this.thisSide = f;
                    this.otherSide = -f;
                }
                CONNECTORS.register(host.getWorld(), getLocatableKey(), this);
            } else {
                CONNECTORS.unregister(host.getWorld(), getLocatableKey());
                this.otherSide = 0;
                this.thisSide = 0;
            }
        }

        var myOtherSide = this.otherSide == 0 ? null : CONNECTORS.get(host.getWorld(), this.otherSide);

        this.shutdown = false;
        this.dis = 0;

        if (myOtherSide instanceof WirelessConnect sideB) {
            var sideA = this;
            this.dis = Math.sqrt(sideA.host.getPos().getSquaredDistance(sideB.host.getPos()));
            if (sideA.isActive() && sideB.isActive()
                    && this.dis <= EPPConfig.INSTANCE.wirelessConnectorMaxRange
                    && (sideA.host.getWorld() == sideB.host.getWorld())) {
                if (this.connection != null && this.connection.getConnection() != null) {
                    final IGridNode a = this.connection.getConnection().a();
                    final IGridNode b = this.connection.getConnection().b();
                    final IGridNode sa = sideA.getNode();
                    final IGridNode sb = sideB.getNode();
                    if ((a == sa || b == sa) && (a == sb || b == sb)) {
                        return;
                    }
                }

                try {
                    if (sideA.connection != null && sideA.connection.getConnection() != null) {
                        sideA.connection.getConnection().destroy();
                        sideA.connection = new ConnectionWrapper(null);
                    }
                    if (sideB.connection != null && sideB.connection.getConnection() != null) {
                        sideB.connection.getConnection().destroy();
                        sideB.connection = new ConnectionWrapper(null);
                    }
                    sideA.connection = sideB.connection = new ConnectionWrapper(GridHelper.createGridConnection(sideA.getNode(), sideB.getNode()));
                } catch (FailedConnectionException e) {
                    EPP.LOGGER.debug(e.getMessage());
                }
            } else {
                this.shutdown = true;
            }
        } else {
            this.shutdown = true;
        }

        if (this.shutdown && this.connection != null && this.connection.getConnection() != null) {
            this.connection.getConnection().destroy();
            this.connection.setConnection(null);
            this.connection = new ConnectionWrapper(null);
        }
    }

    public double getDistance() {
        return this.dis;
    }

    public boolean isConnected() {
        return !this.shutdown;
    }

    @SuppressWarnings("deprecation")
    private boolean canUseNode(long qe) {
        var locatable = CONNECTORS.get(host.getWorld(), qe);
        if (locatable instanceof WirelessConnect qc) {
            var world = qc.host.getWorld();
            if (!qc.isDestroyed && world != null) {
                if (world.isChunkLoaded(qc.host.getPos())) {
                    final var cur = Objects.requireNonNull(world.getServer()).getWorld(world.getRegistryKey());
                    final var te = world.getBlockEntity(qc.host.getPos());
                    return te != qc.host || world != cur;
                } else {
                    EPP.LOGGER.warn(String.format("Found a registered Wireless Connector with serial %s whose chunk seems to be unloaded: %s", qe, qc));
                }
            }
        }
        return true;
    }

    private boolean isActive() {
        if (this.isDestroyed || !this.registered) {
            return false;
        }
        return this.host.isPowered() && this.hasFreq();
    }

    private IGridNode getNode() {
        return this.host.getGridNode();
    }

    private boolean hasFreq() {
        return this.thisSide != 0;
    }

    public void destroy() {
        if (this.isDestroyed) {
            return;
        }
        this.isDestroyed = true;
        try {
            if (this.registered) {
                ACTIVE_CONNECTOR.remove(this);
                this.registered = false;
            }

            if (this.thisSide != 0) {
                this.updateStatus();
                CONNECTORS.unregister(host.getWorld(), getLocatableKey());
            }
            this.host = null;
        } catch (Exception ignore) {
        }
    }

    private long getLocatableKey() {
        return this.thisSide;
    }

    @Override
    public IGridNode getActionableNode() {
        return host.getMainNode().getNode();
    }

}
