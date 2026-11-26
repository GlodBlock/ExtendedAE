package com.glodblock.github.ae2netanalyser.client.gui.sub;

import appeng.client.gui.AESubScreen;
import com.glodblock.github.ae2netanalyser.client.gui.GuiAnalyser;
import com.glodblock.github.ae2netanalyser.client.gui.buttons.ChooseButton;
import com.glodblock.github.ae2netanalyser.client.gui.buttons.DragBar;
import com.glodblock.github.ae2netanalyser.client.gui.elements.ColorDisplay;
import com.glodblock.github.ae2netanalyser.container.ContainerAnalyser;
import com.glodblock.github.glodium.client.render.ColorData;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GuiColorConfig extends AESubScreen<ContainerAnalyser, GuiAnalyser> {

    private final Consumer<ColorData> syncer;
    private final List<DragBar> dragBars = new ArrayList<>();
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
        this.widgets.add("red", this.addDragBar(new DragBar(this.red, bar -> {
            this.red = bar.getValue();
            this.updateDisplay();
        })));
        this.widgets.add("green", this.addDragBar(new DragBar(this.green, bar -> {
            this.green = bar.getValue();
            this.updateDisplay();
        })));
        this.widgets.add("blue", this.addDragBar(new DragBar(this.blue, bar -> {
            this.blue = bar.getValue();
            this.updateDisplay();
        })));
        this.widgets.add("alpha", this.addDragBar(new DragBar(this.alpha, bar -> {
            this.alpha = bar.getValue();
            this.updateDisplay();
        })));
        this.widgets.add("show", new ColorDisplay(() -> this.display));
        this.widgets.add("confirm", new ChooseButton(ChooseButton.Choice.OK, this::save));
        this.widgets.add("cancel", new ChooseButton(ChooseButton.Choice.CANCEL, this::returnToParent));
    }

    private DragBar addDragBar(DragBar dragBar) {
        this.dragBars.add(dragBar);
        return dragBar;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
        for (var dragBar : this.dragBars) {
            if (dragBar.mouseDragged(mouseX, mouseY, mouseButton, dragX, dragY)) {
                return true;
            }
        }
        return super.mouseDragged(mouseX, mouseY, mouseButton, dragX, dragY);
    }

    private void updateDisplay() {
        this.display = new ColorData((float) this.alpha, (float) this.red, (float) this.green, (float) this.blue);
    }

    private void save() {
        this.syncer.accept(this.display);
        this.returnToParent();
    }

}
