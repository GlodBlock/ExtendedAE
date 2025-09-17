package com.glodblock.github.extendedae.common.tileentities;

import appeng.api.implementations.blockentities.IColorableBlockEntity;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridNodeListener;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.UpgradeInventories;
import appeng.api.util.AECableType;
import appeng.api.util.AEColor;
import appeng.blockentity.ServerTickingBlockEntity;
import appeng.blockentity.grid.AENetworkBlockEntity;
import appeng.core.definitions.AEItems;
import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import com.glodblock.github.extendedae.common.me.wireless.WirelessConnect;
import com.glodblock.github.extendedae.common.me.wireless.WirelessNode;
import com.glodblock.github.extendedae.util.CacheHolder;
import com.glodblock.github.extendedae.xmod.jade.JadeDataProvider;
import com.glodblock.github.glodium.util.GlodUtil;
import gripe._90.megacells.definition.MEGAItems;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TileWirelessHub extends AENetworkBlockEntity implements ServerTickingBlockEntity, IUpgradeableObject, IColorableBlockEntity, JadeDataProvider {
    public static final int MAX_PORT = 8;
    private final boolean[] updateStatus = new boolean[MAX_PORT];
    private final long[] freq = new long[MAX_PORT];
    private final WirelessConnect[] connect = new WirelessConnect[MAX_PORT];
    private double powerUse;
    private final IUpgradeInventory upgrades;
    @SuppressWarnings("unchecked")
    private final CacheHolder<BlockPos>[] other = new CacheHolder[MAX_PORT];
    @NotNull
    private AEColor color = AEColor.TRANSPARENT;

    public TileWirelessHub(BlockPos pos, BlockState blockState) {
        super(GlodUtil.getTileType(TileWirelessHub.class, TileWirelessHub::new, EPPItemAndBlock.WIRELESS_HUB), pos, blockState);
        this.getMainNode().setExposedOnSides(EnumSet.allOf(Direction.class));
        this.getMainNode().setFlags(GridFlags.DENSE_CAPACITY);
        this.powerUse = 1.0;
        this.getMainNode().setIdlePowerUsage(this.powerUse);
        this.upgrades = UpgradeInventories.forMachine(EPPItemAndBlock.WIRELESS_HUB, 4, this::updatePowerUsage);
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
        for (int i = 0; i < MAX_PORT; i ++) {
            if (this.connect[i].isConnected()) {
                var dis = Math.max(this.connect[i].getDistance(), Math.E);
                this.powerUse += Math.max(1.0, dis * Math.log(dis) * disc);
            }
        }
        this.getMainNode().setIdlePowerUsage(this.powerUse);
    }

    private double calculateDisc() {
        double disc = 0.1 * this.upgrades.getInstalledUpgrades(AEItems.ENERGY_CARD);
        if (ModList.get().isLoaded("megacells")) {
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
    public void loadTag(CompoundTag data) {
        super.loadTag(data);
        this.upgrades.readFromNBT(data, "upgrades");
        for (int i = 0; i < MAX_PORT; i ++) {
            this.freq[i] = data.getLong("freq" + i);
            WirelessConnect.G.markUsed(this.freq[i]);
        }
        if (data.contains("color")) {
            this.color = AEColor.valueOf(data.getString("color"));
        } else {
            this.color = AEColor.TRANSPARENT;
        }
        this.getMainNode().setGridColor(this.color);
    }

    @Override
    public void saveAdditional(CompoundTag data) {
        super.saveAdditional(data);
        this.upgrades.writeToNBT(data, "upgrades");
        for (int i = 0; i < MAX_PORT; i ++) {
            data.putLong("freq" + i, freq[i]);
            WirelessConnect.G.markUsed(this.freq[i]);
        }
        data.putString("color", this.color.name());
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

    @Override
    public @NotNull AEColor getColor() {
        return this.color;
    }

    @Override
    public boolean recolourBlock(Direction direction, AEColor colour, Player player) {
        if (colour == this.color) {
            return false;
        }
        this.color = colour;
        this.saveChanges();
        this.markForUpdate();
        this.getMainNode().setGridColor(this.color);
        return true;
    }

    @Override
    public String jadeID() {
        return "wireless";
    }

    @Override
    public void collectJadeInfo(CompoundTag tag) {
        tag.putString("color", this.color.name());
        this.getMainNode().ifPresent((gird, node) -> tag.putInt("used", node.getUsedChannels()));
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
