package com.glodblock.github.extendedae.common.items;

import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import appeng.api.parts.PartHelper;
import appeng.block.AEBaseEntityBlock;
import appeng.blockentity.networking.CableBusBlockEntity;
import appeng.parts.AEBasePart;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.util.FCUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.function.Supplier;

public abstract class ItemUpgrade extends Item {

    private final HashMap<Class<? extends BlockEntity>, TileEntityPair> BLOCK_MAP = new HashMap<>();
    private final HashMap<Class<? extends IPart>, Supplier<? extends IPartItem<?>>> PART_MAP = new HashMap<>();

    public ItemUpgrade(Properties properties) {
        super(properties);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Nonnull
    @Override
    public InteractionResult useOn(@Nonnull UseOnContext context) {
        var pos = context.getClickedPos();
        var world = context.getLevel();
        var tile = world.getBlockEntity(pos);
        if (!(world instanceof ServerLevel serverLevel)) {
            return InteractionResult.PASS;
        }
        if (tile != null) {
            var ctx = new BlockPlaceContext(context);
            var tClazz = tile.getClass();
            if (this.BLOCK_MAP.containsKey(tClazz)) {
                var originState = world.getBlockState(pos);
                var replaceInfo = this.BLOCK_MAP.get(tClazz);
                var state = replaceInfo.block.get().getStateForPlacement(ctx);
                var tileType = replaceInfo.tile.get();
                if (state == null) {
                    return InteractionResult.PASS;
                }
                for (var sp : originState.getValues().toList()) {
                    var pt = sp.property();
                    var va = sp.value();
                    try {
                        if (state.hasProperty(pt)) {
                            state = state.<Comparable, Comparable>setValue((Property) pt, va);
                        }
                    } catch (Exception ignore) {
                        // NO-OP
                    }
                }
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
                    var partItem = this.PART_MAP.get(part.getClass());
                    try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(ExtendedAE.LOGGER)) {
                        var output = TagValueOutput.createWithContext(reporter, world.registryAccess());
                        part.writeToNBT(output);
                        var p = PartHelper.setPart(serverLevel, pos, side, context.getPlayer(), partItem.get());
                        if (p != null) {
                            var contents = output.buildResult();
                            contents.putBoolean("BYPASS_EXTENDEDAE", true);
                            var input = TagValueInput.create(reporter, world.registryAccess(), contents);
                            p.readFromNBT(input);
                            p.addToWorld();
                        }
                    }
                } else {
                    return InteractionResult.PASS;
                }
                context.getItemInHand().shrink(1);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    protected void addTile(Class<? extends BlockEntity> clazz, Supplier<? extends AEBaseEntityBlock<?>> block) {
        this.BLOCK_MAP.put(clazz, new TileEntityPair(block::get, () -> block.get().getBlockEntityType()));
    }

    protected void addPart(Class<? extends IPart> clazz, Supplier<? extends IPartItem<?>> item) {
        this.PART_MAP.put(clazz, item);
    }

    private record TileEntityPair(Supplier<Block> block, Supplier<BlockEntityType<?>> tile) {

    }

}
