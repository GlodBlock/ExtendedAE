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
import appeng.blockentity.grid.AENetworkedBlockEntity;
import appeng.core.definitions.AEItems;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.common.me.wireless.WirelessConnect;
import com.glodblock.github.extendedae.common.me.wireless.WirelessNode;
import com.glodblock.github.extendedae.config.EAEConfig;
import com.glodblock.github.extendedae.util.CacheHolder;
import com.glodblock.github.extendedae.xmod.jade.JadeDataProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;

// Adapt from Quantum Bridge code
public class TileWirelessConnector extends AENetworkedBlockEntity implements ServerTickingBlockEntity, IUpgradeableObject, IColorableBlockEntity, WirelessNode, JadeDataProvider {

    private boolean updateStatus = true;
    private long freq = 0;
    private final WirelessConnect connect;
    private double powerUse;
    private final IUpgradeInventory upgrades;
    private final CacheHolder<BlockPos> other = CacheHolder.empty();
    @NotNull
    private AEColor color = AEColor.TRANSPARENT;

    public TileWirelessConnector(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        this.getMainNode().setExposedOnSides(EnumSet.allOf(Direction.class));
        this.getMainNode().setFlags(GridFlags.DENSE_CAPACITY);
        this.powerUse = 1.0;
        this.getMainNode().setIdlePowerUsage(this.powerUse);
        this.connect = new WirelessConnect(this);
        this.upgrades = UpgradeInventories.forMachine(EAESingletons.WIRELESS_CONNECTOR, 4, this::updatePowerUsage);
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

    @Override
    public IGridNode getGridNode() {
        return this.getMainNode().getNode();
    }

    public void updatePowerUsage() {
        var disc = 1 - this.calculateDisc();
        if (this.connect.isConnected()) {
            var dis = Math.max(this.connect.getDistance(), Math.E);
            this.powerUse = Math.max(1.0, dis * Math.log(dis) * disc);
            this.powerUse *= EAEConfig.wirelessPowerMultiplier;
        } else {
            this.powerUse = EAEConfig.wirelessPowerMultiplier;
        }
        this.getMainNode().setIdlePowerUsage(this.powerUse);
    }

    private double calculateDisc() {
        return 0.1 * this.upgrades.getInstalledUpgrades(AEItems.ENERGY_CARD);
    }

    public double getPowerUse() {
        return this.powerUse;
    }

    @Nullable
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
    public void loadTag(ValueInput data) {
        super.loadTag(data);
        this.freq = data.getLongOr("freq", 0);
        this.upgrades.readFromNBT(data, "upgrades");
        data.getString("color").ifPresentOrElse(s -> this.color = AEColor.valueOf(s), () -> this.color = AEColor.TRANSPARENT);
        this.getMainNode().setGridColor(this.color);
        WirelessConnect.G.markUsed(this.freq);
    }

    @Override
    public void saveAdditional(ValueOutput data) {
        super.saveAdditional(data);
        data.putLong("freq", this.freq);
        this.upgrades.writeToNBT(data, "upgrades");
        data.putString("color", this.color.name());
        WirelessConnect.G.markUsed(this.freq);
    }

    public void setFrequency(long freq) {
        this.freq = freq;
        this.updateStatus = true;
        this.setChanged();
    }

    public long getNewFreq() {
        return WirelessConnect.G.genFreq();
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

    @Override
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
    public boolean recolourBlock(Direction side, AEColor colour, Player who) {
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
        this.getMainNode().ifPresent((_, node) -> tag.putInt("used", node.getUsedChannels()));
    }

    @Override
    public void preRemoveSideEffects(BlockPos blockPos, BlockState blockState) {
        super.preRemoveSideEffects(blockPos, blockState);
        this.breakOnRemove();
    }

}
