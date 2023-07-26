package com.github.glodblock.epp.common.items;

import appeng.blockentity.misc.InterfaceBlockEntity;
import appeng.blockentity.networking.CableBusBlockEntity;
import appeng.parts.misc.InterfacePart;
import com.github.glodblock.epp.common.EPPItemAndBlock;
import com.github.glodblock.epp.common.tileentities.TileExInterface;
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

public class ItemInterfaceUpgrade extends Item {


    public ItemInterfaceUpgrade() {
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
            if (tile.getClass() == InterfaceBlockEntity.class) {
                var state = EPPItemAndBlock.EX_INTERFACE.getStateForPlacement(ctx);
                var tileType = FCUtil.getTileType(TileExInterface.class);
                var contents = tile.serializeNBT();
                assert state != null;
                world.removeBlockEntity(pos);
                world.removeBlock(pos, false);
                var te = tileType.create(pos, state);
                world.setBlock(pos, state, 3);
                assert te != null;
                world.setBlockEntity(te);
                te.deserializeNBT(contents);
                te.markForUpdate();
                context.getItemInHand().shrink(1);
                return InteractionResult.CONSUME;
            } else if (tile instanceof CableBusBlockEntity cable) {
                var part = cable.getPart(side);
                var contents = new CompoundTag();
                if (part != null && part.getClass() == InterfacePart.class) {
                    part.writeToNBT(contents);
                    var p = cable.replacePart(EPPItemAndBlock.EX_INTERFACE_PART, side, context.getPlayer(), null);
                    if (p != null) {
                        p.readFromNBT02(contents);
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
        list.add(Component.translatable("ei.upgrade.tooltip").setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
        super.appendHoverText(stack, level, list, tooltipFlag);
    }


}
