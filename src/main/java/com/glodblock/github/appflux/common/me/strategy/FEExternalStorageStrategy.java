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
import com.glodblock.github.appflux.util.AFUtil;
import com.glodblock.github.appflux.xmod.mek.MekEnergy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class FEExternalStorageStrategy implements ExternalStorageStrategy {

    private final Direction fromSide;
    private final BlockApiCache<IEnergyStorage> apiCache;
    private final MekEnergy inductionHandler;

    public FEExternalStorageStrategy(ServerLevel level, BlockPos fromPos, Direction fromSide) {
        this.apiCache = BlockApiCache.create(ForgeCapabilities.ENERGY, level, fromPos);
        this.fromSide = fromSide;
        if (ModList.get().isLoaded("mekanism")) {
            this.inductionHandler = MekEnergy.INDUCTION.create(level, fromPos);
        } else {
            this.inductionHandler = MekEnergy.NULL.create(level, fromPos);
        }
    }

    @Override
    public @Nullable MEStorage createWrapper(boolean extractableOnly, Runnable callback) {
        var storage = this.apiCache.find(this.fromSide);
        if (storage == null && !this.inductionHandler.valid()) {
            return null;
        }
        return new FEStorageWrapper(storage, this.inductionHandler, this.fromSide, callback);
    }

    private record FEStorageWrapper(@Nullable IEnergyStorage storage, MekEnergy inductionStorage, Direction side, Runnable callback) implements MEStorage {

        @Override
        public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
            if (FluxKey.of(EnergyType.FE).equals(what)) {
                if (this.inductionStorage.valid()) {
                    long in = this.inductionStorage.input(amount, mode, this.side);
                    if (in > 0 && mode == Actionable.MODULATE) {
                        this.callback.run();
                    }
                    return in;
                }
                if (this.storage != null) {
                    int in = this.storage.receiveEnergy(AFUtil.clampLong(amount), mode.isSimulate());
                    if (in > 0 && mode == Actionable.MODULATE) {
                        this.callback.run();
                    }
                    return in;
                }
            }
            return 0;
        }

        @Override
        public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
            if (FluxKey.of(EnergyType.FE).equals(what)) {
                if (this.inductionStorage.valid()) {
                    long out = this.inductionStorage.output(amount, mode, this.side);
                    if (out > 0 && mode == Actionable.MODULATE) {
                        this.callback.run();
                    }
                    return out;
                }
                if (this.storage != null) {
                    int out = this.storage.extractEnergy(AFUtil.clampLong(amount), mode.isSimulate());
                    if (out > 0 && mode == Actionable.MODULATE) {
                        this.callback.run();
                    }
                    return out;
                }
            }
            return 0;
        }

        @Override
        public void getAvailableStacks(KeyCounter out) {
            if (this.inductionStorage.valid()) {
                long stored = this.inductionStorage.getStored();
                if (stored > 0) {
                    out.add(FluxKey.of(EnergyType.FE), stored);
                }
            } else if (this.storage != null) {
                int stored = this.storage.getEnergyStored();
                if (stored > 0) {
                    out.add(FluxKey.of(EnergyType.FE), stored);
                }
            }
        }

        @Override
        public Component getDescription() {
            return GuiText.ExternalStorage.text(FluxKeyType.TYPE.getDescription());
        }

    }
}
