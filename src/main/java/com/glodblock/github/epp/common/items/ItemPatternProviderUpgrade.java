package com.glodblock.github.epp.common.items;

import appeng.blockentity.crafting.PatternProviderBlockEntity;
import appeng.blockentity.networking.CableBusBlockEntity;
import appeng.parts.crafting.PatternProviderPart;
import com.glodblock.github.epp.common.EPPItemAndBlock;
import com.glodblock.github.epp.common.tiles.TileExPatternProvider;
import com.glodblock.github.epp.util.FCUtil;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemPatternProviderUpgrade extends Item {

    public ItemPatternProviderUpgrade() {
        super(new Item.Settings().group(EPPItemAndBlock.TAB));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        var side = context.getSide();
        var pos = context.getBlockPos();
        var world = context.getWorld();
        var tile = world.getBlockEntity(pos);
        if (tile != null) {
            var ctx = new ItemPlacementContext(context);
            if (tile.getClass() == PatternProviderBlockEntity.class) {
                var state = EPPItemAndBlock.EX_PATTERN_PROVIDER.getPlacementState(ctx);
                assert state != null;
                var te = TileExPatternProvider.TYPE.instantiate(pos, state);
                FCUtil.replaceTile(world, pos, tile, te, state);
                context.getStack().decrement(1);
                return ActionResult.CONSUME;
            } else if (tile instanceof CableBusBlockEntity cable) {
                var part = cable.getPart(side);
                var contents = new NbtCompound();
                contents.putBoolean("exae_reload", true);
                if (part != null && part.getClass() == PatternProviderPart.class) {
                    part.writeToNBT(contents);
                    var p = cable.replacePart(EPPItemAndBlock.EX_PATTERN_PROVIDER_PART, side, context.getPlayer(), null);
                    if (p != null) {
                        p.readFromNBT(contents);
                    }
                }
                context.getStack().decrement(1);
                return ActionResult.CONSUME;
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public void appendTooltip(@NotNull ItemStack stack, @Nullable World level, @NotNull List<Text> list, @NotNull TooltipContext tooltipFlag) {
        list.add(Text.translatable("epp.upgrade.tooltip.01").formatted(Formatting.GRAY));
        list.add(Text.translatable("epp.upgrade.tooltip.02").formatted(Formatting.GRAY));
        super.appendTooltip(stack, level, list, tooltipFlag);
    }

}
