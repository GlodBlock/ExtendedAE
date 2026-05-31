package com.glodblock.github.extendedae.common.items;

import appeng.api.parts.IPartItem;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.items.AEBaseItem;
import appeng.parts.PartPlacement;
import appeng.util.Platform;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.EAESingletons;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.TagValueInput;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class ItemPackedDevice extends AEBaseItem {

    public ItemPackedDevice(Properties props) {
        super(props.stacksTo(1));
    }

    @Override
    public void addToMainCreativeTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        // NO-OP
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(@NotNull ItemStack is, @NotNull TooltipContext context, @NotNull TooltipDisplay tooltipDisplay, @NotNull Consumer<Component> lines, @NotNull TooltipFlag tooltipFlags) {
        super.appendHoverText(is, context, tooltipDisplay, lines, tooltipFlags);
        if (is.has(EAESingletons.IS_PART)) {
            boolean isPart = Boolean.TRUE.equals(is.get(EAESingletons.IS_PART));
            if (isPart) {
                var data = is.get(EAESingletons.TAPE_PART_DATA);
                if (data != null) {
                    var item = BuiltInRegistries.ITEM.get(data.id());
                    if (item.isPresent() && !item.get().is(Items.AIR.builtInRegistryHolder())) {
                        var name = new ItemStack(item.get()).getDisplayName();
                        lines.accept(Component.translatable("packaged_device.tooltip", name).withStyle(ChatFormatting.GRAY));
                        return;
                    }
                }
            } else {
                var data = is.get(EAESingletons.TAPE_TILE_DATA);
                if (data != null) {
                    var item = BuiltInRegistries.BLOCK.get(data.block());
                    if (item.isPresent() && !item.get().is(Blocks.AIR.builtInRegistryHolder())) {
                        var name = new ItemStack(item.get().value()).getDisplayName();
                        lines.accept(Component.translatable("packaged_device.tooltip", name).withStyle(ChatFormatting.GRAY));
                        return;
                    }
                }
            }
        }
        lines.accept(Component.translatable("packaged_device.error.tooltip").withStyle(ChatFormatting.RED));
    }

    @Nonnull
    @Override
    public InteractionResult useOn(@Nonnull UseOnContext context) {
        var side = context.getClickedFace();
        var pos = context.getClickedPos();
        var world = context.getLevel();
        var pack = context.getItemInHand();
        if (!pack.has(EAESingletons.IS_PART)) {
            return InteractionResult.FAIL;
        }
        boolean isPart = Boolean.TRUE.equals(pack.get(EAESingletons.IS_PART));
        if (isPart) {
            var data = pack.get(EAESingletons.TAPE_PART_DATA);
            if (data == null) {
                return InteractionResult.FAIL;
            }
            var item = BuiltInRegistries.ITEM.get(data.id());
            if (item.isPresent() && item.get().value() instanceof IPartItem<?> partItem) {
                var placement = PartPlacement.getPartPlacement(context.getPlayer(), world, new ItemStack(partItem), pos, side, context.getClickLocation());
                if (placement != null) {
                    var part = PartPlacement.placePart(context.getPlayer(), world, partItem, null, placement.pos(), placement.side());
                    if (part != null) {
                        var contents = data.context();
                        contents.putBoolean("BYPASS_EXTENDEDAE", true);
                        try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(ExtendedAE.LOGGER)) {
                            var input = TagValueInput.create(reporter, world.registryAccess(), contents);
                            part.readFromNBT(input);
                            part.addToWorld();
                            pack.shrink(1);
                            return InteractionResult.SUCCESS;
                        }
                    } else {
                        Platform.sendImmediateBlockEntityUpdate(context.getPlayer(), pos);
                    }
                }
            }
            return InteractionResult.PASS;
        } else {
            var data = pack.get(EAESingletons.TAPE_TILE_DATA);
            if (data == null) {
                return InteractionResult.FAIL;
            }
            var block = BuiltInRegistries.BLOCK_ENTITY_TYPE.get(data.id());
            if (block.isPresent()) {
                var state = NbtUtils.readBlockState(world.holderLookup(Registries.BLOCK), data.state());
                var item = state.getBlock().asItem();
                if (item instanceof BlockItem blockItem && context.getPlayer() != null) {
                    var ctxB = new BlockPlaceContext(world, context.getPlayer(), context.getHand(), new ItemStack(blockItem), context.hitResult);
                    ctxB = blockItem.updatePlacementContext(ctxB);
                    if (ctxB != null && blockItem.place(ctxB) != InteractionResult.FAIL) {
                        var posNew = ctxB.getClickedPos();
                        var te = block.get().value().create(posNew, state);
                        world.setBlock(posNew, state, 3);
                        world.setBlockEntity(te);
                        try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(ExtendedAE.LOGGER)) {
                            var input =  TagValueInput.create(reporter, world.registryAccess(), data.context());
                            te.loadWithComponents(input);
                            if (te instanceof AEBaseBlockEntity aeTile) {
                                aeTile.markForUpdate();
                            } else {
                                te.setChanged();
                            }
                            pack.shrink(1);
                            return InteractionResult.SUCCESS;
                        }
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }

}
