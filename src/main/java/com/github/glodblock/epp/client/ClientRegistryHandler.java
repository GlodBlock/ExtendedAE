package com.github.glodblock.epp.client;

import appeng.api.util.AEColor;
import appeng.client.render.StaticItemColor;
import appeng.init.client.InitScreens;
import appeng.menu.SlotSemantics;
import com.github.glodblock.epp.client.gui.*;
import com.github.glodblock.epp.client.render.HighlightRender;
import com.github.glodblock.epp.common.EPPItemAndBlock;
import com.github.glodblock.epp.container.*;
import com.github.glodblock.epp.util.Ae2ReflectClient;
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
        ExSemantics.EX_1 = SlotSemantics.register("EX_1", false);
        ExSemantics.EX_2 = SlotSemantics.register("EX_2", false);
        ExSemantics.EX_3 = SlotSemantics.register("EX_3", false);
        ExSemantics.EX_4 = SlotSemantics.register("EX_4", false);
    }

    public void registerGui() {
        InitScreens.register(ContainerExPatternProvider.TYPE, GuiExPatternProvider::new, "/screens/ex_pattern_provider.json");
        InitScreens.register(ContainerExInterface.TYPE, GuiExInterface::new, "/screens/ex_interface.json");
        InitScreens.register(ContainerExIOBus.EXPORT_TYPE, GuiExIOBus::new, "/screens/ex_export_bus.json");
        InitScreens.register(ContainerExIOBus.IMPORT_TYPE, GuiExIOBus::new, "/screens/ex_import_bus.json");
        InitScreens.register(ContainerExPatternTerminal.TYPE, GuiExPatternTerminal::new, "/screens/ex_pattern_access_terminal.json");
        InitScreens.register(ContainerWirelessConnector.TYPE, GuiWirelessConnector::new, "/screens/wireless_connector.json");
    }

    @SubscribeEvent
    public void registerColorHandler(RegisterColorHandlersEvent.Item event) {
        var color = event.getItemColors();
        color.register(new StaticItemColor(AEColor.TRANSPARENT), EPPItemAndBlock.EX_PATTERN_TERMINAL);
    }

    public void registerHighLightRender(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            HighlightRender.INSTANCE.tick(event.getPoseStack(), Ae2ReflectClient.getRenderBuffers(event.getLevelRenderer()).outlineBufferSource(), event.getCamera());
        }
    }

}
