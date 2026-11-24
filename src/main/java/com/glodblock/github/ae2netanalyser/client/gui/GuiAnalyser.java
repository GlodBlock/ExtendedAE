package com.glodblock.github.ae2netanalyser.client.gui;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.PaletteColor;
import appeng.client.gui.style.ScreenStyle;
import com.glodblock.github.ae2netanalyser.client.gui.buttons.ArrowButton;
import com.glodblock.github.ae2netanalyser.client.gui.buttons.ColorButton;
import com.glodblock.github.ae2netanalyser.client.gui.buttons.TextButton;
import com.glodblock.github.ae2netanalyser.client.gui.sub.GuiColorConfig;
import com.glodblock.github.ae2netanalyser.client.gui.textures.Blitters;
import com.glodblock.github.ae2netanalyser.client.render.NetworkDataHandler;
import com.glodblock.github.ae2netanalyser.common.items.ItemNetworkAnalyzer;
import com.glodblock.github.ae2netanalyser.common.me.AnalyserMode;
import com.glodblock.github.ae2netanalyser.common.me.netdata.LinkFlag;
import com.glodblock.github.ae2netanalyser.common.me.netdata.NodeFlag;
import com.glodblock.github.ae2netanalyser.container.ContainerAnalyser;
import com.glodblock.github.ae2netanalyser.network.AEANetworkHandler;
import com.glodblock.github.ae2netanalyser.network.packets.CAnalyserConfigSave;
import com.glodblock.github.ae2netanalyser.util.Util;
import com.glodblock.github.glodium.client.render.ColorData;
import com.glodblock.github.glodium.util.GlodUtil;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class GuiAnalyser extends AEBaseScreen<ContainerAnalyser> {

    private float size;
    private AnalyserMode mode;
    private final Reference2ObjectMap<Enum<?>, ColorData> colors = new Reference2ObjectOpenHashMap<>();

    private static final List<Enum<?>> COLOR_ORDER = List.of(
            LinkFlag.NORMAL, LinkFlag.DENSE, LinkFlag.COMPRESSED,
            NodeFlag.NORMAL, NodeFlag.DENSE, NodeFlag.MISSING
    );

    public GuiAnalyser(ContainerAnalyser menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        this.colors.putAll(menu.initConfig.colors());
        this.mode = menu.initConfig.mode();
        this.size = menu.initConfig.nodeSize();
        this.widgets.add("arrow_left1", new ArrowButton(ArrowButton.ArrowDirection.LEFT, () -> this.changeMode(-1), () -> true));
        this.widgets.add("arrow_right1", new ArrowButton(ArrowButton.ArrowDirection.RIGHT, () -> this.changeMode(1), () -> true));
        this.widgets.add("arrow_left2", new ArrowButton(ArrowButton.ArrowDirection.LEFT, () -> this.changeSize(-0.1f), () -> (int) (this.size * 10) > 1));
        this.widgets.add("arrow_right2", new ArrowButton(ArrowButton.ArrowDirection.RIGHT, () -> this.changeSize(0.1f), () -> (int) (this.size * 10) < 9));
        this.widgets.add("reset_btn", new TextButton(
                new TextButton.Background(Blitters.BIG_BUTTON, Blitters.BIG_BUTTON_HOVER, Blitters.BIG_BUTTON_SELECT),
                Component.translatable("gui.ae2netanalyser.network_analyser.reset"),
                this::loadDefault
        ));
        for (int i = 0; i < COLOR_ORDER.size(); i ++) {
            var mode = COLOR_ORDER.get(i);
            this.widgets.add("color" + (i + 1), new ColorButton(() -> this.beginColorConfig(mode), () -> this.colors.get(mode)));
        }
    }

    public void loadConfig(ItemNetworkAnalyzer.AnalyserConfig config) {
        this.mode = config.mode();
        this.size = config.nodeSize();
        this.colors.clear();
        this.colors.putAll(config.colors());
    }

    public void loadDefault() {
        this.loadConfig(ItemNetworkAnalyzer.defaultConfig);
    }

    private void beginColorConfig(Enum<?> type) {
        this.switchToScreen(new GuiColorConfig(
                this,
                this.colors.get(type),
                color -> this.colors.put(type, color)
        ));
    }

    @Override
    public void drawFG(GuiGraphicsExtractor guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY) {
        var textColor = this.style.getColor(PaletteColor.DEFAULT_TEXT_COLOR).toARGB();
        var fieldColor = this.style.getColor(PaletteColor.TEXTFIELD_TEXT).toARGB();
        drawCenteredText(guiGraphics, this.mode.getTranslatedName(), 76, 27, fieldColor);
        drawCenteredText(guiGraphics, Component.translatable("gui.ae2netanalyser.network_analyser.mode"), 24, 26, textColor);
        drawCenteredText(guiGraphics, String.valueOf((int) (this.size * 10)), 76, 55, fieldColor);
        drawCenteredText(guiGraphics, Component.translatable("gui.ae2netanalyser.network_analyser.node_size"), 24, 54, textColor);
        for (int i = 0; i < COLOR_ORDER.size(); i ++) {
            var m = COLOR_ORDER.get(i);
            if (m.getClass() == NodeFlag.class) {
                guiGraphics.text(
                        this.font,
                        Component.translatable("gui.ae2netanalyser.network_analyser.NODE." + m.name()),
                        134, 23 + 21 * i,
                        textColor,
                        false
                        );
            }
            if (m.getClass() == LinkFlag.class) {
                guiGraphics.text(
                        this.font,
                        Component.translatable("gui.ae2netanalyser.network_analyser.LINK." + m.name()),
                        134, 23 + 21 * i,
                        textColor,
                        false
                );
            }
        }
        guiGraphics.text(
                this.font,
                Component.translatable("gui.ae2netanalyser.network_analyser.channel." + Util.getChannelMode().name()),
                16, 72,
                textColor,
                false
        );
        guiGraphics.text(
                this.font,
                Component.translatable("gui.ae2netanalyser.network_analyser.state.normal_nodes", this.countNode(NodeFlag.NORMAL)),
                16, 86,
                0xFF32A843,
                false
        );
        guiGraphics.text(
                this.font,
                Component.translatable("gui.ae2netanalyser.network_analyser.state.dense_nodes", this.countNode(NodeFlag.DENSE)),
                16, 100,
                0xFF19B3A3,
                false
        );
        guiGraphics.text(
                this.font,
                Component.translatable("gui.ae2netanalyser.network_analyser.state.missing_nodes", this.countNode(NodeFlag.MISSING)),
                16, 114,
                0xFFAA1A1A,
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

    private void drawCenteredText(GuiGraphicsExtractor guiGraphics, String text, int centerX, int centerY, int color) {
        this.drawCenteredText(guiGraphics, Component.literal(text), centerX, centerY, color);
    }

    private void drawCenteredText(GuiGraphicsExtractor guiGraphics, Component text, int centerX, int centerY, int color) {
        int width = this.font.width(text);
        int height = this.font.lineHeight;
        guiGraphics.text(this.font, text, centerX - width / 2, centerY - height / 2, color, false);
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
        AEANetworkHandler.INSTANCE.sendToServer(new CAnalyserConfigSave(new ItemNetworkAnalyzer.AnalyserConfig(this.mode, this.size, this.colors)));
        super.onClose();
    }

}
