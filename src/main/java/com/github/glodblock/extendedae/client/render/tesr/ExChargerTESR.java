package com.github.glodblock.extendedae.client.render.tesr;

import appeng.client.render.renderable.ItemRenderable;
import appeng.client.render.tesr.ModularTESR;
import com.github.glodblock.extendedae.common.tileentities.TileExCharger;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ExChargerTESR extends ModularTESR<TileExCharger> {

    private static final float HALF_PI = (float) (Math.PI / 2f);
    private static final float[] X = new float[] {0.27f, 0.4f, 0.6f, 0.73f};

    public ExChargerTESR(BlockEntityRendererProvider.Context context) {
        super(
                new ItemRenderable<>(te -> renderItem(te, 0)),
                new ItemRenderable<>(te -> renderItem(te, 1)),
                new ItemRenderable<>(te -> renderItem(te, 2)),
                new ItemRenderable<>(te -> renderItem(te, 3))
        );
    }

    private static Pair<ItemStack, Transformation> renderItem(TileExCharger te, int index) {
        var bo = new Quaternionf().rotateYXZ(HALF_PI, 0, 0);
        Transformation transform = new Transformation(new Vector3f(X[index], 0.375f, 0.5f), bo, null, null);
        return new ImmutablePair<>(te.getInternalInventory().getStackInSlot(index), transform);
    }

}
