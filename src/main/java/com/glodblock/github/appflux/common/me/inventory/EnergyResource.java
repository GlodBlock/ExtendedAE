package com.glodblock.github.appflux.common.me.inventory;

import net.neoforged.neoforge.transfer.resource.Resource;

public class EnergyResource implements Resource {

    public static final EnergyResource INSTANCE = new EnergyResource();
    public static final EnergyResource EMPTY = new EnergyResource();

    private EnergyResource() {
        assert this == EMPTY || this == INSTANCE;
    }

    @Override
    public boolean isEmpty() {
        return this == EMPTY;
    }

}
