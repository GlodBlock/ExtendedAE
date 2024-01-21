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
    private GTEUExternalStorageStrategy gteuHandler;

    private boolean isGTPresent() {
        return ModList.get().isLoaded("gtceu");
    }

    public FEExternalStorageStrategy(ServerLevel level, BlockPos fromPos, Direction fromSide) {
        this.apiCache = BlockApiCache.create(ForgeCapabilities.ENERGY, level, fromPos);
        this.fromSide = fromSide;
        if (isGTPresent()) {
            this.gteuHandler = new GTEUExternalStorageStrategy(level, fromPos, fromSide);
        }
    }

    @Override
    public @Nullable MEStorage createWrapper(boolean extractableOnly, Runnable callback) {
        if (isGTPresent()) {
            var res = this.gteuHandler.createWrapper(extractableOnly, callback);
            if (res != null) {
                return res;
            }
        }
        var storage = this.apiCache.find(this.fromSide);
        if (storage == null) {
            return null;
        }
        return new FEStorageWrapper(storage, callback);
    }

    private record FEStorageWrapper(IEnergyStorage storage, Runnable callback) implements MEStorage {

        @Override
        public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
            if (FluxKey.of(EnergyType.FE).equals(what)) {
                int in = this.storage.receiveEnergy(AFUtil.clampLong(amount), mode.isSimulate());
                if (in > 0 && mode == Actionable.MODULATE) {
                    this.callback.run();
                }
                return in;
            }
            return 0;
        }

        @Override
        public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
            if (FluxKey.of(EnergyType.FE).equals(what)) {
                int out = this.storage.extractEnergy(AFUtil.clampLong(amount), mode.isSimulate());
                if (out > 0 && mode == Actionable.MODULATE) {
                    this.callback.run();
                }
                return out;
            }
            return 0;
        }

        @Override
        public void getAvailableStacks(KeyCounter out) {
            int stored = this.storage.getEnergyStored();
            if (stored > 0) {
                out.add(FluxKey.of(EnergyType.FE), stored);
            }
        }

        @Override
        public Component getDescription() {
            return GuiText.ExternalStorage.text(FluxKeyType.TYPE.getDescription());
        }

    }
}
