package com.glodblock.github.extendedae.common.items;

import appeng.blockentity.networking.CableBusBlockEntity;
import appeng.util.Platform;
import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.Collections;

public class ItemMEPackingTape extends Item {

    private static final ObjectSet<ResourceLocation> WHITE_LIST = new ObjectOpenHashSet<>();

    public ItemMEPackingTape() {
        super(new Item.Properties().durability(64));
    }

    @Nonnull
    @Override
    public InteractionResult useOn(@Nonnull UseOnContext context) {
        var pos = context.getClickedPos();
        var world = context.getLevel();
        var tile = world.getBlockEntity(pos);
        var player = context.getPlayer();
        if (tile != null && player != null) {
            var tag = new CompoundTag();
            if (tile instanceof CableBusBlockEntity cable) {
                Vec3 hitVec = context.getClickLocation();
                Vec3 hitInBlock = new Vec3(hitVec.x - pos.getX(), hitVec.y - pos.getY(), hitVec.z - pos.getZ());
                var part = cable.getCableBus().selectPartLocal(hitInBlock).part;
                if (part != null) {
                    tag.putBoolean("part", true);
                    var partItem = part.getPartItem().asItem();
                    var id = ForgeRegistries.ITEMS.getKey(partItem);
                    if (!WHITE_LIST.contains(id)) {
                        return InteractionResult.PASS;
                    }
                    assert id != null;
                    tag.putString("id", id.toString());
                    var ctxTag = new CompoundTag();
                    part.writeToNBT(ctxTag);
                    tag.put("ctx", ctxTag);
                    cable.removePart(part);
                }
            } else {
                tag.putBoolean("part", false);
                var state = tile.getBlockState();
                var blockId = ForgeRegistries.BLOCKS.getKey(state.getBlock());
                if (!WHITE_LIST.contains(blockId)) {
                    return InteractionResult.PASS;
                }
                var id = ForgeRegistries.BLOCK_ENTITY_TYPES.getKey(tile.getType());
                assert id != null;
                assert blockId != null;
                tag.putString("id", id.toString());
                tag.putString("block_id", blockId.toString());
                tag.put("state", NbtUtils.writeBlockState(state));
                var ctxTag = tile.serializeNBT();
                tag.put("ctx", ctxTag);
                world.removeBlockEntity(pos);
                world.removeBlock(pos, false);
            }
            if (!tag.isEmpty()) {
                var pack = new ItemStack(EPPItemAndBlock.PACKAGE);
                pack.setTag(tag);
                Platform.spawnDrops(world, pos, Collections.singletonList(pack));
                context.getItemInHand().hurtAndBreak(1, player, e -> e.broadcastBreakEvent(context.getHand()));
                return InteractionResult.sidedSuccess(world.isClientSide);
            }
        }
        return InteractionResult.PASS;
    }

    public static void registerPackableDevice(ResourceLocation id) {
        WHITE_LIST.add(id);
    }

}
