package com.github.glodblock.epp.common.items;

import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import appeng.blockentity.networking.CableBusBlockEntity;
import com.github.glodblock.epp.util.FCUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;
import java.util.HashMap;

public abstract class ItemUpgrade extends Item {

    private final HashMap<Class<? extends BlockEntity>, TileEntityPair> BLOCK_MAP = new HashMap<>();
    private final HashMap<Class<? extends IPart>, IPartItem<?>> PART_MAP = new HashMap<>();

    public ItemUpgrade(Item.Properties properties) {
        super(properties);
    }

    @Nonnull
    @Override
    public InteractionResult useOn(@Nonnull UseOnContext context) {
        var side = context.getClickedFace();
        var pos = context.getClickedPos();
        var world = context.getLevel();
        var tile = world.getBlockEntity(pos);
        if (tile != null) {
            var ctx = new BlockPlaceContext(context);
            var tClazz = tile.getClass();
            if (this.BLOCK_MAP.containsKey(tClazz)) {
                var replaceInfo = this.BLOCK_MAP.get(tClazz);
                var state = replaceInfo.block.getStateForPlacement(ctx);
                var tileType = FCUtil.getTileType(replaceInfo.tile);
                assert state != null;
                var te = tileType.create(pos, state);
                FCUtil.replaceTile(world, pos, tile, te, state);
                context.getItemInHand().shrink(1);
                return InteractionResult.CONSUME;
            } else if (tile instanceof CableBusBlockEntity cable) {
                var part = cable.getPart(side);
                if (part != null && this.PART_MAP.containsKey(part.getClass())) {
                    var contents = new CompoundTag();
                    var partItem = this.PART_MAP.get(part.getClass());
                    contents.putBoolean("exae_reload", true);
                    part.writeToNBT(contents);
                    var p = cable.replacePart(partItem, side, context.getPlayer(), null);
                    if (p != null) {
                        p.readFromNBT(contents);
                    }
                }
                context.getItemInHand().shrink(1);
                return InteractionResult.sidedSuccess(world.isClientSide);
            }
        }
        return InteractionResult.PASS;
    }

    protected void addTile(Class<? extends BlockEntity> clazz, Block block, Class<? extends BlockEntity> tile) {
        this.BLOCK_MAP.put(clazz, new TileEntityPair(block, tile));
    }

    protected void addPart(Class<? extends IPart> clazz, IPartItem<?> item) {
        this.PART_MAP.put(clazz, item);
    }

    private record TileEntityPair(Block block, Class<? extends BlockEntity> tile) {

    }

}
