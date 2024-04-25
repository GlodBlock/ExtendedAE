package com.github.glodblock.extendedae.common.tileentities;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEKeyType;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.helpers.externalstorage.GenericStackInv;
import com.github.glodblock.extendedae.common.EAEItemAndBlock;
import com.github.glodblock.extendedae.util.FCUtil;
import com.github.glodblock.extendedae.xmod.ExternalTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class TileIngredientBuffer extends AEBaseBlockEntity {

    private final GenericStackInv buffer;

    public TileIngredientBuffer(BlockPos pos, BlockState blockState) {
        super(FCUtil.getTileType(TileIngredientBuffer.class, TileIngredientBuffer::new, EAEItemAndBlock.INGREDIENT_BUFFER), pos, blockState);
        this.buffer = new GenericStackInv(this::setChanged, 36);
        this.buffer.setCapacity(AEKeyType.fluids(), 64L * AEFluidKey.AMOUNT_BUCKET);
        if (ExternalTypes.MANA != null) {
            this.buffer.setCapacity(ExternalTypes.MANA, 1000);
        }
        if (ExternalTypes.SOURCE != null) {
            this.buffer.setCapacity(ExternalTypes.SOURCE, 1000);
        }
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

    public GenericStackInv getInventory() {
        return this.buffer;
    }

}
