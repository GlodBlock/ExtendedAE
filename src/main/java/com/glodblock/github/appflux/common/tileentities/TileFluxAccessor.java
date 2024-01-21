package com.glodblock.github.appflux.common.tileentities;

import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageService;
import appeng.blockentity.grid.AENetworkBlockEntity;
import com.glodblock.github.appflux.common.AFItemAndBlock;
import com.glodblock.github.appflux.common.caps.NetworkFEPower;
import com.glodblock.github.glodium.util.GlodUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TileFluxAccessor extends AENetworkBlockEntity {

    public TileFluxAccessor(BlockPos pos, BlockState blockState) {
        super(GlodUtil.getTileType(TileFluxAccessor.class, TileFluxAccessor::new, AFItemAndBlock.FLUX_ACCESSOR), pos, blockState);
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        /*if (ModList.get().isLoaded("gtceu")) {
            if (cap == GTCapability.CAPABILITY_ENERGY_CONTAINER && this.getStorage() != null) {
                return LazyOptional.of(() -> new NetworkGTEUPower(this.getStorage(), this.getSource(), this.getAsker(side), side != null ? side.getOpposite() : null)).cast();
            }
        }*/
        if (cap == ForgeCapabilities.ENERGY && this.getStorage() != null) {
            return LazyOptional.of(() -> new NetworkFEPower(this.getStorage(), this.getSource())).cast();
        }
        return super.getCapability(cap, side);
    }

    private BlockEntity getAsker(Direction side) {
        if (this.level != null && side != null) {
            this.level.getBlockEntity(this.worldPosition.offset(side.getNormal()));
        }
        return null;
    }

    private IStorageService getStorage() {
        if (this.getGridNode() != null) {
            return this.getGridNode().getGrid().getStorageService();
        }
        return null;
    }

    private IActionSource getSource() {
        return IActionSource.ofMachine(this);
    }

}
