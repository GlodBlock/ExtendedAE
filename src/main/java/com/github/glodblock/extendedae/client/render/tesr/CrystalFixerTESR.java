package com.github.glodblock.extendedae.client.render.tesr;

import appeng.client.render.renderable.ItemRenderable;
import appeng.client.render.tesr.ModularTESR;
import com.github.glodblock.extendedae.common.tileentities.TileCrystalFixer;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class CrystalFixerTESR extends ModularTESR<TileCrystalFixer> {

    private static final float HALF_PI = (float) (Math.PI / 2f);
    private static final Transformation T = new Transformation(
            new Vector3f(0.5f, 0.5f, 0.4f),
            new Quaternionf().rotateYXZ(0, -HALF_PI, 0),
            null, null
    );

    public CrystalFixerTESR(BlockEntityRendererProvider.Context context) {
        super(new ItemRenderable<>(CrystalFixerTESR::renderItem));
    }

    private static Pair<ItemStack, Transformation> renderItem(TileCrystalFixer te) {
        return new ImmutablePair<>(te.getInternalInventory().getStackInSlot(0), T);
    }

}
