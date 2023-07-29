package com.github.glodblock.epp.common.items;

import appeng.blockentity.networking.CableBusBlockEntity;
import appeng.parts.automation.ExportBusPart;
import appeng.parts.automation.ImportBusPart;
import com.github.glodblock.epp.common.EPPItemAndBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemIOBusUpgrade extends Item {

    public ItemIOBusUpgrade() {
        super(new Item.Properties().tab(EPPItemAndBlock.TAB));
    }

    @Nonnull
    @Override
    public InteractionResult useOn(@Nonnull UseOnContext context) {
        var side = context.getClickedFace();
        var pos = context.getClickedPos();
        var world = context.getLevel();
        var tile = world.getBlockEntity(pos);
        if (tile instanceof CableBusBlockEntity cable) {
            var part = cable.getPart(side);
            var contents = new CompoundTag();
            contents.putBoolean("exae_reload", true);
            if (part != null && part.getClass() == ExportBusPart.class) {
                part.writeToNBT(contents);
                var p = cable.replacePart(EPPItemAndBlock.EX_EXPORT_BUS, side, context.getPlayer(), null);
                if (p != null) {
                    p.readFromNBT(contents);
                }
                context.getItemInHand().shrink(1);
                return InteractionResult.CONSUME;
            } else if (part != null && part.getClass() == ImportBusPart.class) {
                part.writeToNBT(contents);
                var p = cable.replacePart(EPPItemAndBlock.EX_IMPORT_BUS, side, context.getPlayer(), null);
                if (p != null) {
                    p.readFromNBT(contents);
                }
                context.getItemInHand().shrink(1);
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> list, @NotNull TooltipFlag tooltipFlag) {
        list.add(Component.translatable("ebus.upgrade.tooltip").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, level, list, tooltipFlag);
    }

}
