package com.glodblock.github.epp.common.items;

import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import appeng.blockentity.networking.CableBusBlockEntity;
import com.glodblock.github.epp.util.FCUtil;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;

import java.util.HashMap;

public class ItemUpgrade extends Item {

    private final HashMap<Class<? extends BlockEntity>, TileEntityPair> BLOCK_MAP = new HashMap<>();
    private final HashMap<Class<? extends IPart>, IPartItem<?>> PART_MAP = new HashMap<>();

    public ItemUpgrade(Item.Settings properties) {
        super(properties);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        var side = context.getSide();
        var pos = context.getBlockPos();
        var world = context.getWorld();
        var tile = world.getBlockEntity(pos);
        if (tile != null) {
            var ctx = new ItemPlacementContext(context);
            var tClazz = tile.getClass();
            if (this.BLOCK_MAP.containsKey(tClazz)) {
                var replaceInfo = this.BLOCK_MAP.get(tClazz);
                var state = replaceInfo.block.getPlacementState(ctx);
                assert state != null;
                var te = replaceInfo.tile.instantiate(pos, state);
                FCUtil.replaceTile(world, pos, tile, te, state);
                context.getStack().decrement(1);
                return ActionResult.success(world.isClient);
            } else if (tile instanceof CableBusBlockEntity cable) {
                var part = cable.getPart(side);
                if (part != null && this.PART_MAP.containsKey(part.getClass())) {
                    var contents = new NbtCompound();
                    var partItem = this.PART_MAP.get(part.getClass());
                    contents.putBoolean("exae_reload", true);
                    part.writeToNBT(contents);
                    var p = cable.replacePart(partItem, side, context.getPlayer(), null);
                    if (p != null) {
                        p.readFromNBT(contents);
                    }
                }
                context.getStack().decrement(1);
                return ActionResult.success(world.isClient);
            }
        }
        return ActionResult.PASS;
    }

    protected void addTile(Class<? extends BlockEntity> clazz, Block block, BlockEntityType<?> tile) {
        this.BLOCK_MAP.put(clazz, new TileEntityPair(block, tile));
    }

    protected void addPart(Class<? extends IPart> clazz, IPartItem<?> item) {
        this.PART_MAP.put(clazz, item);
    }

    private record TileEntityPair(Block block, BlockEntityType<?> tile) {

    }

}
