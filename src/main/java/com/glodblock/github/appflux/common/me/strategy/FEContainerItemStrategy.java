package com.glodblock.github.appflux.common.me.strategy;

import appeng.api.behaviors.ContainerItemStrategy;
import appeng.api.config.Actionable;
import appeng.api.stacks.GenericStack;
import com.glodblock.github.appflux.common.me.key.FluxKey;
import com.glodblock.github.appflux.common.me.key.type.EnergyType;
import com.glodblock.github.appflux.util.AFUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class FEContainerItemStrategy implements ContainerItemStrategy<FluxKey, EnergyHandler> {

    @Override
    public @Nullable GenericStack getContainedStack(ItemStack stack) {
        var energy = AFUtil.findCapability(stack, Capabilities.Energy.ITEM);
        if (energy != null && energy.getAmountAsLong() > 0) {
            return new GenericStack(FluxKey.of(EnergyType.FE), energy.getAmountAsLong());
        }
        return null;
    }

    @Override
    public @Nullable EnergyHandler findCarriedContext(Player player, AbstractContainerMenu menu) {
        var itemAccess = ItemAccess.forPlayerCursor(player, menu);
        return itemAccess.getCapability(Capabilities.Energy.ITEM);
    }

    @Override
    public @Nullable EnergyHandler findPlayerSlotContext(Player player, int slot) {
        var itemAccess = ItemAccess.forPlayerSlot(player, slot);
        return itemAccess.getCapability(Capabilities.Energy.ITEM);
    }

    @Override
    public long extract(EnergyHandler context, FluxKey what, long amount, Actionable mode) {
        try (var tx = Transaction.openRoot()) {
            var extracted = context.extract(AFUtil.clampLong(amount), tx);
            if (mode == Actionable.MODULATE) {
                tx.commit();
            }
            return extracted;
        }
    }

    @Override
    public long insert(EnergyHandler context, FluxKey what, long amount, Actionable mode) {
        try (var tx = Transaction.openRoot()) {
            var inserted = context.insert(AFUtil.clampLong(amount), tx);
            if (mode == Actionable.MODULATE) {
                tx.commit();
            }
            return inserted;
        }
    }

    @Override
    public void playFillSound(Player player, FluxKey what) {
        // NO-OP
    }

    @Override
    public void playEmptySound(Player player, FluxKey what) {
        // NO-OP
    }

    @Override
    public @Nullable GenericStack getExtractableContent(EnergyHandler context) {
        var stored = context.getAmountAsLong();
        if (stored > 0) {
            return new GenericStack(FluxKey.of(EnergyType.FE), stored);
        }
        return null;
    }

}
