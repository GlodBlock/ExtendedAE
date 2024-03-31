package com.glodblock.github.extendedae.common.tileentities;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.orientation.BlockOrientation;
import appeng.api.orientation.RelativeSide;
import appeng.api.util.AECableType;
import appeng.api.util.DimensionalBlockPos;
import appeng.blockentity.grid.AENetworkInvBlockEntity;
import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.util.Platform;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.filter.AEItemDefinitionFilter;
import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import com.glodblock.github.glodium.util.GlodUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumSet;
import java.util.Set;


public class TileCrystalFixer extends AENetworkInvBlockEntity implements IGridTickable {
    
    private int progress = 0;
    private final AppEngInternalInventory inv = new AppEngInternalInventory(this, 1);

    public TileCrystalFixer(BlockPos pos, BlockState blockState) {
        super(GlodUtil.getTileType(TileCrystalFixer.class, TileCrystalFixer::new, EPPItemAndBlock.CRYSTAL_FIXER), pos, blockState);
        this.getMainNode().setFlags().setIdlePowerUsage(0).addService(IGridTickable.class, this);
        this.inv.setFilter(new AEItemDefinitionFilter(AEItems.CERTUS_QUARTZ_CRYSTAL_CHARGED));
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode node) {
        return new TickingRequest(2, 10, false, false);
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        if (doWork(ticksSinceLastCall)) {
            return TickRateModulation.FASTER;
        }
        return TickRateModulation.SLOWER;
    }

    private boolean needGrowth(BlockState blockState) {
        return blockState.is(AEBlocks.DAMAGED_BUDDING_QUARTZ.block()) ||
                blockState.is(AEBlocks.CHIPPED_BUDDING_QUARTZ.block()) ||
                blockState.is(AEBlocks.QUARTZ_BLOCK.block());
    }

    private boolean doWork(int ticksSinceLastCall) {
        if (this.getLevel() == null || !checkFuel()) {
            return false;
        }
        BlockPos blockPos = this.getBlockPos().offset(this.getFront().getNormal());
        BlockState blockState = this.getLevel().getBlockState(blockPos);
        if (needGrowth(blockState)) {
            if (this.userPower(ticksSinceLastCall * 50) > 0) {
                this.progress += Platform.getRandom().nextInt(5);
                this.consumeFuel();
            }
            if (this.progress >= 100) {
                if (blockState.is(AEBlocks.QUARTZ_BLOCK.block())) {
                    this.getLevel().setBlockAndUpdate(blockPos, AEBlocks.DAMAGED_BUDDING_QUARTZ.block().defaultBlockState());
                } else if (blockState.is(AEBlocks.DAMAGED_BUDDING_QUARTZ.block())) {
                    this.getLevel().setBlockAndUpdate(blockPos, AEBlocks.CHIPPED_BUDDING_QUARTZ.block().defaultBlockState());
                } else if (blockState.is(AEBlocks.CHIPPED_BUDDING_QUARTZ.block())) {
                    this.getLevel().setBlockAndUpdate(blockPos, AEBlocks.FLAWED_BUDDING_QUARTZ.block().defaultBlockState());
                }
                this.progress = 0;
            }
            return true;
        } else {
            this.progress = 0;
            return false;
        }
    }

    private boolean checkFuel() {
        return !this.inv.getStackInSlot(0).isEmpty();
    }

    private void consumeFuel() {
        if (Platform.getRandom().nextInt(10) < 1) {
            this.inv.extractItem(0, 1, false);
        }
    }

    public int getProgress() {
        return this.progress;
    }

    private long userPower(int value) {
        if (this.getGridNode() == null) {
            return 0;
        }
        var grid = this.getGridNode().getGrid();
        if (grid != null) {
            return (long) grid.getEnergyService().extractAEPower(value, Actionable.MODULATE, PowerMultiplier.CONFIG);
        } else {
            return 0;
        }
    }

    @Override
    protected boolean readFromStream(FriendlyByteBuf data) {
        var changed = super.readFromStream(data);
        this.inv.setItemDirect(0, data.readItem());
        return changed;
    }

    @Override
    protected void writeToStream(FriendlyByteBuf data) {
        super.writeToStream(data);
        data.writeItem(this.inv.getStackInSlot(0));
    }

    @Override
    public AECableType getCableConnectionType(Direction dir) {
        return AECableType.COVERED;
    }

    @Override
    public Set<Direction> getGridConnectableSides(BlockOrientation orientation) {
        return EnumSet.of(orientation.getSide(RelativeSide.BACK));
    }

    @Override
    public void saveAdditional(CompoundTag data) {
        super.saveAdditional(data);
        data.putInt("progress", this.progress);
    }

    @Override
    public void loadTag(CompoundTag data) {
        super.loadTag(data);
        this.progress = data.getInt("progress");
    }

    @Override
    public InternalInventory getInternalInventory() {
        return this.inv;
    }

    @Override
    public void onChangeInventory(InternalInventory inv, int slot) {
        this.markForUpdate();
    }

    public void refuel(Player player) {
        if (!Platform.hasPermissions(new DimensionalBlockPos(this), player)) {
            return;
        }
        var playerInv = player.getInventory();
        ItemStack held = playerInv.getSelected();
        if (held.isEmpty()) {
            var stuff = this.inv.extractItem(0, Integer.MAX_VALUE, false);
            if (!stuff.isEmpty()) {
                playerInv.placeItemBackInInventory(stuff);
            }
        } else {
            var notAdded = this.inv.insertItem(0, held, false);
            playerInv.setItem(playerInv.selected, notAdded);
        }
    }

}
