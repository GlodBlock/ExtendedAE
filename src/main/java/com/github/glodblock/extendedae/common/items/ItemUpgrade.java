package com.github.glodblock.extendedae.common.items;

import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import appeng.blockentity.networking.CableBusBlockEntity;
import appeng.parts.AEBasePart;
import com.github.glodblock.extendedae.util.FCUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public abstract class ItemUpgrade extends Item {

    private final HashMap<Class<? extends BlockEntity>, TileEntityPair> BLOCK_MAP = new HashMap<>();
    private final HashMap<Class<? extends IPart>, IPartItem<?>> PART_MAP = new HashMap<>();

    public ItemUpgrade(Item.Properties properties) {
        super(properties);
    }

    @NotNull
    @Override
    public InteractionResult useOn(@NotNull UseOnContext context) {
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
                Vec3 hitVec = context.getClickLocation();
                Vec3 hitInBlock = new Vec3(hitVec.x - pos.getX(), hitVec.y - pos.getY(), hitVec.z - pos.getZ());
                var part = cable.getCableBus().selectPartLocal(hitInBlock).part;
                if (part instanceof AEBasePart basePart && this.PART_MAP.containsKey(part.getClass())) {
                    var side = basePart.getSide();
                    var contents = new CompoundTag();
                    var partItem = this.PART_MAP.get(part.getClass());
                    part.writeToNBT(contents);
                    var p = cable.replacePart(partItem, side, context.getPlayer(), null);
                    if (p != null) {
                        p.readFromNBT(contents);
                    }
                } else {
                    return InteractionResult.PASS;
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
