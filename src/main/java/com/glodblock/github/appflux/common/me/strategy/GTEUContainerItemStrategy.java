package com.glodblock.github.appflux.common.me.strategy;

import appeng.api.config.Actionable;
import appeng.api.stacks.GenericStack;
import com.glodblock.github.appflux.api.ItemContext;
import com.glodblock.github.appflux.common.me.key.FluxKey;
import com.glodblock.github.appflux.common.me.key.type.EnergyType;
import com.glodblock.github.appflux.common.me.strategy.context.CarriedContext;
import com.glodblock.github.appflux.common.me.strategy.context.PlayerInvContext;
import com.glodblock.github.appflux.util.AFUtil;
import com.gregtechceu.gtceu.api.capability.forge.GTCapability;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

public class GTEUContainerItemStrategy {

    public static final GTEUContainerItemStrategy BRIDGE = new GTEUContainerItemStrategy();

    public @Nullable GenericStack getContainedStack(ItemStack stack) {
        var energy = AFUtil.findCapability(stack, GTCapability.CAPABILITY_ELECTRIC_ITEM);
        if (energy != null && energy.getCharge() > 0) {
            return new GenericStack(FluxKey.of(EnergyType.GTEU), energy.getCharge());
        }
        return null;
    }

    public @Nullable ItemContext findCarriedContext(Player player, AbstractContainerMenu menu) {
        if (menu.getCarried().getCapability(GTCapability.CAPABILITY_ELECTRIC_ITEM).isPresent()) {
            return new CarriedContext(player, menu);
        }
        return null;
    }

    public @Nullable ItemContext findPlayerSlotContext(Player player, int slot) {
        if (player.getInventory().getItem(slot).getCapability(GTCapability.CAPABILITY_ELECTRIC_ITEM).isPresent()) {
            return new PlayerInvContext(player, slot);
        }
        return null;
    }

    public long extract(ItemContext context, FluxKey what, long amount, Actionable mode) {
        var stack = context.getStack();
        var copy = ItemHandlerHelper.copyStackWithSize(stack, 1);
        var handler = AFUtil.findCapability(copy, GTCapability.CAPABILITY_ELECTRIC_ITEM);
        if (handler == null) {
            return 0;
        }
        long extracted = handler.discharge(amount, handler.getTier(), false, true, mode.isSimulate());
        if (mode == Actionable.MODULATE) {
            stack.shrink(1);
            context.addOverflow(copy);
        }
        return extracted;
    }

    public long insert(ItemContext context, FluxKey what, long amount, Actionable mode) {
        var stack = context.getStack();
        var copy = ItemHandlerHelper.copyStackWithSize(stack, 1);
        var handler = AFUtil.findCapability(copy, GTCapability.CAPABILITY_ELECTRIC_ITEM);
        if (handler == null) {
            return 0;
        }

        long filled = handler.charge(amount, handler.getTier(), false, mode.isSimulate());
        if (mode == Actionable.MODULATE) {
            stack.shrink(1);
            context.addOverflow(copy);
        }
        return filled;
    }

}

