package com.glodblock.github.extendedae.common.tileentities;

import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.orientation.BlockOrientation;
import appeng.api.orientation.RelativeSide;
import appeng.api.util.AECableType;
import appeng.api.util.DimensionalBlockPos;
import appeng.blockentity.grid.AENetworkedInvBlockEntity;
import appeng.util.Platform;
import appeng.util.inv.AppEngInternalInventory;
import com.glodblock.github.extendedae.api.IRecipeMachine;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.recipe.CrystalFixerRecipe;
import com.glodblock.github.extendedae.util.RecipeExecutor;
import com.glodblock.github.glodium.recipe.CommonRecipeContext;
import com.glodblock.github.glodium.recipe.RecipeSearchContext;
import com.glodblock.github.glodium.util.GlodUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Set;

public class TileCrystalFixer extends AENetworkedInvBlockEntity implements IGridTickable, IRecipeMachine<RecipeInput, CrystalFixerRecipe> {

    public static final int MAX_PROGRESS = 100;
    private final AppEngInternalInventory inv = new AppEngInternalInventory(this, 1);
    private final CommonRecipeContext<CrystalFixerRecipe> ctx = new FixerRecipeContext(this);
    private final RecipeExecutor<CrystalFixerRecipe> exec;
    private int progress = 0;

    public TileCrystalFixer(BlockPos pos, BlockState blockState) {
        super(GlodUtil.getTileType(TileCrystalFixer.class, TileCrystalFixer::new, EAESingletons.CRYSTAL_FIXER), pos, blockState);
        this.getMainNode().setFlags().setIdlePowerUsage(0).addService(IGridTickable.class, this);
        this.exec = new RecipeExecutor<>(this, r -> new ItemStack(r.getOutput()), MAX_PROGRESS, 50);
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode node) {
        return new TickingRequest(2, 10, !this.ctx.shouldTick());
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        markForUpdate();
        return this.exec.execute(genProgress(), true);
    }

    private int genProgress() {
        if (this.getLevel() == null) {
            return 0;
        }
        return this.getLevel().getRandom().nextInt(2, 4);
    }

    protected BlockState getFacingBlock() {
        if (this.getLevel() == null) {
            return Blocks.AIR.defaultBlockState();
        }
        return this.getLevel().getBlockState(this.getBlockPos().offset(this.getFront().getNormal()));
    }

    protected void setNewBlock(BlockState block) {
        if (this.getLevel() != null) {
            this.getLevel().setBlockAndUpdate(this.getBlockPos().offset(this.getFront().getNormal()), block);
        }
    }

    @Override
    public int getProgress() {
        return this.progress;
    }

    @Override
    public void addProgress(int delta) {
        this.progress += delta;
    }

    @Override
    public void setProgress(int progress) {
        this.progress = progress;
    }

    @Override
    public RecipeSearchContext<RecipeInput, CrystalFixerRecipe> getContext() {
        return this.ctx;
    }

    @Override
    public void setWorking(boolean work) {
        // NO-OP
    }

    @Override
    public InternalInventory getOutput() {
        return VoidInventory.INSTANCE;
    }

    @Override
    public @Nullable IManagedGridNode getNode() {
        return this.getMainNode();
    }

    @Override
    protected boolean readFromStream(RegistryFriendlyByteBuf data) {
        var changed = super.readFromStream(data);
        this.inv.setItemDirect(0, ItemStack.OPTIONAL_STREAM_CODEC.decode(data));
        return changed;
    }

    @Override
    protected void writeToStream(RegistryFriendlyByteBuf data) {
        super.writeToStream(data);
        ItemStack.OPTIONAL_STREAM_CODEC.encode(data, this.inv.getStackInSlot(0));
    }

    @Override
    public AECableType getCableConnectionType(Direction dir) {
        return AECableType.COVERED;
    }

    @Override
    public Set<Direction> getGridConnectableSides(BlockOrientation orientation) {
        return EnumSet.of(orientation.getSide(RelativeSide.BACK));
    }

    @Override
    public void saveAdditional(CompoundTag data, HolderLookup.Provider registries) {
        super.saveAdditional(data, registries);
        data.putInt("progress", this.progress);
        this.ctx.save(data);
    }

    @Override
    public void loadTag(CompoundTag data, HolderLookup.Provider registries) {
        super.loadTag(data, registries);
        this.progress = data.getInt("progress");
        this.ctx.load(data);
    }

    @Override
    public InternalInventory getInternalInventory() {
        return this.inv;
    }

    @Override
    public void onChangeInventory(AppEngInternalInventory inv, int slot) {
        this.markForUpdate();
        this.saveChanges();
        this.ctx.onInvChange();
    }

    @Override
    protected void onOrientationChanged(BlockOrientation orientation) {
        super.onOrientationChanged(orientation);
        this.ctx.onInvChange();
    }

    public void onChanged() {
        this.ctx.onInvChange();
    }

    public void refuel(Player player) {
        if (!Platform.hasPermissions(new DimensionalBlockPos(this), player)) {
            return;
        }
        var playerInv = player.getInventory();
        ItemStack held = playerInv.getSelected();
        if (held.isEmpty()) {
            var stuff = this.inv.extractItem(0, Integer.MAX_VALUE, false);
            if (!stuff.isEmpty()) {
                playerInv.placeItemBackInInventory(stuff);
            }
        } else {
            var notAdded = this.inv.insertItem(0, held, false);
            playerInv.setItem(playerInv.selected, notAdded);
        }
    }

    private static class FixerRecipeContext extends CommonRecipeContext<CrystalFixerRecipe> {

        private final TileCrystalFixer host;

        protected FixerRecipeContext(TileCrystalFixer host) {
            super(() -> host.level, CrystalFixerRecipe.TYPE);
            this.host = host;
        }

        @Override
        public void onInvChange() {
            super.onInvChange();
            this.host.getMainNode().ifPresent((grid, node) -> grid.getTickManager().wakeDevice(node));
        }

        @Override
        public void onFind(@Nullable RecipeHolder<CrystalFixerRecipe> recipe) {
            super.onFind(recipe);
            this.host.getMainNode().ifPresent((grid, node) -> grid.getTickManager().wakeDevice(node));
        }

        @Override
        public RecipeHolder<CrystalFixerRecipe> searchRecipe() {
            if (host.getLevel() == null) {
                return null;
            }
            var recipe = CrystalFixerRecipe.lookup(this.host.getFacingBlock().getBlock(), host.getLevel());
            if (recipe != null && this.testRecipe(recipe)) {
                return recipe;
            }
            return null;
        }

        @Override
        public boolean testRecipe(RecipeHolder<CrystalFixerRecipe> recipeHolder) {
            var recipe = recipeHolder.value();
            var block = this.host.getFacingBlock().getBlock();
            if (block == recipe.getInput()) {
                var fuel = recipe.getFuel();
                var storedFuel = this.host.inv.getStackInSlot(0).copy();
                if (fuel.checkType(storedFuel)) {
                    fuel.consume(storedFuel);
                    return fuel.isEmpty();
                }
            }
            return false;
        }

        @Override
        public void runRecipe(RecipeHolder<CrystalFixerRecipe> recipeHolder) {
            var recipe = recipeHolder.value();
            var fuel = recipe.getFuel();
            var storedFuel = this.host.inv.getStackInSlot(0);
            if (fuel.checkType(storedFuel)) {
                fuel.consume(storedFuel);
                this.host.inv.setItemDirect(0, storedFuel);
                if (this.host.getLevel() != null && recipe.roll(this.host.getLevel().getRandom())) {
                    this.host.setNewBlock(recipe.getOutput().defaultBlockState());
                }
            }
        }
    }

    private static class VoidInventory implements InternalInventory {

        public static final VoidInventory INSTANCE = new VoidInventory();

        @Override
        public int size() {
            return 1;
        }

        @Override
        public ItemStack getStackInSlot(int slotIndex) {
            return ItemStack.EMPTY;
        }

        @Override
        public void setItemDirect(int slotIndex, ItemStack stack) {
            // NO-OP
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return ItemStack.EMPTY;
        }

    }

}
