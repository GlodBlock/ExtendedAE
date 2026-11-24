package com.glodblock.github.ae2netanalyser.client.gui.sub;

import appeng.client.gui.AESubScreen;
import com.glodblock.github.ae2netanalyser.client.gui.GuiAnalyser;
import com.glodblock.github.ae2netanalyser.client.gui.buttons.ChooseButton;
import com.glodblock.github.ae2netanalyser.client.gui.buttons.ColorButton;
import com.glodblock.github.ae2netanalyser.client.gui.buttons.DragBar;
import com.glodblock.github.ae2netanalyser.container.ContainerAnalyser;
import com.glodblock.github.glodium.client.render.ColorData;

import java.util.function.Consumer;

public class GuiColorConfig extends AESubScreen<ContainerAnalyser, GuiAnalyser> {

    private final Consumer<ColorData> syncer;
    private ColorData display;
    private double red;
    private double green;
    private double blue;
    private double alpha;

    public GuiColorConfig(GuiAnalyser parent, ColorData initColor, Consumer<ColorData> syncer) {
        super(parent, "/screens/color_config.json");
        this.syncer = syncer;
        this.red = initColor.getRf();
        this.green = initColor.getGf();
        this.blue = initColor.getBf();
        this.alpha = initColor.getAf();
        this.display = new ColorData(initColor.toARGB());
        this.widgets.add("red", new DragBar(this.red, bar -> {
            this.red = bar.getValue();
            this.updateDisplay();
        }));
        this.widgets.add("green", new DragBar(this.green, bar -> {
            this.green = bar.getValue();
            this.updateDisplay();
        }));
        this.widgets.add("blue", new DragBar(this.blue, bar -> {
            this.blue = bar.getValue();
            this.updateDisplay();
        }));
        this.widgets.add("alpha", new DragBar(this.alpha, bar -> {
            this.alpha = bar.getValue();
            this.updateDisplay();
        }));
        this.widgets.add("show", new ColorButton(() -> {}, () -> this.display));
        this.widgets.add("confirm", new ChooseButton(ChooseButton.Choice.OK, this::save));
        this.widgets.add("cancel", new ChooseButton(ChooseButton.Choice.CANCEL, this::returnToParent));
    }

    private void updateDisplay() {
        this.display = new ColorData((float) this.alpha, (float) this.red, (float) this.green, (float) this.blue);
    }

    private void save() {
        this.syncer.accept(this.display);
        this.returnToParent();
    }

}
