package com.glodblock.github.ae2netanalyser.util;

import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import com.glodblock.github.ae2netanalyser.common.me.NetworkData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface ExternalNetwork {

    @Nullable
    IGridNode findGridNode(Level world, BlockPos pos);

    @Nullable
    NetworkData.ANode wrapGridNode(IGridNode node);

    void addExtraInfo(IGrid grid, Set<NetworkData.ANode> nodes, Set<NetworkData.ALink> links);

    boolean isValid();

}
