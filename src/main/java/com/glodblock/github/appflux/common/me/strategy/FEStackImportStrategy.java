package com.glodblock.github.appflux.common.me.strategy;

import appeng.api.behaviors.StackImportStrategy;
import appeng.api.behaviors.StackTransferContext;
import appeng.api.config.Actionable;
import appeng.util.BlockApiCache;
import com.glodblock.github.appflux.common.me.key.FluxKey;
import com.glodblock.github.appflux.common.me.key.type.EnergyType;
import com.glodblock.github.appflux.common.me.key.type.FluxKeyType;
import com.glodblock.github.appflux.util.AFUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;

@SuppressWarnings("UnstableApiUsage")
public class FEStackImportStrategy implements StackImportStrategy {

    private final BlockApiCache<? extends IEnergyStorage> apiCache;
    private final Direction fromSide;

    public FEStackImportStrategy(ServerLevel level, BlockPos fromPos, Direction fromSide) {
        this.apiCache = BlockApiCache.create(ForgeCapabilities.ENERGY, level, fromPos);
        this.fromSide = fromSide;
    }

    @Override
    public boolean transfer(StackTransferContext context) {
        if (!context.isKeyTypeEnabled(FluxKeyType.TYPE)) {
            return false;
        }

        var adjacentHandler = this.apiCache.find(this.fromSide);
        var resource = FluxKey.of(EnergyType.FE);

        if (adjacentHandler == null) {
            return false;
        }
        if (!context.isInFilter(resource)) {
            return false;
        }

        var remainingTransferAmount = context.getOperationsRemaining() * (long) FluxKeyType.TYPE.getAmountPerOperation();
        var inv = context.getInternalStorage();
        var toExt = inv.getInventory().insert(resource, remainingTransferAmount, Actionable.SIMULATE, context.getActionSource());

        toExt = adjacentHandler.extractEnergy(AFUtil.clampLong(toExt), true);

        if (toExt > 0) {
            var realExt = adjacentHandler.extractEnergy(AFUtil.clampLong(toExt), false);
            var realAdd = inv.getInventory().insert(resource, realExt, Actionable.MODULATE, context.getActionSource());

            if (realAdd < realExt) {
                adjacentHandler.receiveEnergy((int) (realExt - realAdd), false);
            }

            var opsUsed = Math.max(1, realAdd / FluxKeyType.TYPE.getAmountPerOperation());
            context.reduceOperationsRemaining(opsUsed);
        }

        return false;
    }
}
