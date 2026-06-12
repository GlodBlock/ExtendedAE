package com.glodblock.github.extendedae.xmod.kjs;

import appeng.api.client.StorageCellModels;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import com.glodblock.github.extendedae.common.items.ItemInfinityCell;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@ReturnsSelf
public class InfinityCellBuilder extends ItemBuilder {

    public static final List<Runnable> MODEL_BINDINGS = new ArrayList<>();
    private Supplier<AEKey> record;
    private Identifier model;

    public InfinityCellBuilder(Identifier id) {
        super(id);
    }

    @Info("Create an infinity cell with given AEKey.")
    public InfinityCellBuilder type(Supplier<AEKey> key) {
        this.record = key;
        return this;
    }

    @Info("Create an infinity cell with given item.")
    public InfinityCellBuilder itemType(Identifier id) {
        this.record = new TraceableSupplier(id.toString(), () -> AEItemKey.of(BuiltInRegistries.ITEM.get(id).get().value()));
        return this;
    }

    @Info("Create an infinity cell with given fluid.")
    public InfinityCellBuilder fluidType(Identifier id) {
        this.record = new TraceableSupplier(id.toString(), () -> AEFluidKey.of(BuiltInRegistries.FLUID.get(id).get().value()));
        return this;
    }

    @Info("Set infinity cell's model in ME drive.")
    public InfinityCellBuilder cellModel(Identifier model) {
        this.model = model;
        return this;
    }

    @Override
    public @NotNull Item createObject() {
        var cell = new ItemInfinityCell(this.record, this.createItemProperties().stacksTo(1));
        if (this.model != null) {
            MODEL_BINDINGS.add(() -> StorageCellModels.registerModel(cell, this.model));
        }
        return cell;
    }

    record TraceableSupplier(String err, Supplier<AEKey> supplier) implements Supplier<AEKey> {

        @Override
        public AEKey get() {
            var key = this.supplier.get();
            if (key == null) {
                throw new NullPointerException("Invalid custom infinity cell, check your KubeJS script. ID: %s.".formatted(this.err));
            }
            return key;
        }

    }

}
