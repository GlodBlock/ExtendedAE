package com.github.glodblock.eterminal.client;

import appeng.api.util.AEColor;
import appeng.client.render.StaticItemColor;
import appeng.init.client.InitScreens;
import com.github.glodblock.eterminal.client.gui.GuiExPatternTerminal;
import com.github.glodblock.eterminal.client.render.HighlightRender;
import com.github.glodblock.eterminal.common.ETerminalItemAndBlock;
import com.github.glodblock.eterminal.container.ContainerExPatternTerminal;
import com.github.glodblock.eterminal.util.Ae2ReflectClient;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientRegistryHandler {

    public static final ClientRegistryHandler INSTANCE = new ClientRegistryHandler();

    public void init() {
        this.registerSemantic();
        this.registerGui();
    }

    public void registerSemantic() {
    }

    public void registerGui() {
        InitScreens.register(ContainerExPatternTerminal.TYPE, GuiExPatternTerminal::new, "/screens/ex_pattern_access_terminal.json");
    }

    @SubscribeEvent
    public void registerColorHandler(RegisterColorHandlersEvent.Item event) {
        var color = event.getItemColors();
        color.register(new StaticItemColor(AEColor.TRANSPARENT), ETerminalItemAndBlock.EX_PATTERN_TERMINAL);
    }

    public void registerHighLightRender(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            HighlightRender.INSTANCE.tick(event.getPoseStack(), Ae2ReflectClient.getRenderBuffers(event.getLevelRenderer()).outlineBufferSource(), event.getCamera());
        }
    }

}
