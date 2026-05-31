package com.glodblock.github.extendedae.client.render.tesr.state;

import appeng.api.orientation.BlockOrientation;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;

public class SingleItemState extends BlockEntityRenderState {

    public BlockOrientation blockOrientation;
    public Transformation transform;
    public ItemStackRenderState item = new ItemStackRenderState();

}
