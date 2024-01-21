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
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class FEContainerItemStrategy implements ContainerItemStrategy<FluxKey, ItemContext> {

    private boolean isGTPresent() {
        return ModList.get().isLoaded("gtceu");
    }

    @Override
    public @Nullable GenericStack getContainedStack(ItemStack stack) {
        if (isGTPresent()) {
            var res =  GTEUContainerItemStrategy.BRIDGE.getContainedStack(stack);
            if (res != null) {
                return res;
            }
        }
        var energy = AFUtil.findCapability(stack, ForgeCapabilities.ENERGY);
        if (energy != null && energy.getEnergyStored() > 0) {
            return new GenericStack(FluxKey.of(EnergyType.FE), energy.getEnergyStored());
        }
        return null;
    }

    @Override
    public @Nullable ItemContext findCarriedContext(Player player, AbstractContainerMenu menu) {
        if (isGTPresent()) {
            var res = GTEUContainerItemStrategy.BRIDGE.findCarriedContext(player, menu);
            if (res != null) {
                return res;
            }
        }
        if (menu.getCarried().getCapability(ForgeCapabilities.ENERGY).isPresent()) {
            return new CarriedContext(player, menu);
        }
        return null;
    }

    @Override
    public @Nullable ItemContext findPlayerSlotContext(Player player, int slot) {
        if (isGTPresent()) {
            var res = GTEUContainerItemStrategy.BRIDGE.findPlayerSlotContext(player, slot);
            if (res != null) {
                return res;
            }
        }
        if (player.getInventory().getItem(slot).getCapability(ForgeCapabilities.ENERGY).isPresent()) {
            return new PlayerInvContext(player, slot);
        }
        return null;
    }

    @Override
    public long extract(ItemContext context, FluxKey what, long amount, Actionable mode) {
        if (isGTPresent()) {
            var res = GTEUContainerItemStrategy.BRIDGE.extract(context, what, amount, mode);
            if (res > 0) {
                return res;
            }
        }
        var stack = context.getStack();
        var copy = ItemHandlerHelper.copyStackWithSize(stack, 1);
        var handler = AFUtil.findCapability(copy, ForgeCapabilities.ENERGY);
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
        if (isGTPresent()) {
            var res = GTEUContainerItemStrategy.BRIDGE.insert(context, what, amount, mode);
            if (res > 0) {
                return res;
            }
        }
        var stack = context.getStack();
        var copy = ItemHandlerHelper.copyStackWithSize(stack, 1);
        var handler = AFUtil.findCapability(copy, ForgeCapabilities.ENERGY);
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
