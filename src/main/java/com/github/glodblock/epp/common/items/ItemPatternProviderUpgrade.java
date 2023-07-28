package com.github.glodblock.epp.common.items;

import appeng.blockentity.crafting.PatternProviderBlockEntity;
import appeng.blockentity.networking.CableBusBlockEntity;
import appeng.parts.crafting.PatternProviderPart;
import com.github.glodblock.epp.common.EPPItemAndBlock;
import com.github.glodblock.epp.common.tileentities.TileExPatternProvider;
import com.github.glodblock.epp.util.FCUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemPatternProviderUpgrade extends Item {

    public ItemPatternProviderUpgrade() {
        super(new Item.Properties());
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
            if (tile.getClass() == PatternProviderBlockEntity.class) {
                var state = EPPItemAndBlock.EX_PATTERN_PROVIDER.getStateForPlacement(ctx);
                var tileType = FCUtil.getTileType(TileExPatternProvider.class);
                assert state != null;
                var te = tileType.create(pos, state);
                FCUtil.replaceTile(world, pos, tile, te, state);
                context.getItemInHand().shrink(1);
                return InteractionResult.CONSUME;
            } else if (tile instanceof CableBusBlockEntity cable) {
                var part = cable.getPart(side);
                var contents = new CompoundTag();
                contents.putBoolean("exae_reload", true);
                if (part != null && part.getClass() == PatternProviderPart.class) {
                    part.writeToNBT(contents);
                    var p = cable.replacePart(EPPItemAndBlock.EX_PATTERN_PROVIDER_PART, side, context.getPlayer(), null);
                    if (p != null) {
                        p.readFromNBT(contents);
                    }
                }
                context.getItemInHand().shrink(1);
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> list, @NotNull TooltipFlag tooltipFlag) {
        list.add(Component.translatable("epp.upgrade.tooltip").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, level, list, tooltipFlag);
    }

}
