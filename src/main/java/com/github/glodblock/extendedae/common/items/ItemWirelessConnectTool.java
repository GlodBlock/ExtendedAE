package com.github.glodblock.extendedae.common.items;

import appeng.util.InteractionUtil;
import com.github.glodblock.extendedae.EAE;
import com.github.glodblock.extendedae.common.EAEItemAndBlock;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemWirelessConnectTool extends Item {

    public ItemWirelessConnectTool() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Level world, @NotNull List<Component> lines, @NotNull TooltipFlag adv){
        var nbt = stack.hasTag() ? stack.getTag() : new CompoundTag();
        assert nbt != null;
        var freq = nbt.getLong("freq");
        var globalPos = GlobalPos.CODEC.decode(NbtOps.INSTANCE, nbt.get("bind"))
                .resultOrPartial(Util.prefix("Connector position", EAE.LOGGER::error))
                .map(Pair::getFirst)
                .orElse(null);
        BlockPos pos = globalPos != null ? globalPos.pos() : BlockPos.ZERO;
        if (freq != 0) {
            lines.add(Component.translatable("wireless.tooltip", pos.getX(), pos.getY(), pos.getZ()).withStyle(ChatFormatting.GRAY));
            lines.add(Component.translatable("wireless.use.tooltip.02").withStyle(ChatFormatting.GRAY));
        } else {
            lines.add(Component.translatable("wireless.use.tooltip.01").withStyle(ChatFormatting.GRAY));
        }
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        var stack = player.getItemInHand(hand);
        if (InteractionUtil.isInAlternateUseMode(player) && stack.getItem() == EAEItemAndBlock.WIRELESS_TOOL) {
            stack.setTag(null);
            player.displayClientMessage(Component.translatable("chat.wireless_connect.clear"), true);
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }
        return InteractionResultHolder.pass(stack);
    }

}
