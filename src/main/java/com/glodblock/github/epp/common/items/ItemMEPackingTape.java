package com.glodblock.github.epp.common.items;

import appeng.blockentity.networking.CableBusBlockEntity;
import appeng.util.Platform;
import com.glodblock.github.epp.common.EPPItemAndBlock;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class ItemMEPackingTape extends Item {

    private static final ObjectSet<Identifier> WHITE_LIST = new ObjectOpenHashSet<>();

    public ItemMEPackingTape() {
        super(new Item.Settings().maxDamage(64).group(EPPItemAndBlock.TAB));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        var side = context.getSide();
        var pos = context.getBlockPos();
        var world = context.getWorld();
        var tile = world.getBlockEntity(pos);
        if (tile != null) {
            var tag = new NbtCompound();
            if (tile instanceof CableBusBlockEntity cable) {
                var part = cable.getPart(side);
                if (part != null) {
                    tag.putBoolean("part", true);
                    var partItem = part.getPartItem().asItem();
                    var id = Registry.ITEM.getId(partItem);
                    if (!WHITE_LIST.contains(id)) {
                        return ActionResult.PASS;
                    }
                    tag.putString("id", id.toString());
                    var ctxTag = new NbtCompound();
                    part.writeToNBT(ctxTag);
                    ctxTag.putBoolean("exae_reload", true);
                    tag.put("ctx", ctxTag);
                    cable.removePart(part);
                }
            } else {
                tag.putBoolean("part", false);
                var state = tile.getCachedState();
                var blockId = Registry.BLOCK.getId(state.getBlock());
                if (!WHITE_LIST.contains(blockId)) {
                    return ActionResult.PASS;
                }
                var id = Registry.BLOCK_ENTITY_TYPE.getId(tile.getType());
                assert id != null;
                tag.putString("id", id.toString());
                tag.putString("block_id", blockId.toString());
                tag.put("state", NbtHelper.fromBlockState(state));
                var ctxTag = tile.createNbt();
                tag.put("ctx", ctxTag);
                world.removeBlockEntity(pos);
                world.removeBlock(pos, false);
            }
            if (!tag.isEmpty()) {
                var pack = new ItemStack(EPPItemAndBlock.PACKAGE);
                pack.setNbt(tag);
                Platform.spawnDrops(world, pos, Collections.singletonList(pack));
                context.getStack().damage(1, world.random, null);
                return ActionResult.success(world.isClient);
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public void appendTooltip(@NotNull ItemStack stack, @Nullable World level, @NotNull List<Text> list, @NotNull TooltipContext tooltipFlag) {
        list.add(Text.translatable("me_packing_tape.tooltip").formatted(Formatting.GRAY));
        super.appendTooltip(stack, level, list, tooltipFlag);
    }

    public static void registerPackableDevice(String id) {
        WHITE_LIST.add(new Identifier(id));
    }

}
