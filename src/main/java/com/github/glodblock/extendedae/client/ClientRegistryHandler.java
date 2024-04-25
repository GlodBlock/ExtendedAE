package com.github.glodblock.extendedae.client;

import appeng.api.util.AEColor;
import appeng.client.render.SimpleModelLoader;
import appeng.client.render.StaticItemColor;
import appeng.init.client.InitItemColors;
import appeng.init.client.InitScreens;
import com.github.glodblock.extendedae.EAE;
import com.github.glodblock.extendedae.client.gui.GuiActiveFormationPlane;
import com.github.glodblock.extendedae.client.gui.GuiExDrive;
import com.github.glodblock.extendedae.client.gui.GuiExIOBus;
import com.github.glodblock.extendedae.client.gui.GuiExInscriber;
import com.github.glodblock.extendedae.client.gui.GuiExInterface;
import com.github.glodblock.extendedae.client.gui.GuiExMolecularAssembler;
import com.github.glodblock.extendedae.client.gui.GuiExPatternProvider;
import com.github.glodblock.extendedae.client.gui.GuiExPatternTerminal;
import com.github.glodblock.extendedae.client.gui.GuiIngredientBuffer;
import com.github.glodblock.extendedae.client.gui.GuiModExportBus;
import com.github.glodblock.extendedae.client.gui.GuiModStorageBus;
import com.github.glodblock.extendedae.client.gui.GuiPatternModifier;
import com.github.glodblock.extendedae.client.gui.GuiRenamer;
import com.github.glodblock.extendedae.client.gui.GuiTagExportBus;
import com.github.glodblock.extendedae.client.gui.GuiTagStorageBus;
import com.github.glodblock.extendedae.client.gui.GuiThresholdLevelEmitter;
import com.github.glodblock.extendedae.client.gui.GuiWirelessConnector;
import com.github.glodblock.extendedae.client.gui.GuiCaner;
import com.github.glodblock.extendedae.client.gui.GuiWirelessExPAT;
import com.github.glodblock.extendedae.client.gui.pattern.GuiCraftingPattern;
import com.github.glodblock.extendedae.client.gui.pattern.GuiProcessingPattern;
import com.github.glodblock.extendedae.client.gui.pattern.GuiSmithingTablePattern;
import com.github.glodblock.extendedae.client.gui.pattern.GuiStonecuttingPattern;
import com.github.glodblock.extendedae.client.model.ExDriveModel;
import com.github.glodblock.extendedae.client.model.ExPlaneModel;
import com.github.glodblock.extendedae.client.render.tesr.CrystalFixerTESR;
import com.github.glodblock.extendedae.client.render.tesr.ExChargerTESR;
import com.github.glodblock.extendedae.client.render.tesr.ExDriveTESR;
import com.github.glodblock.extendedae.client.render.tesr.ExInscriberTESR;
import com.github.glodblock.extendedae.client.render.tesr.ExMolecularAssemblerTESR;
import com.github.glodblock.extendedae.client.render.tesr.IngredientBufferTESR;
import com.github.glodblock.extendedae.client.render.tesr.CanerTESR;
import com.github.glodblock.extendedae.common.EAEItemAndBlock;
import com.github.glodblock.extendedae.common.tileentities.TileCaner;
import com.github.glodblock.extendedae.common.tileentities.TileCrystalFixer;
import com.github.glodblock.extendedae.common.tileentities.TileExCharger;
import com.github.glodblock.extendedae.common.tileentities.TileExDrive;
import com.github.glodblock.extendedae.common.tileentities.TileExInscriber;
import com.github.glodblock.extendedae.common.tileentities.TileExMolecularAssembler;
import com.github.glodblock.extendedae.common.tileentities.TileIngredientBuffer;
import com.github.glodblock.extendedae.container.ContainerActiveFormationPlane;
import com.github.glodblock.extendedae.container.ContainerCaner;
import com.github.glodblock.extendedae.container.ContainerExDrive;
import com.github.glodblock.extendedae.container.ContainerExIOBus;
import com.github.glodblock.extendedae.container.ContainerExInscriber;
import com.github.glodblock.extendedae.container.ContainerExInterface;
import com.github.glodblock.extendedae.container.ContainerExMolecularAssembler;
import com.github.glodblock.extendedae.container.ContainerExPatternProvider;
import com.github.glodblock.extendedae.container.ContainerExPatternTerminal;
import com.github.glodblock.extendedae.container.ContainerIngredientBuffer;
import com.github.glodblock.extendedae.container.ContainerModExportBus;
import com.github.glodblock.extendedae.container.ContainerModStorageBus;
import com.github.glodblock.extendedae.container.ContainerPatternModifier;
import com.github.glodblock.extendedae.container.ContainerRenamer;
import com.github.glodblock.extendedae.container.ContainerTagExportBus;
import com.github.glodblock.extendedae.container.ContainerTagStorageBus;
import com.github.glodblock.extendedae.container.ContainerThresholdLevelEmitter;
import com.github.glodblock.extendedae.container.ContainerWirelessConnector;
import com.github.glodblock.extendedae.container.ContainerWirelessExPAT;
import com.github.glodblock.extendedae.container.pattern.ContainerCraftingPattern;
import com.github.glodblock.extendedae.container.pattern.ContainerProcessingPattern;
import com.github.glodblock.extendedae.container.pattern.ContainerSmithingTablePattern;
import com.github.glodblock.extendedae.container.pattern.ContainerStonecuttingPattern;
import com.github.glodblock.extendedae.util.FCUtil;
import com.github.glodblock.extendedae.xmod.wt.WTClientLoad;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public class ClientRegistryHandler {

    public static final ClientRegistryHandler INSTANCE = new ClientRegistryHandler();

    public void init() {
        this.registerGui();
        this.registerModels();
        this.registerColorHandler();
        this.setBlockRenderLayer();
        if (EAE.checkMod("ae2wtlib")) {
            WTClientLoad.init();
        }
    }

    private void registerGui() {
        InitScreens.register(ContainerExPatternProvider.TYPE, GuiExPatternProvider::new, "/screens/ex_pattern_provider.json");
        InitScreens.register(ContainerExInterface.TYPE, GuiExInterface::new, "/screens/ex_interface.json");
        InitScreens.register(ContainerExIOBus.EXPORT_TYPE, GuiExIOBus::new, "/screens/ex_export_bus.json");
        InitScreens.register(ContainerExIOBus.IMPORT_TYPE, GuiExIOBus::new, "/screens/ex_import_bus.json");
        InitScreens.<ContainerExPatternTerminal, GuiExPatternTerminal<ContainerExPatternTerminal>>register(ContainerExPatternTerminal.TYPE, GuiExPatternTerminal::new, "/screens/ex_pattern_access_terminal.json");
        InitScreens.register(ContainerWirelessConnector.TYPE, GuiWirelessConnector::new, "/screens/wireless_connector.json");
        InitScreens.register(ContainerIngredientBuffer.TYPE, GuiIngredientBuffer::new, "/screens/ingredient_buffer.json");
        InitScreens.register(ContainerExDrive.TYPE, GuiExDrive::new, "/screens/ex_drive.json");
        InitScreens.register(ContainerPatternModifier.TYPE, GuiPatternModifier::new, "/screens/pattern_modifier.json");
        InitScreens.register(ContainerExMolecularAssembler.TYPE, GuiExMolecularAssembler::new, "/screens/ex_molecular_assembler.json");
        InitScreens.register(ContainerExInscriber.TYPE, GuiExInscriber::new, "/screens/ex_inscriber.json");
        InitScreens.register(ContainerTagStorageBus.TYPE, GuiTagStorageBus::new, "/screens/tag_storage_bus.json");
        InitScreens.register(ContainerTagExportBus.TYPE, GuiTagExportBus::new, "/screens/tag_export_bus.json");
        InitScreens.register(ContainerThresholdLevelEmitter.TYPE, GuiThresholdLevelEmitter::new, "/screens/threshold_level_emitter.json");
        InitScreens.register(ContainerRenamer.TYPE, GuiRenamer::new, "/screens/renamer.json");
        InitScreens.register(ContainerModStorageBus.TYPE, GuiModStorageBus::new, "/screens/mod_storage_bus.json");
        InitScreens.register(ContainerModExportBus.TYPE, GuiModExportBus::new, "/screens/mod_export_bus.json");
        InitScreens.register(ContainerActiveFormationPlane.TYPE, GuiActiveFormationPlane::new, "/screens/active_formation_plane.json");
        InitScreens.register(ContainerCaner.TYPE, GuiCaner::new, "/screens/caner.json");
        InitScreens.register(ContainerWirelessExPAT.TYPE, GuiWirelessExPAT::new, "/screens/ex_pattern_access_terminal.json");
        MenuScreens.register(ContainerProcessingPattern.TYPE, GuiProcessingPattern::new);
        MenuScreens.register(ContainerCraftingPattern.TYPE, GuiCraftingPattern::new);
        MenuScreens.register(ContainerStonecuttingPattern.TYPE, GuiStonecuttingPattern::new);
        MenuScreens.register(ContainerSmithingTablePattern.TYPE, GuiSmithingTablePattern::new);
    }

    private void registerColorHandler() {
        InitItemColors.Registry color = ColorProviderRegistry.ITEM::register;
        color.register(new StaticItemColor(AEColor.TRANSPARENT), EAEItemAndBlock.EX_PATTERN_TERMINAL);
    }

    private void registerModels() {
        BlockEntityRenderers.register(FCUtil.getTileType(TileIngredientBuffer.class), IngredientBufferTESR::new);
        BlockEntityRenderers.register(FCUtil.getTileType(TileExDrive.class), ExDriveTESR::new);
        BlockEntityRenderers.register(FCUtil.getTileType(TileExMolecularAssembler.class), ExMolecularAssemblerTESR::new);
        BlockEntityRenderers.register(FCUtil.getTileType(TileExInscriber.class), ExInscriberTESR::new);
        BlockEntityRenderers.register(FCUtil.getTileType(TileExCharger.class), ExChargerTESR::new);
        BlockEntityRenderers.register(FCUtil.getTileType(TileCaner.class), CanerTESR::new);
        BlockEntityRenderers.register(FCUtil.getTileType(TileCrystalFixer.class), CrystalFixerTESR::new);
        ModelLoadingRegistry.INSTANCE.registerResourceProvider(rm -> new SimpleModelLoader<>(EAE.id("block/ex_drive"), ExDriveModel::new));
        ModelLoadingRegistry.INSTANCE.registerResourceProvider(rm -> new SimpleModelLoader<>(EAE.id("part/active_formation_plane"), () -> new ExPlaneModel(EAE.id("part/active_formation_plane"))));
        ModelLoadingRegistry.INSTANCE.registerResourceProvider(rm -> new SimpleModelLoader<>(EAE.id("part/active_formation_plane_on"), () -> new ExPlaneModel(EAE.id("part/active_formation_plane_on"))));
    }

    private void setBlockRenderLayer() {
        BlockRenderLayerMap.INSTANCE.putBlock(EAEItemAndBlock.EX_DRIVE, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(EAEItemAndBlock.EX_ASSEMBLER, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(EAEItemAndBlock.INGREDIENT_BUFFER, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(EAEItemAndBlock.FISHBIG, RenderType.cutout());
    }

}
