package com.glodblock.github.extendedae.common.tileentities;

import appeng.api.stacks.AEKeyType;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.capabilities.Capabilities;
import appeng.helpers.externalstorage.GenericStackInv;
import appeng.util.SettingsFrom;
import appeng.util.helpers.ItemComparisonHelper;
import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import com.glodblock.github.extendedae.xmod.ExternalTypes;
import com.glodblock.github.glodium.util.GlodUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TileIngredientBuffer extends AEBaseBlockEntity {

    private final GenericStackInv buffer;

    public TileIngredientBuffer(BlockPos pos, BlockState blockState) {
        super(GlodUtil.getTileType(TileIngredientBuffer.class, TileIngredientBuffer::new, EPPItemAndBlock.INGREDIENT_BUFFER), pos, blockState);
        this.buffer = new GenericStackInv(this::setChanged, 36);
        this.buffer.setCapacity(AEKeyType.fluids(), 64000);
        if (ExternalTypes.GAS != null) {
            this.buffer.setCapacity(ExternalTypes.GAS, 64000);
        }
        if (ExternalTypes.MANA != null) {
            this.buffer.setCapacity(ExternalTypes.MANA, 1000);
        }
        if (ExternalTypes.FLUX != null) {
            this.buffer.setCapacity(ExternalTypes.FLUX, 10000);
        }
        if (ExternalTypes.SOURCE != null) {
            this.buffer.setCapacity(ExternalTypes.SOURCE, 1000);
        }
    }

    @Override
    public InteractionResult disassembleWithWrench(Player player, Level level, BlockHitResult hitResult, ItemStack wrench) {
        var pos = hitResult.getBlockPos();
        var state = level.getBlockState(pos);
        var block = state.getBlock();

        if (level instanceof ServerLevel serverLevel) {
            // Drops of the block itself (without extra block entity inventory)
            var drops = Block.getDrops(state, serverLevel, pos, this, player, wrench);

            var op = new ItemStack(state.getBlock());
            for (var ol : drops) {
                if (ItemComparisonHelper.isEqualItemType(ol, op)) {
                    var tag = new CompoundTag();
                    exportSettings(SettingsFrom.DISMANTLE_ITEM, tag, player);
                    if (!tag.isEmpty()) {
                        ol.setTag(tag);
                    }
                    break;
                }
            }
            for (var item : drops) {
                player.getInventory().placeItemBackInInventory(item);
            }
        }

        block.playerWillDestroy(level, pos, state, player);
        level.removeBlock(pos, false);
        block.destroy(level, pos, getBlockState());

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public void addAdditionalDrops(Level level, BlockPos pos, List<ItemStack> drops) {
        super.addAdditionalDrops(level, pos, drops);
        for (int index = 0; index < this.buffer.size(); index ++) {
            var stack = this.buffer.getStack(index);
            if (stack != null) {
                stack.what().addDrops(stack.amount(), drops, level, pos);
            }
        }
    }

    @Override
    public void saveAdditional(CompoundTag data) {
        super.saveAdditional(data);
        this.buffer.writeToChildTag(data, "buffer");
    }

    @Override
    public void loadTag(CompoundTag data) {
        super.loadTag(data);
        this.buffer.readFromChildTag(data, "buffer");
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction facing) {
        if (capability == Capabilities.GENERIC_INTERNAL_INV) {
            return LazyOptional.of(() -> this.buffer).cast();
        }
        return super.getCapability(capability, facing);
    }

    public GenericStackInv getInventory() {
        return this.buffer;
    }

}
