package com.glodblock.github.appflux.common.me.strategy;

import appeng.api.behaviors.ContainerItemStrategy;
import appeng.api.config.Actionable;
import appeng.api.stacks.GenericStack;
import com.glodblock.github.appflux.api.ItemContext;
import com.glodblock.github.appflux.common.me.key.FluxKey;
import com.glodblock.github.appflux.common.me.key.type.EnergyType;
import com.glodblock.github.appflux.common.me.strategy.context.CarriedContext;
import com.glodblock.github.appflux.common.me.strategy.context.PlayerInvContext;
import com.glodblock.github.appflux.util.AFUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class FEContainerItemStrategy implements ContainerItemStrategy<FluxKey, ItemContext> {

    @Override
    public @Nullable GenericStack getContainedStack(ItemStack stack) {
        var energy = AFUtil.findCapability(stack, Capabilities.EnergyStorage.ITEM);
        if (energy != null && energy.getEnergyStored() > 0) {
            return new GenericStack(FluxKey.of(EnergyType.FE), energy.getEnergyStored());
        }
        return null;
    }

    @Override
    public @Nullable ItemContext findCarriedContext(Player player, AbstractContainerMenu menu) {
        if (menu.getCarried().getCapability(Capabilities.EnergyStorage.ITEM) != null) {
            return new CarriedContext(player, menu);
        }
        return null;
    }

    @Override
    public @Nullable ItemContext findPlayerSlotContext(Player player, int slot) {
        if (player.getInventory().getItem(slot).getCapability(Capabilities.EnergyStorage.ITEM) != null) {
            return new PlayerInvContext(player, slot);
        }
        return null;
    }

    @Override
    public long extract(ItemContext context, FluxKey what, long amount, Actionable mode) {
        var stack = context.getStack();
        var copy = stack.copyWithCount(1);
        var handler = AFUtil.findCapability(copy, Capabilities.EnergyStorage.ITEM);
        if (handler == null) {
            return 0;
        }
        int extracted = handler.extractEnergy(AFUtil.clampLong(amount), mode.isSimulate());
        if (mode == Actionable.MODULATE) {
            stack.shrink(1);
            context.addOverflow(copy);
        }
        return extracted;
    }

    @Override
    public long insert(ItemContext context, FluxKey what, long amount, Actionable mode) {
        var stack = context.getStack();
        var copy = stack.copyWithCount(1);
        var handler = AFUtil.findCapability(copy, Capabilities.EnergyStorage.ITEM);
        if (handler == null) {
            return 0;
        }

        int filled = handler.receiveEnergy(AFUtil.clampLong(amount), mode.isSimulate());
        if (mode == Actionable.MODULATE) {
            stack.shrink(1);
            context.addOverflow(copy);
        }
        return filled;
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
    public @Nullable GenericStack getExtractableContent(ItemContext context) {
        return this.getContainedStack(context.getStack());
    }

}
