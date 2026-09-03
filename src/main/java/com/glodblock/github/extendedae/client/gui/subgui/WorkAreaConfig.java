package com.glodblock.github.extendedae.client.gui.subgui;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.AESubScreen;
import appeng.client.gui.Icon;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.widgets.TabButton;
import appeng.core.AppEng;
import appeng.menu.AEBaseMenu;
import com.glodblock.github.extendedae.client.button.EPPButton;
import com.glodblock.github.extendedae.client.gui.GuiVacuumInterface;
import com.glodblock.github.extendedae.common.tileentities.TileVacuumInterface;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.function.BooleanSupplier;

public class WorkAreaConfig<C extends AEBaseMenu, P extends AEBaseScreen<C>> extends AESubScreen<C, P> {

    private static final ResourceLocation TEXTURE = AppEng.makeId("textures/guis/work_area.png");
    private static final Blitter LEFT_OFF = Blitter.texture(TEXTURE).src(122, 0, 4, 8);
    private static final Blitter LEFT_ON = Blitter.texture(TEXTURE).src(126, 0, 4, 8);
    private static final Blitter LEFT_DISABLE = Blitter.texture(TEXTURE).src(130, 0, 4, 8);
    private static final Blitter RIGHT_OFF = Blitter.texture(TEXTURE).src(122, 8, 4, 8);
    private static final Blitter RIGHT_ON = Blitter.texture(TEXTURE).src(126, 8, 4, 8);
    private static final Blitter RIGHT_DISABLE = Blitter.texture(TEXTURE).src(130, 8, 4, 8);

    private final GuiVacuumInterface.DataGetter dataGetter;

    public WorkAreaConfig(P parent, ItemStack icon, GuiVacuumInterface.DataSetter setter, GuiVacuumInterface.DataGetter getter) {
        super(parent, "/screens/set_work_area.json");
        this.dataGetter = getter;
        var button = new TabButton(Icon.BACK, icon.getHoverName(), btn -> this.returnToParent());
        this.widgets.add("return", button);
        for (int x = 0; x < 6; x++) {
            var btn = new NumberButton(NumberButton.LEFT, x % 3, x < 3, setter, getter);
            this.widgets.add("left_" + (x + 1), btn);
        }
        for (int x = 0; x < 6; x++) {
            var btn = new NumberButton(NumberButton.RIGHT, x % 3, x < 3, setter, getter);
            this.widgets.add("right_" + (x + 1), btn);
        }
    }

    @Override
    public void drawFG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY) {
        super.drawFG(guiGraphics, offsetX, offsetY, mouseX, mouseY);
        int sx = 22, sy = 29, dx = 39, dy = 27;
        for (int x = 0; x < 3; x++) {
            var text = String.valueOf(this.dataGetter.get(x, true));
            int tw = this.font.width(text) / 2;
            guiGraphics.drawString(this.font, text, sx + dx * x - tw, sy, 0xFFFFFFFF, false);
        }
        for (int x = 0; x < 3; x++) {
            var text = String.valueOf(this.dataGetter.get(x, false));
            int tw = this.font.width(text) / 2;
            guiGraphics.drawString(this.font, text, sx + dx * x - tw, sy + dy, 0xFFFFFFFF, false);
        }
    }

    private static class NumberButton extends EPPButton {

        static final int LEFT = 0;
        static final int RIGHT = 1;

        final int side;
        final BooleanSupplier checker;

        public NumberButton(int side, int seq, boolean size, GuiVacuumInterface.DataSetter setter, GuiVacuumInterface.DataGetter getter) {
            super(b -> setter.set(delta(side), seq, size));
            this.side = side;
            this.checker = () -> {
                var val = getter.get(seq, size);
                var test = val + delta(side);
                if (size) {
                    return test >= 1 && test <= TileVacuumInterface.MAX_SIZE;
                } else {
                    return test >= -TileVacuumInterface.MAX_OFFSET && test <= TileVacuumInterface.MAX_OFFSET;
                }
            };
        }

        @Override
        public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partial) {
            if (this.visible) {
                this.icon(this.isHovered(), this.checker.getAsBoolean()).dest(getX(), getY()).zOffset(2).blit(guiGraphics);
            }
        }

        @Override
        protected Blitter getBlitterIcon() {
            return null;
        }

        Blitter icon(boolean hovered, boolean enabled) {
            if (enabled) {
                if (hovered) {
                    if (this.side == LEFT) {
                        return LEFT_ON;
                    } else if (this.side == RIGHT) {
                        return RIGHT_ON;
                    }
                } else {
                    if (this.side == LEFT) {
                        return LEFT_OFF;
                    } else if (this.side == RIGHT) {
                        return RIGHT_OFF;
                    }
                }
            } else {
                if (this.side == LEFT) {
                    return LEFT_DISABLE;
                } else if (this.side == RIGHT) {
                    return RIGHT_DISABLE;
                }
            }
            throw new IllegalStateException();
        }

        static int delta(int side) {
            if (side == LEFT) {
                return -1;
            } else if (side == RIGHT) {
                return 1;
            } else {
                return 0;
            }
        }

    }

}
