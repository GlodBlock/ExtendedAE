package com.glodblock.github.appflux.util.helpers;

import net.minecraft.core.Direction;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public final class Constants {

    public static final Set<Direction> ALL_DIRECTIONS = EnumSet.allOf(Direction.class);
    public static final Set<Direction> NO_DIRECTIONS = Set.of();
    public static final List<Direction> ALL_DIRECTIONS_LIST = List.of(Direction.values());

}
