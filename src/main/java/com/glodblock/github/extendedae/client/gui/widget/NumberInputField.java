package com.glodblock.github.extendedae.client.gui.widget;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.NumberEntryType;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.NumberEntryWidget;
import com.glodblock.github.extendedae.util.Ae2ReflectClient;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.Rect2i;

import java.util.function.Consumer;

public class NumberInputField extends NumberEntryWidget {

    private Consumer<AbstractWidget> remover;

    public NumberInputField(ScreenStyle style, NumberEntryType type) {
        super(style, type);
    }

    @Override
    public void populateScreen(Consumer<AbstractWidget> addWidget, Rect2i bounds, AEBaseScreen<?> screen) {
        super.populateScreen(addWidget, bounds, screen);
        if (this.remover != null) {
            var btns = Ae2ReflectClient.getButton(this);
            btns.forEach(b -> remover.accept(b));
            btns.clear();
        }
    }

    public void addRemover(Consumer<AbstractWidget> r) {
        this.remover = r;
    }

}
