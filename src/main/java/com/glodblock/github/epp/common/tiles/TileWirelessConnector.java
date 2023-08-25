package com.glodblock.github.epp.common.tiles;

import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNodeListener;
import appeng.api.util.AECableType;
import appeng.blockentity.ServerTickingBlockEntity;
import appeng.blockentity.grid.AENetworkBlockEntity;
import com.glodblock.github.epp.common.EPPItemAndBlock;
import com.glodblock.github.epp.common.me.FreqGenerator;
import com.glodblock.github.epp.common.me.WirelessConnect;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.EnumSet;

// Adapt from Quantum Bridge
public class TileWirelessConnector extends AENetworkBlockEntity implements ServerTickingBlockEntity {

    public static final BlockEntityType<TileWirelessConnector> TYPE = BlockEntityType.Builder.create(TileWirelessConnector::new, EPPItemAndBlock.WIRELESS_CONNECTOR).build(null);

    private boolean updateStatus = true;
    private long freq = 0;
    private final WirelessConnect connect;
    public static final FreqGenerator G = new FreqGenerator();

    public TileWirelessConnector(BlockPos pos, BlockState blockState) {
        super(TYPE, pos, blockState);
        this.getMainNode().setExposedOnSides(EnumSet.allOf(Direction.class));
        this.getMainNode().setFlags(GridFlags.DENSE_CAPACITY);
        this.getMainNode().setIdlePowerUsage(1.0);
        this.connect = new WirelessConnect(this);
    }

    @Override
    public void serverTick() {
        if (this.updateStatus) {
            this.updateStatus = false;
            this.connect.updateStatus();
            this.updatePowerUsage();
            this.markForUpdate();
        }
    }

    public void updatePowerUsage() {
        if (this.connect.isConnected()) {
            var dis = Math.max(this.connect.getDistance(), Math.E);
            this.getMainNode().setIdlePowerUsage(Math.max(1.0, dis * Math.log(dis)));
        } else {
            this.getMainNode().setIdlePowerUsage(1.0);
        }
    }

    @Override
    public void onMainNodeStateChanged(IGridNodeListener.State reason) {
        this.updateStatus = true;
    }

    @Override
    public void onChunkUnloaded() {
        this.disconnect();
        super.onChunkUnloaded();
    }

    @Override
    public void onReady() {
        super.onReady();
        this.updateStatus = true;
    }

    @Override
    public void markRemoved() {
        this.disconnect();
        super.markRemoved();
    }

    @Override
    public void loadTag(NbtCompound data) {
        super.loadTag(data);
        this.freq = data.getLong("freq");
        G.put(this.freq);
    }

    @Override
    public void writeNbt(NbtCompound data) {
        super.writeNbt(data);
        data.putLong("freq", this.freq);
        G.put(this.freq);
    }

    public void setFreq(long freq) {
        this.freq = freq;
        this.updateStatus = true;
    }

    public void disconnect() {
        this.connect.destroy();
    }

    public boolean isConnected() {
        return this.connect.isConnected();
    }

    @Override
    public AECableType getCableConnectionType(Direction dir) {
        return AECableType.DENSE_SMART;
    }

    public void breakOnRemove() {
        this.connect.destroy();
    }

    public long getFrequency() {
        return this.freq;
    }

}
