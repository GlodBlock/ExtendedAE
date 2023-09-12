package com.github.glodblock.epp.client;

import appeng.client.render.model.AutoRotatingBakedModel;
import appeng.init.client.InitScreens;
import appeng.menu.SlotSemantics;
import com.github.glodblock.epp.EPP;
import com.github.glodblock.epp.client.gui.GuiExDrive;
import com.github.glodblock.epp.client.gui.GuiExIOBus;
import com.github.glodblock.epp.client.gui.GuiExInterface;
import com.github.glodblock.epp.client.gui.GuiExPatternProvider;
import com.github.glodblock.epp.client.model.AERotatableBlocks;
import com.github.glodblock.epp.client.model.ExDriveModel;
import com.github.glodblock.epp.client.render.tesr.ExDriveTESR;
import com.github.glodblock.epp.common.EPPItemAndBlock;
import com.github.glodblock.epp.common.tileentities.TileExDrive;
import com.github.glodblock.epp.container.ContainerExDrive;
import com.github.glodblock.epp.container.ContainerExIOBus;
import com.github.glodblock.epp.container.ContainerExInterface;
import com.github.glodblock.epp.container.ContainerExPatternProvider;
import com.github.glodblock.epp.util.FCUtil;
import com.google.common.collect.Sets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Map;
import java.util.Set;

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
        InitScreens.register(ContainerExDrive.TYPE, GuiExDrive::new, "/screens/ex_drive.json");
    }

    @SubscribeEvent
    public void registerModels(ModelEvent.RegisterGeometryLoaders event) {
        BlockEntityRenderers.register(FCUtil.getTileType(TileExDrive.class), ExDriveTESR::new);
        event.register("ex_drive", new ExDriveModel.Loader());
    }

    public void registerRotatableBlock(ModelEvent.BakingCompleted event) {
        Map<ResourceLocation, BakedModel> modelRegistry = event.getModels();
        Set<ResourceLocation> keys = Sets.newHashSet(modelRegistry.keySet());
        BakedModel missingModel = modelRegistry.get(ModelBakery.MISSING_MODEL_LOCATION);
        for (ResourceLocation location : keys) {
            if (location.getNamespace().equals(EPP.MODID)) {
                if (AERotatableBlocks.check(location)) {
                    BakedModel orgModel = modelRegistry.get(location);
                    if (orgModel == missingModel) {
                        continue;
                    }
                    BakedModel newModel = new AutoRotatingBakedModel(orgModel);
                    if (newModel != orgModel) {
                        modelRegistry.put(location, newModel);
                    }
                }
            }
        }
    }

}
