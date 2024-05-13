package com.glodblock.github.extendedae.api.caps;

import appeng.api.implementations.blockentities.ICrankable;
import net.minecraft.core.Direction;

public interface ICrankPowered {

    ICrankable getCrankable(Direction direction);

}
