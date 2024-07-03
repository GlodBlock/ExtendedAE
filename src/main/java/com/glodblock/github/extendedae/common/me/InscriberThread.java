package com.glodblock.github.extendedae.common.me;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.energy.IEnergySource;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.blockentity.misc.InscriberRecipes;
import appeng.core.definitions.AEItems;
import appeng.recipes.handlers.InscriberProcessType;
import appeng.recipes.handlers.InscriberRecipe;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.CombinedInternalInventory;
import appeng.util.inv.FilteredInternalInventory;
import appeng.util.inv.filter.IAEItemFilter;
import com.glodblock.github.extendedae.common.tileentities.TileExInscriber;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.IdentityHashMap;
import java.util.Map;

public class InscriberThread {

    private static final int MAX_PROCESSING_STEPS = 200;
    @NotNull
    private final TileExInscriber host;
    private int processingTime = 0;
    private final AppEngInternalInventory topItemHandler;
    private final AppEngInternalInventory bottomItemHandler;
    private final AppEngInternalInventory sideItemHandler;
    private final InternalInventory inv;
    private final Map<InternalInventory, ItemStack> lastStacks;
    public final InternalInventory topItemHandlerExtern;
    public final InternalInventory bottomItemHandlerExtern;
    public final InternalInventory sideItemHandlerExtern;
    public final InternalInventory combinedItemHandlerExtern;
    private boolean smash;
    private int finalStep;
    private long clientStart;

    private InscriberRecipe cachedTask = null;

    public InscriberThread(@NotNull TileExInscriber host) {
        this.host = host;
        IAEItemFilter baseFilter = new BaseFilter();
        this.topItemHandler = new AppEngInternalInventory(this.host, 1, 1, baseFilter);
        this.bottomItemHandler = new AppEngInternalInventory(this.host, 1, 1, baseFilter);
        this.sideItemHandler = new AppEngInternalInventory(this.host, 2, 1, baseFilter);
        this.inv = new CombinedInternalInventory(this.topItemHandler, this.bottomItemHandler, this.sideItemHandler);
        this.lastStacks = new IdentityHashMap<>(Map.of(
                topItemHandler, ItemStack.EMPTY, bottomItemHandler, ItemStack.EMPTY,
                sideItemHandler, ItemStack.EMPTY)
        );
        var automationFilter = new AutomationFilter();
        this.topItemHandlerExtern = new FilteredInternalInventory(this.topItemHandler, automationFilter);
        this.bottomItemHandlerExtern = new FilteredInternalInventory(this.bottomItemHandler, automationFilter);
        this.sideItemHandlerExtern = new FilteredInternalInventory(this.sideItemHandler, automationFilter);
        this.combinedItemHandlerExtern = new CombinedInternalInventory(this.topItemHandlerExtern, this.bottomItemHandlerExtern, this.sideItemHandlerExtern);
    }

    public void init() {
        this.lastStacks.put(topItemHandler, topItemHandler.getStackInSlot(0));
        this.lastStacks.put(bottomItemHandler, bottomItemHandler.getStackInSlot(0));
        this.lastStacks.put(sideItemHandler, sideItemHandler.getStackInSlot(0));
    }

    public void setStackSize(int size) {
        this.topItemHandler.setMaxStackSize(0, size);
        this.bottomItemHandler.setMaxStackSize(0, size);
        this.sideItemHandler.setMaxStackSize(0, size);
        this.sideItemHandler.setMaxStackSize(1, size);
    }

    public boolean containsInv(InternalInventory inv) {
        return topItemHandler == inv || bottomItemHandler == inv || sideItemHandler == inv;
    }

    public void readFromStream(RegistryFriendlyByteBuf data) {
        var oldSmash = isSmash();
        var newSmash = data.readBoolean();
        if (oldSmash != newSmash && newSmash) {
            setSmash(true);
        }
        for (int i = 0; i < this.inv.size(); i++) {
            this.inv.setItemDirect(i, ItemStack.STREAM_CODEC.decode(data));
        }
        this.cachedTask = null;
    }

    public void writeToStream(RegistryFriendlyByteBuf data) {
        data.writeBoolean(isSmash());
        for (int i = 0; i < this.inv.size(); i++) {
            ItemStack.STREAM_CODEC.encode(data, inv.getStackInSlot(i));
        }
    }

    public int getMaxProcessingTime() {
        return MAX_PROCESSING_STEPS;
    }

    public int getProcessingTime() {
        return this.processingTime;
    }

    private void setProcessingTime(int processingTime) {
        this.processingTime = processingTime;
    }

    public InternalInventory getInternalInventory() {
        return this.inv;
    }

    public void onChangeInventory(InternalInventory inv, int slot) {
        if (slot == 0) {
            boolean sameItemSameTags = ItemStack.isSameItemSameComponents(inv.getStackInSlot(0), lastStacks.get(inv));
            lastStacks.put(inv, inv.getStackInSlot(0).copy());
            if (sameItemSameTags) {
                return;
            }
            this.setProcessingTime(0);
            this.cachedTask = null;
        }
        this.host.getMainNode().ifPresent((grid, node) -> grid.getTickManager().wakeDevice(node));
    }

    public boolean isSleep() {
        return !hasAutoExportWork() && !this.hasCraftWork();
    }

    private boolean hasAutoExportWork() {
        return !this.sideItemHandler.getStackInSlot(1).isEmpty() &&
                this.host.getConfigManager().getSetting(Settings.AUTO_EXPORT) == YesNo.YES;
    }

    private boolean hasCraftWork() {
        var task = this.getTask();
        if (task != null) {
            return sideItemHandler.insertItem(1, task.getResultItem().copy(), true).isEmpty();
        }

        this.setProcessingTime(0);
        return this.isSmash();
    }

    public boolean isSmash() {
        return this.smash;
    }

    public void setSmash(boolean smash) {
        if (smash && !this.smash) {
            setClientStart(System.currentTimeMillis());
        }
        this.smash = smash;
    }

    public long getClientStart() {
        return this.clientStart;
    }

    private void setClientStart(long clientStart) {
        this.clientStart = clientStart;
    }

    public TickRateModulation tick() {
        if (this.isSmash()) {
            this.finalStep++;
            if (this.finalStep == 8) {
                final InscriberRecipe out = this.getTask();
                if (out != null) {
                    final ItemStack outputCopy = out.getResultItem().copy();

                    if (this.sideItemHandler.insertItem(1, outputCopy, false).isEmpty()) {
                        this.setProcessingTime(0);
                        if (out.getProcessType() == InscriberProcessType.PRESS) {
                            this.topItemHandler.extractItem(0, 1, false);
                            this.bottomItemHandler.extractItem(0, 1, false);
                        }
                        this.sideItemHandler.extractItem(0, 1, false);
                    }
                }
                this.host.saveChanges();
            } else if (this.finalStep == 16) {
                this.finalStep = 0;
                this.setSmash(false);
                this.host.markForUpdate();
            }
        } else if (this.hasCraftWork()) {
            this.host.getMainNode().ifPresent(grid -> {
                IEnergyService eg = grid.getEnergyService();
                IEnergySource src = this.host;
                final int speedFactor = switch (this.host.getUpgrades().getInstalledUpgrades(AEItems.SPEED_CARD)) {
                    default -> 2; // 116 ticks
                    case 1 -> 3; // 83 ticks
                    case 2 -> 5; // 56 ticks
                    case 3 -> 10; // 36 ticks
                    case 4 -> 50; // 20 ticks
                };
                final int powerConsumption = 10 * speedFactor;
                final double powerThreshold = powerConsumption - 0.01;
                double powerReq = this.host.extractAEPower(powerConsumption, Actionable.SIMULATE, PowerMultiplier.CONFIG);

                if (powerReq <= powerThreshold) {
                    src = eg;
                    powerReq = eg.extractAEPower(powerConsumption, Actionable.SIMULATE, PowerMultiplier.CONFIG);
                }

                if (powerReq > powerThreshold) {
                    src.extractAEPower(powerConsumption, Actionable.MODULATE, PowerMultiplier.CONFIG);
                    this.setProcessingTime(this.getProcessingTime() + speedFactor);
                }
            });

            if (this.getProcessingTime() > this.getMaxProcessingTime()) {
                this.setProcessingTime(this.getMaxProcessingTime());
                final InscriberRecipe out = this.getTask();
                if (out != null) {
                    final ItemStack outputCopy = out.getResultItem().copy();
                    if (this.sideItemHandler.insertItem(1, outputCopy, true).isEmpty()) {
                        this.setSmash(true);
                        this.finalStep = 0;
                        this.host.markForUpdate();
                    }
                }
            }
        }
        if (this.pushOutResult()) {
            return TickRateModulation.URGENT;
        }
        return this.hasCraftWork() ? TickRateModulation.URGENT
                : this.hasAutoExportWork() ? TickRateModulation.SLOWER : TickRateModulation.SLEEP;
    }

    private boolean pushOutResult() {
        if (!this.hasAutoExportWork()) {
            return false;
        }

        var pushSides = EnumSet.allOf(Direction.class);
        if (this.host.isSeparateSides()) {
            pushSides.remove(this.host.getTop());
            pushSides.remove(this.host.getTop().getOpposite());
        }

        assert this.host.getLevel() != null;
        for (var dir : pushSides) {
            var target = InternalInventory.wrapExternal(this.host.getLevel(), this.host.getBlockPos().relative(dir), dir.getOpposite());

            if (target != null) {
                int startItems = this.sideItemHandler.getStackInSlot(1).getCount();
                this.sideItemHandler.insertItem(1, target.addItems(this.sideItemHandler.extractItem(1, 64, false)), false);
                int endItems = this.sideItemHandler.getStackInSlot(1).getCount();

                if (startItems != endItems) {
                    return true;
                }
            }
        }

        return false;
    }

    @Nullable
    public InscriberRecipe getTask() {
        if (this.cachedTask == null && this.host.getLevel() != null) {
            ItemStack input = this.sideItemHandler.getStackInSlot(0);
            ItemStack plateA = this.topItemHandler.getStackInSlot(0);
            ItemStack plateB = this.bottomItemHandler.getStackInSlot(0);
            if (input.isEmpty()) {
                return null; // No input to handle
            }
            this.cachedTask = InscriberRecipes.findRecipe(this.host.getLevel(), input, plateA, plateB, true);
        }
        return this.cachedTask;
    }

    public class BaseFilter implements IAEItemFilter {
        @Override
        public boolean allowInsert(InternalInventory inv, int slot, ItemStack stack) {
            // output slot
            if (slot == 1) {
                // slots and automation prevent insertion into the output,
                // we need it here for the inscriber's own internal logic
                return true;
            }

            // always allow name press
            if (inv == topItemHandler || inv == bottomItemHandler) {
                if (AEItems.NAME_PRESS.isSameAs(stack)) {
                    return true;
                }
            }

            if (inv == sideItemHandler && (AEItems.NAME_PRESS.isSameAs(topItemHandler.getStackInSlot(0))
                    || AEItems.NAME_PRESS.isSameAs(bottomItemHandler.getStackInSlot(0)))) {
                // can always rename anything
                return true;
            }

            // only allow if is a proper recipe match
            ItemStack bot = bottomItemHandler.getStackInSlot(0);
            ItemStack middle = sideItemHandler.getStackInSlot(0);
            ItemStack top = topItemHandler.getStackInSlot(0);

            if (inv == bottomItemHandler)
                bot = stack;
            if (inv == sideItemHandler)
                middle = stack;
            if (inv == topItemHandler)
                top = stack;

            assert host.getLevel() != null;
            for (var holder : InscriberRecipes.getRecipes(host.getLevel())) {
                var recipe = holder.value();
                if (!middle.isEmpty() && !recipe.getMiddleInput().test(middle)) {
                    continue;
                }

                if (bot.isEmpty() && top.isEmpty()) {
                    return true;
                } else if (bot.isEmpty()) {
                    if (recipe.getTopOptional().test(top) || recipe.getBottomOptional().test(top)) {
                        return true;
                    }
                } else if (top.isEmpty()) {
                    if (recipe.getBottomOptional().test(bot) || recipe.getTopOptional().test(bot)) {
                        return true;
                    }
                } else {
                    if ((recipe.getTopOptional().test(top) && recipe.getBottomOptional().test(bot))
                            || (recipe.getBottomOptional().test(top) && recipe.getTopOptional().test(bot))) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public class AutomationFilter implements IAEItemFilter {
        @Override
        public boolean allowExtract(InternalInventory inv, int slot, int amount) {
            if (slot == 1) {
                return true; // Can always extract from output slot
            }

            if (isSmash()) {
                return false;
            }

            // Can only extract from top and bottom in separated sides mode
            return host.isSeparateSides() && (inv == topItemHandler || inv == bottomItemHandler);
        }

        @Override
        public boolean allowInsert(InternalInventory inv, int slot, ItemStack stack) {
            if (slot == 1) {
                return false; // No inserting into the output slot
            }
            return !isSmash();
        }
    }

}
