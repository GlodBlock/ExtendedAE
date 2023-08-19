package com.glodblock.github.epp.common.items;

import appeng.api.parts.IPartItem;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.parts.PartPlacement;
import appeng.util.Platform;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemPackedDevice extends Item {

    public ItemPackedDevice() {
        super(new Item.Settings());
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        // NO-OP
    }

    @Override
    public void appendTooltip(@NotNull ItemStack is, @Nullable World level, @NotNull List<Text> list, @NotNull TooltipContext tooltipFlag) {
        if (is.hasNbt()) {
            var tag = is.getNbt();
            assert tag != null;
            if (checkNBT(tag)) {
                if (tag.getBoolean("part")) {
                    var id = new Identifier(tag.getString("id"));
                    if (Registry.ITEM.containsId(id)) {
                        var item = Registry.ITEM.get(id);
                        var name = Platform.getItemDisplayName(item, new NbtCompound());
                        list.add(Text.translatable("packaged_device.tooltip", name).formatted(Formatting.GRAY));
                        return;
                    }
                } else {
                    var id = new Identifier(tag.getString("block_id"));
                    if (Registry.BLOCK.containsId(id)) {
                        var item = Registry.BLOCK.get(id);
                        var name = Platform.getItemDisplayName(item.asItem(), new NbtCompound());
                        list.add(Text.translatable("packaged_device.tooltip", name).formatted(Formatting.GRAY));
                        return;
                    }
                }
            }
        }
        list.add(Text.translatable("packaged_device.error.tooltip").formatted(Formatting.RED));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        var side = context.getSide();
        var pos = context.getBlockPos();
        var world = context.getWorld();
        var pack = context.getStack();
        if (!pack.hasNbt()) {
            return ActionResult.FAIL;
        }
        var ctx = pack.getNbt();
        assert ctx != null;
        if (!checkNBT(ctx)) {
            return ActionResult.FAIL;
        }
        if (ctx.getBoolean("part")) {
            var id = new Identifier(ctx.getString("id"));
            if (Registry.ITEM.containsId(id)) {
                var item = Registry.ITEM.get(id);
                if (item instanceof IPartItem<?> partItem) {
                    var placement = PartPlacement.getPartPlacement(context.getPlayer(), world, new ItemStack(partItem), pos, side);
                    if (placement != null) {
                        var part = PartPlacement.placePart(context.getPlayer(), world, partItem, null, placement.pos(), placement.side());
                        if (part != null) {
                            part.readFromNBT(ctx.getCompound("ctx"));
                            pack.decrement(1);
                            return ActionResult.success(world.isClient);
                        } else {
                            Platform.sendImmediateBlockEntityUpdate(context.getPlayer(), pos);
                        }
                    }
                }
            }
            return ActionResult.PASS;
        } else {
            var id = new Identifier(ctx.getString("id"));
            if (Registry.BLOCK_ENTITY_TYPE.containsId(id)) {
                var block = Registry.BLOCK_ENTITY_TYPE.get(id);
                var state = NbtHelper.toBlockState(ctx.getCompound("state"));
                var item = state.getBlock().asItem();
                if (item instanceof BlockItem blockItem && context.getPlayer() != null) {
                    var ctxB = new ItemPlacementContext(world, context.getPlayer(), context.getHand(), new ItemStack(blockItem), context.hit);
                    ctxB = blockItem.getPlacementContext(ctxB);
                    if (ctxB != null && block != null && blockItem.place(ctxB) != ActionResult.FAIL) {
                        var posNew = ctxB.getBlockPos();
                        var te = block.instantiate(posNew, state);
                        if (te != null) {
                            world.setBlockState(posNew, state, 3);
                            world.addBlockEntity(te);
                            te.readNbt(ctx.getCompound("ctx"));
                            if (te instanceof AEBaseBlockEntity aeTile) {
                                aeTile.markForUpdate();
                            } else {
                                te.markDirty();
                            }
                            pack.decrement(1);
                            return ActionResult.success(world.isClient);
                        }
                    }
                }
            }
        }
        return ActionResult.PASS;
    }

    private boolean checkNBT(NbtCompound content) {
        return content.contains("part") && content.contains("id") && content.contains("ctx");
    }

}
