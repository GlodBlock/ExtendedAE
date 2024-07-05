package com.glodblock.github.appflux.mixins;

import appeng.api.upgrades.Upgrades;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.implementations.PatternProviderScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.UpgradesPanel;
import appeng.core.localization.GuiText;
import appeng.menu.SlotSemantics;
import appeng.menu.implementations.PatternProviderMenu;
import com.glodblock.github.appflux.util.helpers.IUpgradableMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(PatternProviderScreen.class)
public abstract class MixinPatternProviderScreen<C extends PatternProviderMenu> extends AEBaseScreen<C> {

    @Inject(
            method = "<init>",
            at = @At("TAIL"),
            remap = false
    )
    private void initUpgrade(PatternProviderMenu menu, Inventory playerInventory, Component title, ScreenStyle style, CallbackInfo ci) {
        this.widgets.add("upgrades", new UpgradesPanel(menu.getSlots(SlotSemantics.UPGRADE), this::af_$getCompatibleUpgrades));
    }

    @Unique
    private List<Component> af_$getCompatibleUpgrades() {
        var list = new ArrayList<Component>();
        list.add(GuiText.CompatibleUpgrades.text());
        list.addAll(Upgrades.getTooltipLinesForMachine(((IUpgradableMenu) menu).getUpgrades().getUpgradableItem()));
        return list;
    }

    public MixinPatternProviderScreen(C menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

}
