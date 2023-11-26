package com.github.glodblock.extendedae.common.tileentities;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.AEKeyTypes;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.helpers.externalstorage.GenericStackInv;
import com.github.glodblock.extendedae.common.EAEItemAndBlock;
import com.github.glodblock.extendedae.util.FCUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class TileIngredientBuffer extends AEBaseBlockEntity {

    private final GenericStackInv buffer;

    private static AEKeyType MEK;
    private static AEKeyType BOT;

    static {
        try {
            MEK = AEKeyTypes.get(new ResourceLocation("appmek:chemical"));
        } catch (Exception e) {
            MEK = null;
        }
        try {
            BOT = AEKeyTypes.get(new ResourceLocation("appbot:mana"));
        } catch (Exception e) {
            BOT = null;
        }
    }

    public TileIngredientBuffer(BlockPos pos, BlockState blockState) {
        super(FCUtil.getTileType(TileIngredientBuffer.class, TileIngredientBuffer::new, EAEItemAndBlock.INGREDIENT_BUFFER), pos, blockState);
        this.buffer = new GenericStackInv(this::setChanged, 36);
        this.buffer.setCapacity(AEKeyType.fluids(), 64L * AEFluidKey.AMOUNT_BUCKET);
        if (MEK != null) {
            this.buffer.setCapacity(MEK, 64000);
        }
        if (BOT != null) {
            this.buffer.setCapacity(BOT, 1000);
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
