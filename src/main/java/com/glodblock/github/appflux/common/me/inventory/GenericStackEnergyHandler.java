package com.glodblock.github.appflux.common.me.inventory;

import appeng.api.behaviors.GenericInternalInventory;
import appeng.helpers.externalstorage.GenericStackInvHandler;
import com.glodblock.github.appflux.common.me.key.type.FluxKeyType;

@SuppressWarnings("UnstableApiUsage")
public class GenericStackEnergyHandler extends GenericStackInvHandler<EnergyResource> {

    public GenericStackEnergyHandler(GenericInternalInventory inv) {
        super(EnergyConversion.INSTANCE, FluxKeyType.TYPE, inv);
    }

}
