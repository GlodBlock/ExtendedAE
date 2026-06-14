package com.glodblock.github.appflux.common.me.inventory;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import appeng.helpers.ResourceConversion;
import com.glodblock.github.appflux.common.me.key.FluxKey;
import com.glodblock.github.appflux.common.me.key.type.EnergyType;
import com.glodblock.github.appflux.common.me.key.type.FluxKeyType;
import org.jetbrains.annotations.Nullable;

public class EnergyConversion implements ResourceConversion<EnergyResource> {

    public static final EnergyConversion INSTANCE = new EnergyConversion();

    private EnergyConversion() {
        // NO-OP
    }

    @Override
    public AEKeyType getKeyType() {
        return FluxKeyType.TYPE;
    }

    @Override
    public EnergyResource getVariant(@Nullable AEKey key) {
        return key == null ? EnergyResource.EMPTY : EnergyResource.INSTANCE;
    }

    @Override
    public @Nullable AEKey getKey(EnergyResource variant) {
        return variant.isEmpty() ? null : FluxKey.of(EnergyType.FE);
    }

    @Override
    public long getBaseSlotSize(EnergyResource variant) {
        return 1000000L;
    }

}
