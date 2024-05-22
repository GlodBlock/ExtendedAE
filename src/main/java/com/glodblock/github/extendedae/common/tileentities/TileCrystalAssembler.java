package com.glodblock.github.extendedae.common.tileentities;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.config.Setting;
import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.energy.IEnergySource;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.orientation.BlockOrientation;
import appeng.api.orientation.RelativeSide;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.GenericStack;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.UpgradeInventories;
import appeng.api.util.AECableType;
import appeng.api.util.IConfigManager;
import appeng.api.util.IConfigurableObject;
import appeng.blockentity.grid.AENetworkPowerBlockEntity;
import appeng.core.AppEng;
import appeng.core.definitions.AEItems;
import appeng.core.settings.TickRates;
import appeng.helpers.externalstorage.GenericStackInv;
import appeng.util.ConfigManager;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.CombinedInternalInventory;
import com.glodblock.github.extendedae.api.caps.IGenericInvHost;
import com.glodblock.github.extendedae.common.EAEItemAndBlock;
import com.glodblock.github.extendedae.recipe.CrystalAssemblerRecipe;
import com.glodblock.github.extendedae.util.async.RecipeSearchContext;
import com.glodblock.github.glodium.util.GlodUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class TileCrystalAssembler extends AENetworkPowerBlockEntity implements IGridTickable, IUpgradeableObject, IConfigurableObject, IGenericInvHost {

    public static final int SLOTS = 9;
    public static final int TANK_CAP = 16000;
    public static final int POWER_MAXIMUM_AMOUNT = 8000;
    public static final int MAX_PROGRESS = 200;
    private final AppEngInternalInventory input = new AppEngInternalInventory(this, SLOTS, 64);
    private final AppEngInternalInventory output = new AppEngInternalInventory(this, 1, 64);
    private final CombinedInternalInventory inv = new CombinedInternalInventory(input, output);
    private final IUpgradeInventory upgrades;
    private final ConfigManager configManager;
    private final CrystalRecipeContext ctx = new CrystalRecipeContext(this);
    private final GenericStackInv tank = new GenericStackInv(Set.of(AEKeyType.fluids()), this::onChangeTank, GenericStackInv.Mode.STORAGE, 1);
    private boolean isWorking = false;
    private int progress = 0;

    public TileCrystalAssembler(BlockPos pos, BlockState blockState) {
        super(GlodUtil.getTileType(TileCrystalAssembler.class, TileCrystalAssembler::new, EAEItemAndBlock.CRYSTAL_ASSEMBLER), pos, blockState);
        this.getMainNode().setFlags().setIdlePowerUsage(0).addService(IGridTickable.class, this);
        this.setInternalMaxPower(POWER_MAXIMUM_AMOUNT);
        this.setPowerSides(getGridConnectableSides(getOrientation()));
        this.upgrades = UpgradeInventories.forMachine(EAEItemAndBlock.CRYSTAL_ASSEMBLER, 4, this::saveChanges);
        this.configManager = new ConfigManager(this::onConfigChanged);
        this.configManager.registerSetting(Settings.AUTO_EXPORT, YesNo.NO);
        tank.setCapacity(AEKeyType.fluids(), TANK_CAP);
    }

    @Override
    protected InternalInventory getExposedInventoryForSide(Direction facing) {
        if (facing == getOrientation().getSide(RelativeSide.BOTTOM)) {
            return this.output;
        } else {
            return this.input;
        }
    }

    private void onConfigChanged(IConfigManager manager, Setting<?> setting) {
        if (setting == Settings.AUTO_EXPORT) {
            getMainNode().ifPresent((grid, node) -> grid.getTickManager().wakeDevice(node));
        }
    }

    public boolean isWorking() {
        return this.isWorking;
    }

    public int getProgress() {
        return this.progress;
    }

    public AppEngInternalInventory getInput() {
        return this.input;
    }

    public GenericStackInv getTank() {
        return this.tank;
    }

    public AppEngInternalInventory getOutput() {
        return this.output;
    }

    @Override
    public AECableType getCableConnectionType(Direction dir) {
        return AECableType.COVERED;
    }

    @Override
    public Set<Direction> getGridConnectableSides(BlockOrientation orientation) {
        return EnumSet.complementOf(EnumSet.of(orientation.getSide(RelativeSide.TOP)));
    }

    @Override
    protected boolean readFromStream(FriendlyByteBuf data) {
        var changed = super.readFromStream(data);
        boolean newWork = data.readBoolean();
        if (newWork != this.isWorking) {
            this.isWorking = newWork;
            changed = true;
        }
        return changed;
    }

    @Override
    protected void writeToStream(FriendlyByteBuf data) {
        super.writeToStream(data);
        data.writeBoolean(this.isWorking);
    }

    @Override
    public void addAdditionalDrops(Level level, BlockPos pos, List<ItemStack> drops) {
        super.addAdditionalDrops(level, pos, drops);
        for (var upgrade : upgrades) {
            drops.add(upgrade);
        }
        var fluid = this.tank.getStack(0);
        if (fluid != null) {
            fluid.what().addDrops(fluid.amount(), drops, level, pos);
        }
    }

    @Override
    public void saveAdditional(CompoundTag data) {
        super.saveAdditional(data);
        this.tank.writeToChildTag(data, "tank_in");
        this.upgrades.writeToNBT(data, "upgrades");
        this.configManager.writeToNBT(data);
        this.ctx.save(data);
    }

    @Override
    public void clearContent() {
        super.clearContent();
        this.tank.clear();
        this.upgrades.clear();
    }

    @Override
    public void loadTag(CompoundTag data) {
        super.loadTag(data);
        this.tank.readFromChildTag(data, "tank_in");
        this.upgrades.readFromNBT(data, "upgrades");
        this.configManager.readFromNBT(data);
        this.ctx.load(data);
    }

    @Override
    public ConfigManager getConfigManager() {
        return configManager;
    }

    private boolean hasAutoExportWork() {
        return !this.output.getStackInSlot(0).isEmpty() && configManager.getSetting(Settings.AUTO_EXPORT) == YesNo.YES;
    }

    @Override
    protected void onOrientationChanged(BlockOrientation orientation) {
        super.onOrientationChanged(orientation);
        this.setPowerSides(getGridConnectableSides(orientation));
    }

    @Override
    public IUpgradeInventory getUpgrades() {
        return upgrades;
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode node) {
        return new TickingRequest(TickRates.Inscriber, !this.ctx.shouldTick());
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        if (this.pushOutResult()) {
            return TickRateModulation.URGENT;
        }
        this.markForUpdate();
        var runRecipe = this.ctx.currentRecipe;
        if (runRecipe != null) {
            this.getMainNode().ifPresent(grid -> {
                this.isWorking = true;
                this.markForUpdate();
                var speed = switch (this.getUpgrades().getInstalledUpgrades(AEItems.SPEED_CARD)) {
                    default -> 2; // 116 ticks
                    case 1 -> 3; // 83 ticks
                    case 2 -> 5; // 56 ticks
                    case 3 -> 10; // 36 ticks
                    case 4 -> 50; // 20 ticks
                };
                IEnergyService eg = grid.getEnergyService();
                IEnergySource src = this;
                final int powerConsumption = 10 * speed;
                final double powerThreshold = powerConsumption - 0.01;
                double powerReq = this.extractAEPower(powerConsumption, Actionable.SIMULATE, PowerMultiplier.CONFIG);
                if (powerReq <= powerThreshold) {
                    src = eg;
                    powerReq = eg.extractAEPower(powerConsumption, Actionable.SIMULATE, PowerMultiplier.CONFIG);
                }
                if (powerReq > powerThreshold) {
                    src.extractAEPower(powerConsumption, Actionable.MODULATE, PowerMultiplier.CONFIG);
                    this.progress += speed;
                }
                if (this.progress >= MAX_PROGRESS) {
                    this.progress = 0;
                    var outputStack = runRecipe.value().output.copy();
                    if (this.ctx.testRecipe(runRecipe) && this.output.insertItem(0, outputStack, true).isEmpty()) {
                        this.ctx.runRecipe(runRecipe);
                        this.output.insertItem(0, outputStack, false);
                    }
                    this.ctx.currentRecipe = null;
                }
            });
            return TickRateModulation.URGENT;
        } else {
            this.isWorking = false;
            if (this.ctx.shouldTick()) {
                this.ctx.findRecipe();
            }
            return TickRateModulation.FASTER;
        }
    }

    private boolean pushOutResult() {
        if (!this.hasAutoExportWork()) {
            return false;
        }
        for (var dir : Direction.values()) {
            var target = InternalInventory.wrapExternal(level, getBlockPos().relative(dir), dir.getOpposite());
            if (target != null) {
                int startItems = this.output.getStackInSlot(0).getCount();
                this.output.insertItem(0, target.addItems(this.output.extractItem(0, 64, false)), false);
                int endItems = this.output.getStackInSlot(0).getCount();
                if (startItems != endItems) {
                    return true;
                }
            }
        }
        return false;
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

    public void onChangeTank() {
        this.saveChanges();
        this.ctx.onInvChange();
    }

    @Override
    public GenericStackInv getGenericInv() {
        return this.tank;
    }

    @Override
    public GenericStackInv getGenericInv(@Nullable Direction side) {
        if (side != getOrientation().getSide(RelativeSide.BOTTOM)) {
            return this.tank;
        }
        return null;
    }

    private static class CrystalRecipeContext extends RecipeSearchContext<CrystalAssemblerRecipe> {

        private final TileCrystalAssembler host;

        protected CrystalRecipeContext(TileCrystalAssembler host) {
            this.host = host;
        }

        @Override
        public void onInvChange() {
            super.onInvChange();
            this.host.getMainNode().ifPresent((grid, node) -> grid.getTickManager().wakeDevice(node));
        }

        @Override
        public void onFind(@Nullable RecipeHolder<CrystalAssemblerRecipe> recipe) {
            super.onFind(recipe);
            if (recipe != null) {
                this.host.getMainNode().ifPresent((grid, node) -> grid.getTickManager().wakeDevice(node));
            }
        }

        @Override
        public RecipeHolder<CrystalAssemblerRecipe> searchRecipe() {
            if (this.host.level == null) {
                return null;
            }
            var recipes = this.host.level.getRecipeManager().byType(CrystalAssemblerRecipe.TYPE);
            for (var recipe : recipes.values()) {
                if (testRecipe(recipe)) {
                    return recipe;
                }
            }
            return null;
        }

        @Override
        public boolean testRecipe(RecipeHolder<CrystalAssemblerRecipe> recipe) {
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
                copyInv = copyInv.stream().filter(o -> {
                    if (o instanceof ItemStack s) {
                        return !s.isEmpty();
                    }
                    if (o instanceof FluidStack f) {
                        return !f.isEmpty();
                    }
                    return false;
                }).toList();
            }
            return true;
        }

        @Override
        public void runRecipe(RecipeHolder<CrystalAssemblerRecipe> recipe) {
            var sample = recipe.value().getSample();
            var fluid = this.host.tank.getStack(0);
            FluidStack fluidStack = null;
            if (fluid != null && fluid.what() instanceof AEFluidKey key) {
                fluidStack = key.toStack((int) fluid.amount());
            }
            for (var tester : sample) {
                for (int x = 0; x < this.host.input.size(); x++) {
                    var item = this.host.input.getStackInSlot(x);
                    if (tester.checkType(item)) {
                        tester.consume(item);
                        this.host.input.setItemDirect(x, item);
                    }
                    if (tester.isEmpty()) {
                        break;
                    }
                }
                if (fluidStack != null && !tester.isEmpty() && tester.checkType(fluidStack)) {
                    tester.consume(fluidStack);
                }
            }
            if (fluidStack != null) {
                if (fluidStack.isEmpty()) {
                    this.host.tank.setStack(0, null);
                } else {
                    this.host.tank.setStack(0, new GenericStack(AEFluidKey.of(fluidStack), fluidStack.getAmount()));
                }
            }
        }

        public List<Object> copyHostInv() {
            List<Object> inv = new ArrayList<>();
            for (var item : this.host.input) {
                if (!item.isEmpty()) {
                    inv.add(item.copy());
                }
            }
            var fluid = this.host.tank.getStack(0);
            if (fluid != null && fluid.what() instanceof AEFluidKey key) {
                inv.add(key.toStack((int) fluid.amount()));
            }
            return inv;
        }

        public void save(CompoundTag tag) {
            var nbt = new CompoundTag();
            if (this.currentRecipe != null) {
                nbt.putString("current", this.currentRecipe.id().toString());
            }
            if (this.lastRecipe != null) {
                nbt.putString("last", this.lastRecipe.id().toString());
            }
            tag.put("recipeCtx", nbt);
        }

        public void load(CompoundTag tag) {
            var nbt = tag.getCompound("recipeCtx");
            if (nbt.contains("current")) {
                try {
                    var id = new ResourceLocation(nbt.getString("current"));
                    this.currentRecipe = AppEng.instance().getCurrentServer().getLevel(Level.OVERWORLD).getRecipeManager().byType(CrystalAssemblerRecipe.TYPE).get(id);
                } catch (Throwable e) {
                    this.currentRecipe = null;
                }
            }
            if (nbt.contains("last")) {
                try {
                    var id = new ResourceLocation(nbt.getString("last"));
                    this.lastRecipe = AppEng.instance().getCurrentServer().getLevel(Level.OVERWORLD).getRecipeManager().byType(CrystalAssemblerRecipe.TYPE).get(id);
                } catch (Throwable e) {
                    this.lastRecipe = null;
                }
            }
        }

    }

}
