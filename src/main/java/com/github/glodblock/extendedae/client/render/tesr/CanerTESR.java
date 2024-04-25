package com.github.glodblock.extendedae.client.render.tesr;

import appeng.client.render.renderable.ItemRenderable;
import appeng.client.render.tesr.ModularTESR;
import com.github.glodblock.extendedae.common.tileentities.TileCaner;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Vector3f;

public class CanerTESR extends ModularTESR<TileCaner> {

    private static final Transformation T = new Transformation(new Vector3f(0.5f, 0.375f, 0.5f), null, null, null);

    public CanerTESR(BlockEntityRendererProvider.Context context) {
        super(new ItemRenderable<>(CanerTESR::renderItem));
    }

    private static Pair<ItemStack, Transformation> renderItem(TileCaner te) {
        return new ImmutablePair<>(te.getContainer().getStackInSlot(0), T);
    }

}
