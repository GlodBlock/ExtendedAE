package com.glodblock.github.ae2netanalyser.client.gui.buttons;

import appeng.client.gui.style.Blitter;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CycleButton extends BaseButton {

    private final ArrayList<Blitter> icons = new ArrayList<>();
    private final ArrayList<NameTooltip> messages = new ArrayList<>();
    private final ArrayList<Button.OnPress> actions = new ArrayList<>();
    private int state;
    private int size = 0;

    public CycleButton() {
        super(b -> {});
    }

    public void addActionPair(Blitter icon, NameTooltip desc, Button.OnPress action) {
        this.icons.add(icon);
        this.messages.add(desc);
        this.actions.add(action);
        this.size ++;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return this.state;
    }

    @Override
    Blitter getBlitterIcon() {
        return this.icons.get(this.state);
    }

    @Override
    public @NotNull Component getMessage() {
        return this.messages.get(this.state).getName();
    }

    @Override
    public List<Component> getTooltipMessage() {
        return this.messages.get(this.state).getTooltip();
    }

    @Override
    public void setMessage(@NotNull Component tooltip) {
        // NO-OP
    }

    @Override
    public void onPress() {
        this.actions.get(this.state).onPress(this);
        this.state = (this.state + 1) % this.size;
    }

}
