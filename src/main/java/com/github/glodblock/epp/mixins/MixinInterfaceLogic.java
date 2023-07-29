package com.github.glodblock.epp.mixins;

import appeng.api.networking.crafting.ICraftingRequester;
import appeng.api.stacks.GenericStack;
import appeng.helpers.InterfaceLogic;
import appeng.helpers.MultiCraftingTracker;
import appeng.util.ConfigInventory;
import com.github.glodblock.epp.util.mixinutil.ExtendedInterfaceLogic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(InterfaceLogic.class)
public abstract class MixinInterfaceLogic implements ExtendedInterfaceLogic {

    @Final
    @Mutable
    @Shadow(remap = false)
    private GenericStack[] plannedWork;
    @Final
    @Mutable
    @Shadow(remap = false)
    private ConfigInventory storage;
    @Final
    @Mutable
    @Shadow(remap = false)
    private ConfigInventory config;
    @Final
    @Mutable
    @Shadow(remap = false)
    private MultiCraftingTracker craftingTracker;

    @Shadow(remap = false)
    protected abstract void onConfigRowChanged();

    @Shadow(remap = false)
    protected abstract void onStorageChanged();

    @Override
    public void extend() {
        int EX_SLOT = 18;
        this.plannedWork = new GenericStack[EX_SLOT];
        this.config = ConfigInventory.configStacks(null, EX_SLOT, this::onConfigRowChanged, false);
        this.storage = ConfigInventory.storage(EX_SLOT, this::onStorageChanged);
        this.craftingTracker = new MultiCraftingTracker((ICraftingRequester) this, EX_SLOT);
        this.config.useRegisteredCapacities();
        this.storage.useRegisteredCapacities();
    }

}
