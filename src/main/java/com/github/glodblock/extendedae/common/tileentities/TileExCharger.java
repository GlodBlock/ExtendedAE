package com.github.glodblock.extendedae.common.tileentities;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.config.PowerUnits;
import appeng.api.implementations.blockentities.ICrankable;
import appeng.api.implementations.items.IAEItemPowerStorage;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.orientation.BlockOrientation;
import appeng.api.orientation.RelativeSide;
import appeng.api.stacks.AEItemKey;
import appeng.api.util.AECableType;
import appeng.api.util.DimensionalBlockPos;
import appeng.blockentity.grid.AENetworkPowerBlockEntity;
import appeng.blockentity.misc.ChargerRecipes;
import appeng.core.AEConfig;
import appeng.core.settings.TickRates;
import appeng.util.Platform;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.filter.IAEItemFilter;
import com.github.glodblock.extendedae.common.EAEItemAndBlock;
import com.github.glodblock.extendedae.common.me.Crankable;
import com.github.glodblock.extendedae.util.FCUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class TileExCharger extends AENetworkPowerBlockEntity implements IGridTickable {

    public static final int POWER_MAXIMUM_AMOUNT = 3200;
    public static final int MAX_THREAD = 4;
    private static final int POWER_THRESHOLD = POWER_MAXIMUM_AMOUNT - 1;
    private boolean working;
    private final AppEngInternalInventory inv = new AppEngInternalInventory(this, MAX_THREAD, 1, new ChargerInvFilter(this));

    public TileExCharger(BlockPos pos, BlockState blockState) {
        super(FCUtil.getTileType(TileExCharger.class, TileExCharger::new, EAEItemAndBlock.EX_CHARGER), pos, blockState);
        this.getMainNode()
                .setFlags()
                .setIdlePowerUsage(0)
                .addService(IGridTickable.class, this);
        this.setInternalMaxPower(POWER_MAXIMUM_AMOUNT);
        this.setPowerSides(getGridConnectableSides(getOrientation()));
    }

    @Override
    public AECableType getCableConnectionType(Direction dir) {
        return AECableType.COVERED;
    }

    @Override
    public Set<Direction> getGridConnectableSides(BlockOrientation orientation) {
        return EnumSet.complementOf(EnumSet.of(orientation.getSide(RelativeSide.FRONT)));
    }

    @Override
    protected boolean readFromStream(FriendlyByteBuf data) {
        var changed = super.readFromStream(data);
        this.working = data.readBoolean();
        for (int x = 0; x < MAX_THREAD; x ++) {
            if (data.readBoolean()) {
                var item = AEItemKey.fromPacket(data);
                this.inv.setItemDirect(x, item.toStack());
            } else {
                this.inv.setItemDirect(x, ItemStack.EMPTY);
            }
        }
        return changed;
    }

    @Override
    protected void writeToStream(FriendlyByteBuf data) {
        super.writeToStream(data);
        data.writeBoolean(working);
        for (int x = 0; x < MAX_THREAD; x ++) {
            var is = AEItemKey.of(this.inv.getStackInSlot(x));
            data.writeBoolean(is != null);
            if (is != null) {
                is.writeToPacket(data);
            }
        }
    }

    @Override
    protected void onOrientationChanged(BlockOrientation orientation) {
        super.onOrientationChanged(orientation);
        this.setPowerSides(getGridConnectableSides(orientation));
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode node) {
        return new TickingRequest(TickRates.Charger, false, true);
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        doWork(ticksSinceLastCall);
        return TickRateModulation.FASTER;
    }

    public boolean isWorking() {
        return working;
    }

    /**
     * Allow cranking from the top or bottom.
     */
    @Nullable
    public ICrankable getCrankable(Direction direction) {
        if (direction != getFront()) {
            return new Crankable(this);
        }
        return null;
    }

    private void doWork(int ticksSinceLastCall) {
        if (this.level == null) {
            return;
        }
        var wasWorking = this.working;
        this.working = false;
        var changed = false;
        for (int x = 0; x < MAX_THREAD; x ++) {
            var myItem = this.inv.getStackInSlot(x);
            if (!myItem.isEmpty()) {
                if (Platform.isChargeable(myItem)) {
                    var ps = (IAEItemPowerStorage) myItem.getItem();
                    var currentPower = ps.getAECurrentPower(myItem);
                    var maxPower = ps.getAEMaxPower(myItem);
                    if (currentPower < maxPower) {
                        var chargeRate = ps.getChargeRate(myItem) * ticksSinceLastCall * AEConfig.instance().getChargerChargeRate();
                        double extractedAmount = this.extractAEPower(chargeRate, Actionable.MODULATE,
                                PowerMultiplier.CONFIG);
                        var missingChargeRate = chargeRate - extractedAmount;
                        var missingAEPower = maxPower - currentPower;
                        var toExtract = Math.min(missingChargeRate, missingAEPower);
                        var grid = getMainNode().getGrid();
                        if (grid != null) {
                            extractedAmount += grid.getEnergyService().extractAEPower(toExtract, Actionable.MODULATE, PowerMultiplier.ONE);
                        }
                        if (extractedAmount > 0) {
                            var adjustment = ps.injectAEPower(myItem, extractedAmount, Actionable.MODULATE);
                            this.setInternalCurrentPower(this.getInternalCurrentPower() + adjustment);
                            this.working = true;
                            changed = true;
                        }
                    }
                } else if (this.getInternalCurrentPower() > POWER_THRESHOLD && ChargerRecipes.findRecipe(level, myItem) != null) {
                    this.working = true;
                    if (this.level.getRandom().nextFloat() > 0.8f) {
                        this.extractAEPower(this.getInternalMaxPower(), Actionable.MODULATE, PowerMultiplier.CONFIG);
                        Item charged = Objects.requireNonNull(ChargerRecipes.findRecipe(level, myItem)).result;
                        this.inv.setItemDirect(x, new ItemStack(charged));
                        changed = true;
                    }
                }
            }

            if (this.getInternalCurrentPower() < POWER_THRESHOLD) {
                getMainNode().ifPresent(grid -> {
                    double toExtract = Math.min(800.0, this.getInternalMaxPower() - this.getInternalCurrentPower());
                    final double extracted = grid.getEnergyService().extractAEPower(toExtract, Actionable.MODULATE,
                            PowerMultiplier.ONE);

                    this.injectExternalPower(PowerUnits.AE, extracted, Actionable.MODULATE);
                });
                changed = true;
            }
        }
        if (changed || this.working != wasWorking) {
            this.markForUpdate();
        }
    }

    @Override
    public InternalInventory getInternalInventory() {
        return this.inv;
    }

    @Override
    public void onChangeInventory(InternalInventory inv, int slot) {
        getMainNode().ifPresent((grid, node) -> grid.getTickManager().wakeDevice(node));
        this.markForUpdate();
    }

    public void activate(Player player) {
        if (!Platform.hasPermissions(new DimensionalBlockPos(this), player)) {
            return;
        }
        for (int x = 0; x < MAX_THREAD; x ++) {
            var stored = this.inv.getStackInSlot(x);
            if (stored.isEmpty()) {
                ItemStack held = player.getInventory().getSelected();
                assert level != null;
                if (ChargerRecipes.findRecipe(level, held) != null || Platform.isChargeable(held)) {
                    held = player.getInventory().removeItem(player.getInventory().selected, 1);
                    this.inv.setItemDirect(x, held);
                    return;
                }
            }
        }
        for (int x = 0; x < MAX_THREAD; x ++) {
            var stored = this.inv.getStackInSlot(x);
            if (!stored.isEmpty()) {
                var drops = List.of(stored);
                this.inv.setItemDirect(x, ItemStack.EMPTY);
                Platform.spawnDrops(player.level(), this.worldPosition.relative(this.getFront()), drops);
                return;
            }
        }
    }

    private record ChargerInvFilter(TileExCharger chargerBlockEntity) implements IAEItemFilter {

        @Override
        public boolean allowInsert(InternalInventory inv, int i, ItemStack itemstack) {
            if (Platform.isChargeable(itemstack)) return true;
            assert chargerBlockEntity.level != null;
            return ChargerRecipes.allowInsert(chargerBlockEntity.level, itemstack);
        }

        @Override
        public boolean allowExtract(InternalInventory inv, int slotIndex, int amount) {
            ItemStack extractedItem = inv.getStackInSlot(slotIndex);
            if (Platform.isChargeable(extractedItem)) {
                final IAEItemPowerStorage ips = (IAEItemPowerStorage) extractedItem.getItem();
                if (ips.getAECurrentPower(extractedItem) >= ips.getAEMaxPower(extractedItem)) {
                    return true;
                }
            }
            assert chargerBlockEntity.level != null;
            return ChargerRecipes.allowExtract(chargerBlockEntity.level, extractedItem);
        }
    }

}
