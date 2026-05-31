package com.glodblock.github.extendedae.client.render.tesr.state;

import appeng.api.orientation.BlockOrientation;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MultiItemState extends BlockEntityRenderState {

    public BlockOrientation blockOrientation;
    public List<TransformedItemState> items = new ArrayList<>();

    public void clearItems() {
        this.items.clear();
    }

    public void setupItem(Consumer<ItemStackRenderState> setup, Transformation transformation) {
        var item = new ItemStackRenderState();
        setup.accept(item);
        this.items.add(new TransformedItemState(item, transformation));
    }

    public record TransformedItemState(ItemStackRenderState item, Transformation transform) {

    }

}
