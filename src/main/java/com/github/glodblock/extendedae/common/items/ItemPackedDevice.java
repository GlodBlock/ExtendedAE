package com.github.glodblock.extendedae.common.items;

import appeng.api.parts.IPartItem;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.items.AEBaseItem;
import appeng.parts.PartPlacement;
import appeng.util.Platform;
import com.github.glodblock.extendedae.util.Ae2Reflect;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemPackedDevice extends AEBaseItem {

    public ItemPackedDevice() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public void addToMainCreativeTab(CreativeModeTab.Output output) {
    }

    @Override
    public void appendHoverText(@NotNull ItemStack is, Level world, @NotNull List<Component> lines, @NotNull TooltipFlag adv) {
        if (is.hasTag()) {
            var tag = is.getTag();
            assert tag != null;
            if (checkNBT(tag)) {
                if (tag.getBoolean("part")) {
                    var item = BuiltInRegistries.ITEM.get(new ResourceLocation(tag.getString("id")));
                    if (item != Items.AIR) {
                        var name = Platform.getItemDisplayName(item, new CompoundTag());
                        lines.add(Component.translatable("packaged_device.tooltip", name).withStyle(ChatFormatting.GRAY));
                        return;
                    }
                } else {
                    var item = BuiltInRegistries.BLOCK.get(new ResourceLocation(tag.getString("block_id")));
                    if (item != Blocks.AIR) {
                        var name = Platform.getItemDisplayName(item.asItem(), new CompoundTag());
                        lines.add(Component.translatable("packaged_device.tooltip", name).withStyle(ChatFormatting.GRAY));
                        return;
                    }
                }
            }
        }
        lines.add(Component.translatable("packaged_device.error.tooltip").withStyle(ChatFormatting.RED));
    }

    @NotNull
    @Override
    public InteractionResult useOn(@NotNull UseOnContext context) {
        var side = context.getClickedFace();
        var pos = context.getClickedPos();
        var world = context.getLevel();
        var pack = context.getItemInHand();
        if (!pack.hasTag()) {
            return InteractionResult.FAIL;
        }
        var ctx = pack.getTag();
        assert ctx != null;
        if (!checkNBT(ctx)) {
            return InteractionResult.FAIL;
        }
        if (ctx.getBoolean("part")) {
            var item = BuiltInRegistries.ITEM.get(new ResourceLocation(ctx.getString("id")));
            if (item != Items.AIR) {
                if (item instanceof IPartItem<?> partItem) {
                    var placement = PartPlacement.getPartPlacement(context.getPlayer(), world, new ItemStack(partItem), pos, side, context.getClickLocation());
                    if (placement != null) {
                        var part = PartPlacement.placePart(context.getPlayer(), world, partItem, null, placement.pos(), placement.side());
                        if (part != null) {
                            part.readFromNBT(ctx.getCompound("ctx"));
                            pack.shrink(1);
                            return InteractionResult.sidedSuccess(world.isClientSide);
                        } else {
                            Platform.sendImmediateBlockEntityUpdate(context.getPlayer(), pos);
                        }
                    }
                }
            }
            return InteractionResult.PASS;
        } else {
            var block = BuiltInRegistries.BLOCK_ENTITY_TYPE.get(new ResourceLocation(ctx.getString("id")));
            if (block != null) {
                var state = NbtUtils.readBlockState(world.holderLookup(Registries.BLOCK), ctx.getCompound("state"));
                var item = state.getBlock().asItem();
                if (item instanceof BlockItem blockItem && context.getPlayer() != null) {
                    var ctxB = new BlockPlaceContext(world, context.getPlayer(), context.getHand(), new ItemStack(blockItem), context.hitResult);
                    ctxB = blockItem.updatePlacementContext(ctxB);
                    if (ctxB != null && blockItem.place(ctxB) != InteractionResult.FAIL) {
                        var posNew = ctxB.getClickedPos();
                        var te = block.create(posNew, state);
                        if (te != null) {
                            world.setBlock(posNew, state, 3);
                            world.setBlockEntity(te);
                            te.load(ctx.getCompound("ctx"));
                            if (te instanceof AEBaseBlockEntity aeTile) {
                                aeTile.markForUpdate();
                            } else {
                                te.setChanged();
                            }
                            pack.shrink(1);
                            return InteractionResult.sidedSuccess(world.isClientSide);
                        }
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }

    private boolean checkNBT(CompoundTag content) {
        return content.contains("part") && content.contains("id") && content.contains("ctx");
    }

}
