package com.glodblock.github.extendedae.common.tileentities;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.blockentity.grid.AENetworkInvBlockEntity;
import appeng.core.definitions.AEBlocks;
import appeng.core.settings.TickRates;
import appeng.decorative.solid.BuddingCertusQuartzBlock;
import appeng.util.Platform;
import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import com.glodblock.github.glodium.util.GlodUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;


public class TileCrystalGrowthChamber extends AENetworkInvBlockEntity implements IGridTickable {
    private int progress = 0;

    public TileCrystalGrowthChamber(BlockPos pos, BlockState blockState) {
        super(GlodUtil.getTileType(TileCrystalGrowthChamber.class, TileCrystalGrowthChamber::new, EPPItemAndBlock.CRYSTAL_GROWTH_CHAMBER), pos, blockState);
        this.getMainNode()
                .setFlags()
                .setIdlePowerUsage(10)
                .addService(IGridTickable.class, this);
    }
    @Override
    public TickingRequest getTickingRequest(IGridNode node) {
        return new TickingRequest(TickRates.Charger, false, true);
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        doWork(ticksSinceLastCall);
        return TickRateModulation.SLOWER;
    }

    private void doWork(int ticksSinceLastCall) {
        BlockPos blockPos = this.getBlockPos().offset(0,1,0);
        BlockState blockState;
        try {
            blockState =  this.getLevel().getBlockState(blockPos);
        }catch (NullPointerException e){
            blockState = null;
        }
        if(blockState == null) return;
        Block block = blockState.getBlock();
        if(block.getClass().equals(BuddingCertusQuartzBlock.class) && !blockState.is(AEBlocks.FLAWLESS_BUDDING_QUARTZ.block())){
            if(this.userPower(ticksSinceLastCall * 50)>0){
                this.progress += Platform.getRandom().nextInt(5);
            }
            if(this.progress >= 100){
                if (blockState.is(AEBlocks.DAMAGED_BUDDING_QUARTZ.block())) {
                    this.getLevel().setBlockAndUpdate(blockPos,AEBlocks.CHIPPED_BUDDING_QUARTZ.block().defaultBlockState());
                } else if (blockState.is(AEBlocks.CHIPPED_BUDDING_QUARTZ.block())) {
                    this.getLevel().setBlockAndUpdate(blockPos,AEBlocks.FLAWED_BUDDING_QUARTZ.block().defaultBlockState());
                } else if (blockState.is(AEBlocks.FLAWED_BUDDING_QUARTZ.block())) {
                    this.getLevel().setBlockAndUpdate(blockPos,AEBlocks.FLAWLESS_BUDDING_QUARTZ.block().defaultBlockState());
                }
                this.progress = 0;
            }
        } else {
            this.progress = 0;
        }
    }
    public int getProgress(){
        return this.progress;
    }

    private int userPower(int value) {
        var grid = this.getGridNode().getGrid();
        if (grid != null) {
            return (int) (grid.getEnergyService().extractAEPower(value,Actionable.MODULATE, PowerMultiplier.CONFIG));
        } else {
            return 0;
        }
    }

    @Override
    public void saveAdditional(CompoundTag data) {
        super.saveAdditional(data);
        data.putInt("progress",this.progress);
    }

    @Override
    public void loadTag(CompoundTag data) {
        super.loadTag(data);
        this.progress = data.getInt("progress");
    }


    @Override
    public InternalInventory getInternalInventory() {
        return InternalInventory.empty();
    }

    @Override
    public void onChangeInventory(InternalInventory inv, int slot) {
    }
}
