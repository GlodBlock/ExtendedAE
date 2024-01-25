package com.glodblock.github.ae2netanalyser.client.gui;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;
import com.glodblock.github.ae2netanalyser.client.gui.elements.ClickableArea;
import com.glodblock.github.ae2netanalyser.client.gui.elements.ColorArea;
import com.glodblock.github.ae2netanalyser.client.gui.elements.DraggableArea;
import com.glodblock.github.ae2netanalyser.client.gui.elements.DrawableArea;
import com.glodblock.github.ae2netanalyser.client.gui.textures.Blitters;
import com.glodblock.github.ae2netanalyser.client.render.NetworkDataHandler;
import com.glodblock.github.ae2netanalyser.common.items.ItemNetworkAnalyser;
import com.glodblock.github.ae2netanalyser.common.me.AnalyserMode;
import com.glodblock.github.ae2netanalyser.common.me.netdata.LinkFlag;
import com.glodblock.github.ae2netanalyser.common.me.netdata.NodeFlag;
import com.glodblock.github.ae2netanalyser.container.ContainerAnalyser;
import com.glodblock.github.ae2netanalyser.network.AEANetworkHandler;
import com.glodblock.github.ae2netanalyser.network.packets.CAnalyserConfigSave;
import com.glodblock.github.ae2netanalyser.util.Util;
import com.glodblock.github.glodium.client.render.ColorData;
import com.glodblock.github.glodium.network.packet.CGenericPacket;
import com.glodblock.github.glodium.util.GlodUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public class GuiAnalyser extends AEBaseScreen<ContainerAnalyser> {

    private float size = 0.4f;
    private AnalyserMode mode = AnalyserMode.FULL;
    private final Reference2ObjectMap<Enum<?>, ColorData> colors = new Reference2ObjectOpenHashMap<>();
    private final Reference2ObjectMap<Enum<?>, ColorArea> colorBtns = new Reference2ObjectOpenHashMap<>();
    private final ArrayList<ClickableArea> clickables = new ArrayList<>();
    private final ColorWindow colorWindow;
    private final DraggableArea colorRed;
    private final DraggableArea colorGreen;
    private final DraggableArea colorBlue;
    private final ColorArea colorShow;

    private static final List<Enum<?>> COLOR_ORDER = List.of(
            LinkFlag.NORMAL, LinkFlag.DENSE, LinkFlag.COMPRESSED,
            NodeFlag.NORMAL, NodeFlag.DENSE, NodeFlag.MISSING
            );

    public GuiAnalyser(ContainerAnalyser menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        this.colors.putAll(ItemNetworkAnalyser.defaultColors);
        this.clickables.add(new ClickableArea(39, 21, 6, 11, this, () -> this.changeMode(-1)));
        this.clickables.add(new ClickableArea(107, 21, 6, 11, this, () -> this.changeMode(1)));
        this.clickables.add(new ClickableArea(39, 49, 6, 11, this, () -> this.changeSize(-0.1f)));
        this.clickables.add(new ClickableArea(107, 49, 6, 11, this, () -> this.changeSize(0.1f)));
        this.clickables.add(new ClickableArea(146, 142, 65, 14, this, this::loadDefault));
        for (int i = 0; i < COLOR_ORDER.size(); i ++) {
            var mode = COLOR_ORDER.get(i);
            var btn = new ColorArea(198, 22 + i * 21, 25, 9, this, () -> this.beginColorConfig(mode));
            this.clickables.add(btn);
            this.colorBtns.put(mode, btn);
        }
        this.colorWindow = new ColorWindow(73, 48, 110, 80, this);
        this.colorWindow.addElement(this.colorRed = new DraggableArea(73 + 8, 48 + 9, 90, 7, this));
        this.colorWindow.addElement(this.colorGreen = new DraggableArea(73 + 8, 48 + 24, 90, 7, this));
        this.colorWindow.addElement(this.colorBlue = new DraggableArea(73 + 8, 48 + 39, 90, 7, this));
        this.colorWindow.addElement(this.colorShow = new ColorArea(73 + 41, 48 + 58, 27, 7, this, () -> {}));
        this.colorWindow.addElement(new ClickableArea(73 + 17, 48 + 55, 13, 13, this, () -> this.closeColorConfig(true)));
        this.colorWindow.addElement(new ClickableArea(73 + 79, 48 + 55, 13, 13, this, () -> this.closeColorConfig(false)));
        AEANetworkHandler.INSTANCE.sendToServer(new CGenericPacket("update"));
    }

    public void loadConfig(ItemNetworkAnalyser.AnalyserConfig config) {
        this.mode = config.mode();
        this.size = config.nodeSize();
        this.colors.clear();
        this.colors.putAll(config.colors());
        for (var entry : this.colors.entrySet()) {
            var btn = this.colorBtns.get(entry.getKey());
            btn.setColor(entry.getValue());
        }
    }

    public void loadDefault() {
        this.colors.clear();
        this.colors.putAll(ItemNetworkAnalyser.defaultColors);
        for (var entry : this.colors.entrySet()) {
            var btn = this.colorBtns.get(entry.getKey());
            btn.setColor(entry.getValue());
        }
    }

    public void closeColorConfig(boolean save) {
        if (save) {
            var colorBtn = this.colorBtns.get(this.colorWindow.configType);
            var newData = this.colorShow.getColor();
            colorBtn.setColor(newData);
            this.colors.put(this.colorWindow.configType, newData);
        }
        this.colorWindow.isOn = false;
    }

    private void beginColorConfig(Enum<?> type) {
        this.colorWindow.isOn = true;
        this.colorWindow.configType = type;
        var color = this.colorBtns.get(type).getColor();
        this.colorRed.setValue(color.getRf());
        this.colorGreen.setValue(color.getGf());
        this.colorBlue.setValue(color.getBf());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        RenderSystem.disableDepthTest();
        for (var c : this.clickables) {
            if (c instanceof DrawableArea d) {
                d.draw(guiGraphics);
            }
        }
        if (this.colorWindow.isOn) {
            this.colorShow.setColor(new ColorData(0.8f, this.colorRed.getValue(), this.colorGreen.getValue(), this.colorBlue.getValue()));
            this.colorWindow.draw(guiGraphics);
        }
        RenderSystem.enableDepthTest();
    }

    @Override
    public boolean mouseClicked(double xCoord, double yCoord, int btn) {
        if (this.colorWindow.isOn) {
            return this.colorWindow.click(xCoord, yCoord);
        }
        for (var c : this.clickables) {
            if (c.click(xCoord, yCoord)) {
                return true;
            }
        }
        return super.mouseClicked(xCoord, yCoord, btn);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.colorWindow.isOn) {
            this.colorWindow.release(mouseX, mouseY);
            return true;
        }
        this.clickables.forEach(c -> c.release(mouseX, mouseY));
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
        if (this.colorWindow.isOn) {
            this.colorWindow.drag(mouseX, mouseY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, mouseButton, dragX, dragY);
    }

    @Override
    public void drawFG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY) {
        drawCenteredText(guiGraphics, this.mode.getTranslatedName(), 76, 27, 0xFFFFFFFF);
        drawCenteredText(guiGraphics, Component.translatable("gui.ae2netanalyser.network_analyser.mode"), 24, 26, 0xFFFFFFFF);
        drawCenteredText(guiGraphics, String.valueOf((int) (this.size * 10)), 76, 55, 0xFFFFFFFF);
        drawCenteredText(guiGraphics, Component.translatable("gui.ae2netanalyser.network_analyser.node_size"), 24, 54, 0xFFFFFFFF);
        drawCenteredText(guiGraphics, Component.translatable("gui.ae2netanalyser.network_analyser.reset"), 179, 149, 0xFFFFFFFF);
        for (int i = 0; i < COLOR_ORDER.size(); i ++) {
            var m = COLOR_ORDER.get(i);
            if (m.getClass() == NodeFlag.class) {
                guiGraphics.drawString(
                        this.font,
                        Component.translatable("gui.ae2netanalyser.network_analyser.NODE." + m.name()),
                        134, 23 + 21 * i,
                        0xFFFFFFFF,
                        false
                        );
            }
            if (m.getClass() == LinkFlag.class) {
                guiGraphics.drawString(
                        this.font,
                        Component.translatable("gui.ae2netanalyser.network_analyser.LINK." + m.name()),
                        134, 23 + 21 * i,
                        0xFFFFFFFF,
                        false
                );
            }
        }
        guiGraphics.drawString(
                this.font,
                Component.translatable("gui.ae2netanalyser.network_analyser.channel." + Util.getChannelMode().name()),
                16, 72,
                0xFFFFFFFF,
                false
        );
        guiGraphics.drawString(
                this.font,
                Component.translatable("gui.ae2netanalyser.network_analyser.state.normal_nodes", this.countNode(NodeFlag.NORMAL)),
                16, 86,
                0xFF00FF00,
                false
        );
        guiGraphics.drawString(
                this.font,
                Component.translatable("gui.ae2netanalyser.network_analyser.state.dense_nodes", this.countNode(NodeFlag.DENSE)),
                16, 100,
                0xFF00FFFF,
                false
        );
        guiGraphics.drawString(
                this.font,
                Component.translatable("gui.ae2netanalyser.network_analyser.state.missing_nodes", this.countNode(NodeFlag.MISSING)),
                16, 114,
                0xFFFF0000,
                false
        );
    }

    private int countNode(NodeFlag type) {
        var data = NetworkDataHandler.pullData();
        if (data != null) {
            return data.countNode(type);
        }
        return 0;
    }

    private void drawCenteredText(GuiGraphics guiGraphics, String text, int centerX, int centerY, int color) {
        this.drawCenteredText(guiGraphics, Component.literal(text), centerX, centerY, color);
    }

    private void drawCenteredText(GuiGraphics guiGraphics, Component text, int centerX, int centerY, int color) {
        int width = this.font.width(text);
        int height = this.font.lineHeight;
        guiGraphics.drawString(this.font, text, centerX - width / 2, centerY - height / 2, color, false);
    }

    private void changeMode(int offset) {
        this.mode = AnalyserMode.byIndex((this.mode.ordinal() + offset + AnalyserMode.values().length) % AnalyserMode.values().length);
    }

    private void changeSize(float offset) {
        this.size += offset;
        this.size = (float) GlodUtil.clamp(this.size, 0.1, 0.9);
    }

    @Override
    public void onClose() {
        AEANetworkHandler.INSTANCE.sendToServer(new CAnalyserConfigSave(new ItemNetworkAnalyser.AnalyserConfig(this.mode, this.size, this.colors)));
        super.onClose();
    }

    private static class ColorWindow extends ClickableArea {

        final List<ClickableArea> elements = new ArrayList<>();
        Enum<?> configType;
        boolean isOn = false;

        public ColorWindow(int x, int y, int width, int height, AEBaseScreen<?> parent) {
            super(x, y, width, height, parent, () -> {});
        }

        public void draw(GuiGraphics guiGraphics) {
            if (this.isOn) {
                Blitters.COLOR_SUB_MENU.dest(this.x + this.screen.getGuiLeft(), this.y + this.screen.getGuiTop()).blit(guiGraphics);
                for (var c : this.elements) {
                    if (c instanceof DrawableArea d) {
                        d.draw(guiGraphics);
                    }
                }
            }
        }

        public void addElement(ClickableArea c) {
            this.elements.add(c);
        }

        @Override
        public boolean click(double x, double y) {
            if (isMouseOver(x, y)) {
                for (var c : this.elements) {
                    if (c.click(x, y)) {
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public void release(double x, double y) {
            for (var c : this.elements) {
                c.release(x, y);
            }
        }

        public void drag(double x, double y) {
            for (var c : this.elements) {
                if (c instanceof DraggableArea d) {
                    d.drag(x, y);
                }
            }
        }

    }

}
