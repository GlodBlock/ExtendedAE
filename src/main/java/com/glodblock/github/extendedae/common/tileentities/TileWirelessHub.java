package com.glodblock.github.extendedae.common.tileentities;

import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridNodeListener;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.UpgradeInventories;
import appeng.api.util.AECableType;
import appeng.blockentity.ServerTickingBlockEntity;
import appeng.blockentity.grid.AENetworkedBlockEntity;
import appeng.core.definitions.AEItems;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.common.me.wireless.WirelessConnect;
import com.glodblock.github.extendedae.common.me.wireless.WirelessNode;
import com.glodblock.github.extendedae.config.EAEConfig;
import com.glodblock.github.extendedae.util.CacheHolder;
import com.glodblock.github.extendedae.xmod.ModConstants;
import com.glodblock.github.glodium.util.GlodUtil;
import gripe._90.megacells.definition.MEGAItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

public class TileWirelessHub extends AENetworkedBlockEntity implements ServerTickingBlockEntity, IUpgradeableObject {

    public static final int MAX_PORT = 8;
    private final boolean[] updateStatus = new boolean[MAX_PORT];
    private final long[] freq = new long[MAX_PORT];
    private final WirelessConnect[] connect = new WirelessConnect[MAX_PORT];
    private double powerUse;
    private final IUpgradeInventory upgrades;
    @SuppressWarnings("unchecked")
    private final CacheHolder<BlockPos>[] other = new CacheHolder[MAX_PORT];

    public TileWirelessHub(BlockPos pos, BlockState blockState) {
        super(GlodUtil.getTileType(TileWirelessHub.class, TileWirelessHub::new, EAESingletons.WIRELESS_HUB), pos, blockState);
        this.getMainNode().setExposedOnSides(EnumSet.allOf(Direction.class));
        this.getMainNode().setFlags(GridFlags.DENSE_CAPACITY);
        this.powerUse = 1.0;
        this.getMainNode().setIdlePowerUsage(this.powerUse);
        this.upgrades = UpgradeInventories.forMachine(EAESingletons.WIRELESS_HUB, 4, this::updatePowerUsage);
        for (int i = 0; i < MAX_PORT; i ++) {
            this.updateStatus[i] = true;
            this.connect[i] = new WirelessConnect(new Stock(this, i));
            this.other[i] = CacheHolder.empty();
        }
    }

    public int allocatePort() {
        for (int i = 0; i < MAX_PORT; i ++) {
            if (!this.connect[i].isConnected()) {
                return i;
            }
        }
        return -1;
    }

    public void killPort(int port) {
        this.freq[port] = 0;
        this.connect[port].destroy();
        this.connect[port] = new WirelessConnect(new Stock(this, port));
        this.updateStatus[port] = true;
        this.setChanged();
    }

    public long getFrequency(int port) {
        return this.freq[port];
    }

    @Override
    public void serverTick() {
        boolean changed = false;
        for (int i = 0; i < MAX_PORT; i ++) {
            if (this.updateStatus[i]) {
                this.updateStatus[i] = false;
                this.other[i].expired();
                this.connect[i].updateStatus();
                this.reactive(i);
                changed = true;
            }
        }
        if (changed) {
            this.updatePowerUsage();
            this.markForUpdate();
        }
    }

    public void updatePowerUsage() {
        var disc = 1 - this.calculateDisc();
        this.powerUse = 0;
        boolean anyRunning = false;
        for (int i = 0; i < MAX_PORT; i ++) {
            if (this.connect[i].isConnected()) {
                var dis = Math.max(this.connect[i].getDistance(), Math.E);
                this.powerUse += Math.max(1.0, dis * Math.log(dis) * disc) * EAEConfig.wirelessPowerMultiplier;
                anyRunning = true;
            }
        }
        if (!anyRunning) {
            this.powerUse = EAEConfig.wirelessPowerMultiplier;
        }
        this.getMainNode().setIdlePowerUsage(this.powerUse);
    }

    private double calculateDisc() {
        double disc = 0.1 * this.upgrades.getInstalledUpgrades(AEItems.ENERGY_CARD);
        if (GlodUtil.checkMod(ModConstants.MEGA)) {
            disc += 0.2 * this.upgrades.getInstalledUpgrades(MEGAItems.GREATER_ENERGY_CARD);
        }
        return disc;
    }

    public double getPowerUse() {
        return this.powerUse;
    }

    @Nullable
    public BlockPos getOtherSide(int port) {
        if (this.connect[port].isConnected()) {
            if (!this.other[port].isValid()) {
                this.other[port].update(this.connect[port].getOtherSide());
            }
            return this.other[port].get();
        }
        return null;
    }

    @Override
    public void onMainNodeStateChanged(IGridNodeListener.State reason) {
        Arrays.fill(this.updateStatus, true);
    }

    public void reactive(int port) {
        this.connect[port].active();
    }

    @Override
    public void onChunkUnloaded() {
        this.disconnectAll();
        super.onChunkUnloaded();
    }

    @Override
    public void onReady() {
        super.onReady();
        Arrays.fill(this.updateStatus, true);
    }

    @Override
    public void setRemoved() {
        this.disconnectAll();
        super.setRemoved();
    }

    @Override
    public void loadTag(CompoundTag data, HolderLookup.Provider registries) {
        super.loadTag(data, registries);
        this.upgrades.readFromNBT(data, "upgrades", registries);
        for (int i = 0; i < MAX_PORT; i ++) {
            this.freq[i] = data.getLong("freq" + i);
            WirelessConnect.G.markUsed(this.freq[i]);
        }
    }

    @Override
    public void saveAdditional(CompoundTag data, HolderLookup.Provider registries) {
        super.saveAdditional(data, registries);
        this.upgrades.writeToNBT(data, "upgrades", registries);
        for (int i = 0; i < MAX_PORT; i ++) {
            data.putLong("freq" + i, freq[i]);
            WirelessConnect.G.markUsed(this.freq[i]);
        }
    }

    public void setFrequency(long freq, int port) {
        this.freq[port] = freq;
        this.updateStatus[port] = true;
        this.setChanged();
    }

    public long getNewFreq() {
        return WirelessConnect.G.genFreq();
    }

    public void disconnectAll() {
        for (int i = 0; i < MAX_PORT; i ++) {
            this.connect[i].destroy();
        }
    }

    public boolean isConnected() {
        for (int i = 0; i < MAX_PORT; i ++) {
            if (this.connect[i].isConnected()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public AECableType getCableConnectionType(Direction dir) {
        return AECableType.DENSE_SMART;
    }

    public void breakOnRemove() {
        for (int i = 0; i < MAX_PORT; i ++) {
            this.connect[i].destroy();
        }
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

    @Override
    public void clearContent() {
        super.clearContent();
        this.upgrades.clear();
    }

    private record Stock(TileWirelessHub hub, int port) implements WirelessNode {

        @Override
        public long getFrequency() {
            return this.hub.freq[this.port];
        }

        @Override
        public Level getLevel() {
            return this.hub.getLevel();
        }

        @Override
        public BlockPos getBlockPos() {
            return this.hub.getBlockPos();
        }

        @Override
        public IGridNode getGridNode() {
            return this.hub.getGridNode();
        }

        @Override
        public BlockEntity getBlockEntity() {
            return this.hub;
        }

    }

}
