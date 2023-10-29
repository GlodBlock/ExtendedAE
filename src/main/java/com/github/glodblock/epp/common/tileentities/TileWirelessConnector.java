package com.github.glodblock.epp.common.tileentities;

import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNodeListener;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.UpgradeInventories;
import appeng.api.util.AECableType;
import appeng.blockentity.ServerTickingBlockEntity;
import appeng.blockentity.grid.AENetworkBlockEntity;
import appeng.core.definitions.AEItems;
import com.github.glodblock.epp.common.EPPItemAndBlock;
import com.github.glodblock.epp.common.me.FreqGenerator;
import com.github.glodblock.epp.common.me.wireless.WirelessConnect;
import com.github.glodblock.epp.util.CacheHolder;
import com.github.glodblock.epp.util.FCUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumSet;
import java.util.List;

// Adapt from Quantum Bridge code
public class TileWirelessConnector extends AENetworkBlockEntity implements ServerTickingBlockEntity, IUpgradeableObject {

    private boolean updateStatus = true;
    private long freq = 0;
    private final WirelessConnect connect;
    private double powerUse;
    private final IUpgradeInventory upgrades;
    private final CacheHolder<BlockPos> other = CacheHolder.empty();
    private static final FreqGenerator<Long> G = FreqGenerator.createLong();

    public TileWirelessConnector(BlockPos pos, BlockState blockState) {
        super(FCUtil.getTileType(TileWirelessConnector.class, TileWirelessConnector::new, EPPItemAndBlock.WIRELESS_CONNECTOR), pos, blockState);
        this.getMainNode().setExposedOnSides(EnumSet.allOf(Direction.class));
        this.getMainNode().setFlags(GridFlags.DENSE_CAPACITY);
        this.powerUse = 1.0;
        this.getMainNode().setIdlePowerUsage(this.powerUse);
        this.connect = new WirelessConnect(this);
        this.upgrades = UpgradeInventories.forMachine(EPPItemAndBlock.WIRELESS_CONNECTOR, 4, this::updatePowerUsage);
    }

    @Override
    public void serverTick() {
        if (this.updateStatus) {
            this.updateStatus = false;
            this.other.expired();
            this.connect.updateStatus();
            this.updatePowerUsage();
            this.markForUpdate();
            this.reactive();
        }
    }

    public void updatePowerUsage() {
        var disc = 1 - 0.1 * this.upgrades.getInstalledUpgrades(AEItems.ENERGY_CARD);
        if (this.connect.isConnected()) {
            var dis = Math.max(this.connect.getDistance(), Math.E);
            this.powerUse = Math.max(1.0, dis * Math.log(dis) * disc);
        } else {
            this.powerUse = 1.0;
        }
        this.getMainNode().setIdlePowerUsage(this.powerUse);
    }

    public double getPowerUse() {
        return this.powerUse;
    }

    public BlockPos getOtherSide() {
        if (this.connect.isConnected()) {
            if (!this.other.isValid()) {
                this.other.update(this.connect.getOtherSide());
            }
            return this.other.get();
        }
        return null;
    }

    @Override
    public void onMainNodeStateChanged(IGridNodeListener.State reason) {
        this.updateStatus = true;
    }

    public void reactive() {
        this.connect.active();
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
    public void setRemoved() {
        this.disconnect();
        super.setRemoved();
    }

    @Override
    public void loadTag(CompoundTag data) {
        super.loadTag(data);
        this.freq = data.getLong("freq");
        this.upgrades.readFromNBT(data, "upgrades");
        G.markUsed(this.freq);
    }

    @Override
    public void saveAdditional(CompoundTag data) {
        super.saveAdditional(data);
        data.putLong("freq", this.freq);
        this.upgrades.writeToNBT(data, "upgrades");
        G.markUsed(this.freq);
    }

    public void setFreq(long freq) {
        this.freq = freq;
        this.updateStatus = true;
    }

    public long getNewFreq() {
        return G.genFreq();
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

    @Override
    public IUpgradeInventory getUpgrades() {
        return this.upgrades;
    }

    @Override
    public void addAdditionalDrops(Level level, BlockPos pos, List<ItemStack> drops) {
        super.addAdditionalDrops(level, pos, drops);
        for (var card : this.upgrades) {
            drops.add(card);
        }
    }

}
