package com.glodblock.github.extendedae.util;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.ticking.TickRateModulation;
import com.glodblock.github.extendedae.api.IRecipeMachine;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

import java.util.function.Function;

public record RecipeExecutor<T extends Recipe<?>>(IRecipeMachine<?, T> machine, Function<T, ItemStack> outputGetter, int maxTime) {

    private static final double EPSILON = 0.1;

    public TickRateModulation execute(int speed, boolean usePower) {
        var ctx = this.machine.getContext();
        var mainNode = this.machine.getNode();
        var power = this.machine.getEnergy();
        var runRecipe = ctx.currentRecipe;
        var output = this.machine.getOutput();
        if (runRecipe != null) {
            var total = this.machine.getMaxProcess();
            var current = this.machine.getProgress();
            this.machine.setWorking(true);
            if (usePower) {
                var energyUse = Math.min(total / this.maxTime * speed, total - current);
                // Use network power when online
                if (mainNode != null) {
                    mainNode.ifPresent(grid -> {
                        IEnergyService eg = grid.getEnergyService();
                        double toRemove = energyUse;
                        if (eg != null && toRemove > 0) {
                            var powerReq = eg.extractAEPower(toRemove, Actionable.MODULATE, PowerMultiplier.CONFIG);
                            toRemove -= powerReq;
                        }
                        if (power != null && toRemove > 0) {
                            var powerReq = power.extractAEPower(toRemove, Actionable.MODULATE, PowerMultiplier.CONFIG);
                            toRemove -= powerReq;
                        }
                        toRemove = Math.max(0, toRemove);
                        this.machine.addProgress(energyUse - toRemove);
                    });
                } else {
                    // Use internal power when offline
                    if (power != null) {
                        double removed = power.extractAEPower(energyUse, Actionable.MODULATE, PowerMultiplier.CONFIG);
                        if (removed > 0) {
                            removed = Math.min(removed, energyUse);
                            this.machine.addProgress(removed);
                        }
                    }
                }
            } else {
                this.machine.addProgress(speed);
            }
            if (this.machine.getProgress() + EPSILON >= this.machine.getMaxProcess()) {
                this.machine.setProgress(0);
                var outputStack = this.outputGetter.apply(runRecipe.value()).copy();
                if (ctx.testRecipe(runRecipe)) {
                    if (output.insertItem(0, outputStack, true).isEmpty()) {
                        ctx.runRecipe(runRecipe);
                        output.insertItem(0, outputStack, false);
                    } else {
                        ctx.stuck = true;
                    }
                }
                ctx.currentRecipe = null;
            }
            return TickRateModulation.URGENT;
        } else {
            if (ctx.shouldTick()) {
                ctx.findRecipe();
            }
            if (ctx.currentRecipe == null) {
                this.machine.setWorking(false);
            }
            return TickRateModulation.FASTER;
        }
    }

}
