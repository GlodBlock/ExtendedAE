package com.glodblock.github.appflux.common.me.strategy;

import appeng.api.behaviors.ExternalStorageStrategy;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;
import appeng.core.localization.GuiText;
import appeng.util.BlockApiCache;
import com.glodblock.github.appflux.common.me.key.FluxKey;
import com.glodblock.github.appflux.common.me.key.type.EnergyType;
import com.glodblock.github.appflux.common.me.key.type.FluxKeyType;
import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.forge.GTCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class GTEUExternalStorageStrategy implements ExternalStorageStrategy {

    private final Direction fromSide;
    private final BlockApiCache<IEnergyContainer> apiCache;

    public GTEUExternalStorageStrategy(ServerLevel level, BlockPos fromPos, Direction fromSide) {
        this.apiCache = BlockApiCache.create(GTCapability.CAPABILITY_ENERGY_CONTAINER, level, fromPos);
        this.fromSide = fromSide;
    }

    @Override
    public @Nullable MEStorage createWrapper(boolean extractableOnly, Runnable callback) {
        var storage = this.apiCache.find(this.fromSide);
        if (storage == null) {
            return null;
        }
        return new GTEUStorageWrapper(storage, callback);
    }

    private record GTEUStorageWrapper(IEnergyContainer storage, Runnable callback) implements MEStorage {

        @Override
        public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
            if (FluxKey.of(EnergyType.GTEU).equals(what)) {
                if (mode == Actionable.MODULATE) {
                    long out = this.storage.addEnergy(amount);
                    if (out > 0) {
                        this.callback.run();
                    }
                    return out;
                } else {
                    long space = this.storage.getEnergyCanBeInserted();
                    return Math.min(space, amount);
                }
            }
            return 0;
        }

        @Override
        public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
            if (FluxKey.of(EnergyType.GTEU).equals(what)) {
                if (mode == Actionable.MODULATE) {
                    long out = this.storage.removeEnergy(amount);
                    if (out > 0) {
                        this.callback.run();
                    }
                    return out;
                } else {
                    long stored = this.storage.getEnergyStored();
                    return Math.min(stored, amount);
                }
            }
            return 0;
        }

        @Override
        public void getAvailableStacks(KeyCounter out) {
            long stored = this.storage.getEnergyStored();
            if (stored > 0) {
                out.add(FluxKey.of(EnergyType.GTEU), stored);
            }
        }

        @Override
        public Component getDescription() {
            return GuiText.ExternalStorage.text(FluxKeyType.TYPE.getDescription());
        }

    }
}
