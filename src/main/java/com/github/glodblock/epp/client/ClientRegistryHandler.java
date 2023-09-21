package com.github.glodblock.epp.client;

import appeng.api.util.AEColor;
import appeng.client.render.StaticItemColor;
import appeng.init.client.InitScreens;
import com.github.glodblock.epp.client.gui.GuiExDrive;
import com.github.glodblock.epp.client.gui.GuiExIOBus;
import com.github.glodblock.epp.client.gui.GuiExInterface;
import com.github.glodblock.epp.client.gui.GuiExPatternProvider;
import com.github.glodblock.epp.client.gui.GuiExPatternTerminal;
import com.github.glodblock.epp.client.gui.GuiIngredientBuffer;
import com.github.glodblock.epp.client.gui.GuiPatternModifier;
import com.github.glodblock.epp.client.gui.GuiWirelessConnector;
import com.github.glodblock.epp.client.gui.pattern.GuiCraftingPattern;
import com.github.glodblock.epp.client.gui.pattern.GuiProcessingPattern;
import com.github.glodblock.epp.client.gui.pattern.GuiSmithingTablePattern;
import com.github.glodblock.epp.client.gui.pattern.GuiStonecuttingPattern;
import com.github.glodblock.epp.client.hotkey.PatternHotKey;
import com.github.glodblock.epp.client.model.ExDriveModel;
import com.github.glodblock.epp.client.render.HighlightRender;
import com.github.glodblock.epp.client.render.tesr.ExDriveTESR;
import com.github.glodblock.epp.client.render.tesr.IngredientBufferTESR;
import com.github.glodblock.epp.common.EPPItemAndBlock;
import com.github.glodblock.epp.common.tileentities.TileExDrive;
import com.github.glodblock.epp.common.tileentities.TileIngredientBuffer;
import com.github.glodblock.epp.container.ContainerExDrive;
import com.github.glodblock.epp.container.ContainerExIOBus;
import com.github.glodblock.epp.container.ContainerExInterface;
import com.github.glodblock.epp.container.ContainerExPatternProvider;
import com.github.glodblock.epp.container.ContainerExPatternTerminal;
import com.github.glodblock.epp.container.ContainerIngredientBuffer;
import com.github.glodblock.epp.container.ContainerPatternModifier;
import com.github.glodblock.epp.container.ContainerWirelessConnector;
import com.github.glodblock.epp.container.pattern.ContainerCraftingPattern;
import com.github.glodblock.epp.container.pattern.ContainerProcessingPattern;
import com.github.glodblock.epp.container.pattern.ContainerSmithingTablePattern;
import com.github.glodblock.epp.container.pattern.ContainerStonecuttingPattern;
import com.github.glodblock.epp.util.Ae2ReflectClient;
import com.github.glodblock.epp.util.FCUtil;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientRegistryHandler {

    public static final ClientRegistryHandler INSTANCE = new ClientRegistryHandler();

    public void init() {
        this.registerGui();
    }

    public void registerGui() {
        InitScreens.register(ContainerExPatternProvider.TYPE, GuiExPatternProvider::new, "/screens/ex_pattern_provider.json");
        InitScreens.register(ContainerExInterface.TYPE, GuiExInterface::new, "/screens/ex_interface.json");
        InitScreens.register(ContainerExIOBus.EXPORT_TYPE, GuiExIOBus::new, "/screens/ex_export_bus.json");
        InitScreens.register(ContainerExIOBus.IMPORT_TYPE, GuiExIOBus::new, "/screens/ex_import_bus.json");
        InitScreens.register(ContainerExPatternTerminal.TYPE, GuiExPatternTerminal::new, "/screens/ex_pattern_access_terminal.json");
        InitScreens.register(ContainerWirelessConnector.TYPE, GuiWirelessConnector::new, "/screens/wireless_connector.json");
        InitScreens.register(ContainerIngredientBuffer.TYPE, GuiIngredientBuffer::new, "/screens/ingredient_buffer.json");
        InitScreens.register(ContainerExDrive.TYPE, GuiExDrive::new, "/screens/ex_drive.json");
        InitScreens.register(ContainerPatternModifier.TYPE, GuiPatternModifier::new, "/screens/pattern_modifier.json");
        MenuScreens.register(ContainerProcessingPattern.TYPE, GuiProcessingPattern::new);
        MenuScreens.register(ContainerCraftingPattern.TYPE, GuiCraftingPattern::new);
        MenuScreens.register(ContainerStonecuttingPattern.TYPE, GuiStonecuttingPattern::new);
        MenuScreens.register(ContainerSmithingTablePattern.TYPE, GuiSmithingTablePattern::new);
    }

    @SubscribeEvent
    @SuppressWarnings("deprecation")
    public void registerColorHandler(RegisterColorHandlersEvent.Item event) {
        var color = event.getItemColors();
        color.register(new StaticItemColor(AEColor.TRANSPARENT), EPPItemAndBlock.EX_PATTERN_TERMINAL);
    }

    public void registerHighLightRender(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            HighlightRender.INSTANCE.tick(event.getPoseStack(), Ae2ReflectClient.getRenderBuffers(event.getLevelRenderer()).outlineBufferSource(), event.getCamera());
        }
    }

    @SubscribeEvent
    public void registerModels(ModelEvent.RegisterGeometryLoaders event) {
        BlockEntityRenderers.register(FCUtil.getTileType(TileIngredientBuffer.class), IngredientBufferTESR::new);
        BlockEntityRenderers.register(FCUtil.getTileType(TileExDrive.class), ExDriveTESR::new);
        event.register("ex_drive", new ExDriveModel.Loader());
    }

    @SubscribeEvent
    public void registerHotKey(RegisterKeyMappingsEvent e) {
        e.register(PatternHotKey.getHotKey());
    }

}
