package com.glodblock.github.extendedae.common.tileentities;

import appeng.api.config.Setting;
import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.energy.IEnergySource;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.UpgradeInventories;
import appeng.api.util.AECableType;
import appeng.api.util.IConfigManager;
import appeng.api.util.IConfigurableObject;
import appeng.blockentity.grid.AENetworkedPoweredBlockEntity;
import appeng.core.definitions.AEItems;
import appeng.core.settings.TickRates;
import appeng.util.ConfigManager;
import appeng.util.SettingsFrom;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.CombinedInternalInventory;
import appeng.util.inv.FilteredInternalInventory;
import appeng.util.inv.filter.AEItemFilters;
import com.glodblock.github.extendedae.api.IRecipeMachine;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.common.me.DirectionSet;
import com.glodblock.github.extendedae.recipe.PulverizerRecipe;
import com.glodblock.github.extendedae.util.FCUtil;
import com.glodblock.github.extendedae.util.RecipeExecutor;
import com.glodblock.github.glodium.recipe.CommonRecipeContext;
import com.glodblock.github.glodium.recipe.RecipeSearchContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TilePulverizer extends AENetworkedPoweredBlockEntity implements IGridTickable, IUpgradeableObject, IConfigurableObject, IRecipeMachine<RecipeInput, PulverizerRecipe> {

    public static final int POWER_MAXIMUM_AMOUNT = 10000;
    public static final int MAX_PROGRESS_TICKS = 100;
    public static final int OUTPUT_SLOTS = 4;
    private final AppEngInternalInventory input = new AppEngInternalInventory(this, 1, 64);
    private final AppEngInternalInventory output = new AppEngInternalInventory(this, OUTPUT_SLOTS, 64);
    private final CombinedInternalInventory inv = new CombinedInternalInventory(input, output);
    private final FilteredInternalInventory outputExposed = new FilteredInternalInventory(output, AEItemFilters.EXTRACT_ONLY);
    private final FilteredInternalInventory inputExposed = new FilteredInternalInventory(input, AEItemFilters.INSERT_ONLY);
    private final CombinedInternalInventory invExposed = new CombinedInternalInventory(inputExposed, outputExposed);
    private final IUpgradeInventory upgrades;
    private final ConfigManager configManager;
    private final CommonRecipeContext<PulverizerRecipe> ctx = new PulverizerRecipeContext(this);
    private final RecipeExecutor<PulverizerRecipe> exec;
    private boolean isWorking = false;
    private double progress = 0;
    private final DirectionSet outputSides = new DirectionSet();

    public TilePulverizer(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState) {
        super(blockEntityType, pos, blockState);
        this.getMainNode().setFlags().setIdlePowerUsage(0).addService(IGridTickable.class, this);
        this.setInternalMaxPower(POWER_MAXIMUM_AMOUNT);
        this.upgrades = UpgradeInventories.forMachine(EAESingletons.PULVERIZER, 4, this::saveChanges);
        this.configManager = new ConfigManager(this::onConfigChanged);
        this.configManager.registerSetting(Settings.AUTO_EXPORT, YesNo.NO);
        this.exec = new RecipeExecutor<>(this, MAX_PROGRESS_TICKS);
    }

    public AppEngInternalInventory getInput() {
        return this.input;
    }

    public AppEngInternalInventory getOutput() {
        return this.output;
    }

    @Override
    protected InternalInventory getExposedInventoryForSide(Direction facing) {
        return this.invExposed;
    }

    @Override
    public ResourceHandler<@NotNull ItemResource> getExposedItemHandler(@Nullable Direction side) {
        return this.invExposed.toResourceHandler();
    }

    private void onConfigChanged(IConfigManager manager, Setting<?> setting) {
        if (setting == Settings.AUTO_EXPORT) {
            getMainNode().ifPresent((grid, node) -> grid.getTickManager().wakeDevice(node));
        }
    }

    public boolean isWorking() {
        return this.isWorking;
    }

    @Override
    public double getProgress() {
        return this.progress;
    }

    @Override
    public void addProgress(double delta) {
        this.progress += delta;
    }

    @Override
    public void setProgress(double progress) {
        this.progress = progress;
    }

    @Override
    public RecipeSearchContext<RecipeInput, PulverizerRecipe> getContext() {
        return this.ctx;
    }

    @Override
    public void setWorking(boolean work) {
        boolean oldVal = this.isWorking;
        this.isWorking = work;
        if (oldVal != work) {
            this.markForUpdate();
        }
    }

    @Override
    public boolean collectOutputs(PulverizerRecipe recipe) {
        if (this.level != null) {
            var outputs = recipe.getOutputs(this.level.getRandom());
            var handler = this.output.toResourceHandler();
            try (var trans = Transaction.openRoot()) {
                for (var output : outputs) {
                    if (!output.isEmpty()) {
                        var added = handler.insert(ItemResource.of(output), output.getCount(), trans);
                        if (added != output.getCount()) {
                            return false;
                        }
                    }
                }
                trans.commit();
            }
            return true;
        }
        return false;
    }

    @Override
    public Set<Direction> getOutputSides() {
        return this.outputSides.asSet();
    }

    @Override
    public AECableType getCableConnectionType(Direction dir) {
        return AECableType.COVERED;
    }

    @Override
    protected boolean readFromStream(RegistryFriendlyByteBuf data) {
        var changed = super.readFromStream(data);
        boolean newWork = data.readBoolean();
        if (newWork != this.isWorking) {
            this.isWorking = newWork;
            changed = true;
        }
        return changed;
    }

    @Override
    protected void writeToStream(RegistryFriendlyByteBuf data) {
        super.writeToStream(data);
        data.writeBoolean(this.isWorking);
    }

    @Override
    public void addAdditionalDrops(Level level, BlockPos pos, List<ItemStack> drops) {
        super.addAdditionalDrops(level, pos, drops);
        for (var upgrade : upgrades) {
            drops.add(upgrade);
        }
    }

    @Override
    public void saveAdditional(ValueOutput data) {
        super.saveAdditional(data);
        this.upgrades.writeToNBT(data, "upgrades");
        this.configManager.writeToNBT(data);
        this.ctx.save(data);
        this.outputSides.save(data, "output_side");
        data.putDouble("progress", this.progress);
    }

    @Override
    public void loadTag(ValueInput data) {
        super.loadTag(data);
        this.upgrades.readFromNBT(data, "upgrades");
        this.configManager.readFromNBT(data);
        this.ctx.load(data);
        this.outputSides.load(data, "output_side");
        this.progress = data.getDoubleOr("progress", 0);
    }

    @Override
    public void clearContent() {
        super.clearContent();
        this.upgrades.clear();
    }

    @Override
    public void importSettings(SettingsFrom mode, DataComponentMap input, @Nullable Player player) {
        super.importSettings(mode, input, player);
        var sides = input.get(EAESingletons.DIRECTION_SET);
        if (sides != null) {
            this.outputSides.reload(sides.asList());
        }
    }

    @Override
    public void exportSettings(SettingsFrom mode, DataComponentMap.Builder output, @Nullable Player player) {
        super.exportSettings(mode, output, player);
        if (mode == SettingsFrom.MEMORY_CARD) {
            output.set(EAESingletons.DIRECTION_SET, this.outputSides);
        }
    }

    @Override
    public ConfigManager getConfigManager() {
        return configManager;
    }

    @Override
    public IUpgradeInventory getUpgrades() {
        return upgrades;
    }

    private boolean stopAutoExport() {
        return this.output.getStackInSlot(0).isEmpty() || configManager.getSetting(Settings.AUTO_EXPORT) != YesNo.YES;
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode node) {
        return new TickingRequest(TickRates.Inscriber, !this.ctx.shouldTick() && this.stopAutoExport());
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        if (this.pushOutResult()) {
            return TickRateModulation.URGENT;
        }
        return this.exec.execute(FCUtil.speedCardMap(this.getUpgrades().getInstalledUpgrades(AEItems.SPEED_CARD)));
    }

    private boolean pushOutResult() {
        if (this.stopAutoExport()) {
            return false;
        }
        return FCUtil.ejectInv(this.level, this.getBlockPos(), this.output, this.outputSides.asSet(), te -> te instanceof TilePulverizer);
    }

    @Nullable
    private ServerLevel getServerLevel() {
        if (this.level instanceof ServerLevel) {
            return (ServerLevel) this.level;
        } else {
            return null;
        }
    }

    @Override
    public IManagedGridNode getNode() {
        return this.getMainNode();
    }

    @Override
    public IEnergySource getEnergy() {
        return this;
    }

    @Override
    public InternalInventory getInternalInventory() {
        return this.inv;
    }

    @Override
    public void onChangeInventory(AppEngInternalInventory inv, int slot) {
        this.saveChanges();
        this.ctx.onInvChange();
    }

    @Override
    public double getMaxProcess() {
        if (this.ctx.currentRecipe != null) {
            return this.ctx.currentRecipe.value().energy;
        }
        return 1;
    }

    private static class PulverizerRecipeContext extends CommonRecipeContext<PulverizerRecipe> {

        private final TilePulverizer host;

        protected PulverizerRecipeContext(TilePulverizer host) {
            super(host::getServerLevel, PulverizerRecipe.TYPE);
            this.host = host;
        }

        @Override
        public void onInvChange() {
            super.onInvChange();
            this.host.getMainNode().ifPresent((grid, node) -> grid.getTickManager().wakeDevice(node));
        }

        @Override
        public void onFind(@Nullable RecipeHolder<@NotNull PulverizerRecipe> recipe) {
            super.onFind(recipe);
            this.host.getMainNode().ifPresent((grid, node) -> grid.getTickManager().wakeDevice(node));
        }

        @Override
        public boolean testRecipe(RecipeHolder<@NotNull PulverizerRecipe> recipe) {
            var outputs = recipe.value().getFullOutputs();
            var outputTest = this.host.output.toResourceHandler();
            try (var trans = Transaction.openRoot()) {
                for (var output : outputs) {
                    if (!output.isEmpty()) {
                        int added = outputTest.insert(ItemResource.of(output), output.getCount(), trans);
                        if (added != output.getCount()) {
                            return false;
                        }
                    }
                }
            }
            var sample = recipe.value().getSample();
            var copyInv = this.copyHostInv();
            for (var tester : sample) {
                for (var stack : copyInv) {
                    if (tester.checkType(stack)) {
                        tester.consume(stack);
                    }
                    if (tester.isEmpty()) {
                        break;
                    }
                }
                if (!tester.isEmpty()) {
                    return false;
                }
                copyInv = copyInv.stream().filter(o -> !o.isEmpty()).toList();
            }
            return true;
        }

        @Override
        public void runRecipe(RecipeHolder<@NotNull PulverizerRecipe> recipe) {
            var sample = recipe.value().getSample();
            for (var tester : sample) {
                for (int x = 0; x < this.host.input.size(); x ++) {
                    var item = this.host.input.getStackInSlot(x);
                    if (tester.checkType(item)) {
                        tester.consume(item);
                        this.host.input.setItemDirect(x, item);
                    }
                    if (tester.isEmpty()) {
                        break;
                    }
                }
            }
        }

        public List<ItemStack> copyHostInv() {
            List<ItemStack> inv = new ArrayList<>();
            for (var item : this.host.input) {
                if (!item.isEmpty()) {
                    inv.add(item.copy());
                }
            }
            return inv;
        }

    }

}
