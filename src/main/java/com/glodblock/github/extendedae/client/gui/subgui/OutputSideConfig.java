package com.glodblock.github.extendedae.client.gui.subgui;

import appeng.api.config.ActionItems;
import appeng.api.orientation.RelativeSide;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.AESubScreen;
import appeng.client.gui.Icon;
import appeng.client.gui.widgets.ActionButton;
import appeng.client.gui.widgets.TabButton;
import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantics;
import com.glodblock.github.extendedae.client.button.OutputButton;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;

public class OutputSideConfig<C extends AEBaseMenu, P extends AEBaseScreen<C>> extends AESubScreen<C, P> {

    private final EnumMap<Direction, OutputButton> btns = new EnumMap<>(Direction.class);

    public OutputSideConfig(P parent, ItemStack icon, AEBaseBlockEntity host, List<Direction> selectedSides, BiConsumer<Direction, Boolean> setter) {
        super(parent, "/screens/set_output_sides.json");
        var button = new TabButton(Icon.BACK, icon.getHoverName(), btn -> returnToParent());
        this.widgets.add("return", button);
        var clear = new ActionButton(ActionItems.S_CLOSE, b -> {
            for (var btn : this.btns.values()) {
                btn.setOn(false);
            }
            for (var side : Direction.values()) {
                setter.accept(side, false);
            }
        });
        clear.setHalfSize(true);
        clear.setDisableBackground(true);
        clear.setMessage(Component.translatable("gui.extendedae.set_output_sides.clear"));
        this.widgets.add("clear", clear);
        for (var side : Direction.values()) {
            var btn = new OutputButton(b -> {
                ((OutputButton) b).flip();
                setter.accept(side, ((OutputButton) b).isOn());
            });
            if (host.getLevel() != null) {
                btn.setDisplay(host.getLevel().getBlockState(host.getBlockPos().relative(side)).getBlock());
            }
            this.btns.put(side, btn);
        }
        for (var side : selectedSides) {
            this.btns.get(side).setOn(true);
        }
        for (var relative : RelativeSide.values()) {
            var side = host.getOrientation().getSide(relative);
            this.widgets.add(relative.name().toLowerCase(Locale.ROOT), this.btns.get(side));
        }
    }

    @Override
    protected void init() {
        super.init();
        setSlotsHidden(SlotSemantics.TOOLBOX, true);
    }

}
