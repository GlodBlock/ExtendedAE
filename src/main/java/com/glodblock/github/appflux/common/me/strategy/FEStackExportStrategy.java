package com.glodblock.github.appflux.common.me.strategy;

import appeng.api.behaviors.StackExportStrategy;
import appeng.api.behaviors.StackTransferContext;
import appeng.api.config.Actionable;
import appeng.api.stacks.AEKey;
import appeng.api.storage.StorageHelper;
import appeng.util.BlockApiCache;
import com.glodblock.github.appflux.common.me.key.FluxKey;
import com.glodblock.github.appflux.util.AFUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;

@SuppressWarnings("UnstableApiUsage")
public class FEStackExportStrategy implements StackExportStrategy {
    private final BlockApiCache<? extends IEnergyStorage> apiCache;
    private final Direction fromSide;

    public FEStackExportStrategy(ServerLevel level, BlockPos fromPos, Direction fromSide) {
        this.apiCache = BlockApiCache.create(ForgeCapabilities.ENERGY, level, fromPos);
        this.fromSide = fromSide;
    }

    @Override
    public long transfer(StackTransferContext context, AEKey what, long amount) {
        if (!(what instanceof FluxKey)) {
            return 0;
        }
        var storage = this.apiCache.find(this.fromSide);
        if (storage == null) {
            return 0;
        }

        var inv = context.getInternalStorage();

        var toAdd = StorageHelper.poweredExtraction(context.getEnergySource(), inv.getInventory(), what, amount, context.getActionSource(), Actionable.SIMULATE);

        toAdd = storage.receiveEnergy(AFUtil.clampLong(toAdd), true);

        if (toAdd > 0) {
            var realExt = AFUtil.clampLong(StorageHelper.poweredExtraction(context.getEnergySource(), inv.getInventory(), what, toAdd, context.getActionSource(), Actionable.MODULATE));
            var realAdd = storage.receiveEnergy(realExt, false);
            if (realAdd < realExt) {
                inv.getInventory().insert(what, realExt - realAdd, Actionable.MODULATE, context.getActionSource());
            }
            return realAdd;
        }
        return 0;
    }

    @Override
    public long push(AEKey what, long amount, Actionable mode) {
        if (!(what instanceof FluxKey)) {
            return 0;
        }
        var storage = this.apiCache.find(this.fromSide);
        if (storage == null) {
            return 0;
        }

        return storage.receiveEnergy(AFUtil.clampLong(amount), mode.isSimulate());
    }
}
