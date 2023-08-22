package com.github.glodblock.epp.common.me.wireless;

import appeng.api.features.Locatables;
import appeng.api.networking.GridHelper;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionHost;
import appeng.me.service.helpers.ConnectionWrapper;
import com.github.glodblock.epp.EPP;
import com.github.glodblock.epp.common.tileentities.TileWirelessConnector;
import com.github.glodblock.epp.config.EPPConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Objects;

public class WirelessConnect implements IActionHost {

    private static final Locatables.Type<IActionHost> CONNECTORS = new Locatables.Type<>();
    private boolean isDestroyed = false;
    private boolean registered;
    private ConnectionWrapper connection;
    private long thisSide;
    private long otherSide;
    private boolean shutdown;
    private double dis;
    private TileWirelessConnector host;

    public WirelessConnect(TileWirelessConnector connector) {
        this.host = connector;
        this.registered = true;
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onUnload(final LevelEvent.Unload e) {
        if (this.host.getLevel() == e.getLevel()) {
            this.destroy();
        }
    }

    public void active() {
        MinecraftForge.EVENT_BUS.register(this);
        this.registered = true;
    }

    public void updateStatus() {
        final long f = this.host.getFrequency();
        if (this.thisSide != f && this.thisSide != -f) {
            if (f != 0) {
                if (this.thisSide != 0) {
                    CONNECTORS.unregister(host.getLevel(), this.thisSide);
                }
                if (this.canUseNode(-f)) {
                    this.otherSide = f;
                    this.thisSide = -f;
                } else if (this.canUseNode(f)) {
                    this.thisSide = f;
                    this.otherSide = -f;
                }
                CONNECTORS.register(host.getLevel(), getLocatableKey(), this);
            } else {
                CONNECTORS.unregister(host.getLevel(), getLocatableKey());
                this.otherSide = 0;
                this.thisSide = 0;
            }
        }

        var myOtherSide = this.otherSide == 0 ? null : CONNECTORS.get(host.getLevel(), this.otherSide);

        this.shutdown = false;
        this.dis = 0;

        if (myOtherSide instanceof WirelessConnect sideB) {
            var sideA = this;
            this.dis = Math.sqrt(sideA.host.getBlockPos().distSqr(sideB.host.getBlockPos()));
            if (sideA.isActive() && sideB.isActive()
                    && this.dis <= EPPConfig.wirelessMaxRange
                    && (sideA.host.getLevel() == sideB.host.getLevel())) {
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
                    sideA.connection = sideB.connection = new ConnectionWrapper(GridHelper.createConnection(sideA.getNode(), sideB.getNode()));
                } catch (IllegalStateException e) {
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
        var locatable = CONNECTORS.get(host.getLevel(), qe);
        if (locatable instanceof WirelessConnect qc) {
            var world = qc.host.getLevel();
            if (!qc.isDestroyed && world != null) {
                if (world.hasChunkAt(qc.host.getBlockPos())) {
                    final var cur = Objects.requireNonNull(world.getServer()).getLevel(world.dimension());
                    final var te = world.getBlockEntity(qc.host.getBlockPos());
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
        return this.hasFreq();
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
                MinecraftForge.EVENT_BUS.unregister(this);
                this.registered = false;
            }

            if (this.thisSide != 0) {
                this.updateStatus();
                CONNECTORS.unregister(host.getLevel(), getLocatableKey());
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
