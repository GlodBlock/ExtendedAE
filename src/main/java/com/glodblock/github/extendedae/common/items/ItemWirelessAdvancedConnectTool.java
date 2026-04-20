package com.glodblock.github.extendedae.common.items;

import appeng.util.InteractionUtil;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import com.glodblock.github.extendedae.config.EPPConfig;
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

import javax.annotation.Nonnull;
import java.util.List;

public class ItemWirelessAdvancedConnectTool extends Item {
    public ItemWirelessAdvancedConnectTool() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Level world, @NotNull List<Component> lines, @NotNull TooltipFlag adv) {
        var nbt = stack.hasTag() ? stack.getTag() : new CompoundTag();

        assert nbt != null;

        var addMode = nbt.getBoolean("addMode");
        var connections = nbt.getList("connections", CompoundTag.TAG_COMPOUND);
        var connectionsSize = connections.size();

        lines.add(Component.translatable("wireless_advanced.mode", Component.translatable(addMode ? "wireless_advanced.mode.add" : "wireless_advanced.mode.use")).withStyle(ChatFormatting.GRAY));
        lines.add(Component.translatable("wireless.use.tooltip.%s".formatted(addMode ? "01" : "02")).withStyle(ChatFormatting.GRAY));

        if (connectionsSize > 0) {
            lines.add(Component.empty());
            lines.add(Component.translatable("wireless_advanced.port_list", connectionsSize, EPPConfig.wirelessMaxQueueSize));

            for (int i = 0; i < Math.min(connectionsSize, 10); i++) {
                var connection = connections.getCompound(i);

                var globalPos = connection.contains("bind") ? GlobalPos.CODEC.decode(NbtOps.INSTANCE, connection.get("bind"))
                    .resultOrPartial(Util.prefix("Connector position", ExtendedAE.LOGGER::error))
                    .map(Pair::getFirst)
                    .orElse(null) : null;

                BlockPos pos = globalPos != null ? globalPos.pos() : BlockPos.ZERO;

                if (connection.contains("freq")) {
                    lines.add(Component.translatable("wireless.tooltip", pos.getX(), pos.getY(), pos.getZ()).withStyle(ChatFormatting.GRAY));
                }
            }

            if (connectionsSize > 10) {
                lines.add(Component.nullToEmpty("§7... (%s)".formatted(connectionsSize - 10)));
            }
        }
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        var stack = player.getItemInHand(hand);
        if (InteractionUtil.isInAlternateUseMode(player) && stack.getItem() == EPPItemAndBlock.WIRELESS_ADVANCED_TOOL) {
            if (!level.isClientSide) {
                var nbt = stack.getOrCreateTag();
                var addMode = !nbt.getBoolean("addMode");

                nbt.putBoolean("addMode", addMode);
                player.displayClientMessage(Component.translatable("wireless_advanced.mode.swapped", Component.translatable(addMode ? "wireless_advanced.mode.add" : "wireless_advanced.mode.use")), true);
            }

            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }

        return InteractionResultHolder.pass(stack);
    }
}
