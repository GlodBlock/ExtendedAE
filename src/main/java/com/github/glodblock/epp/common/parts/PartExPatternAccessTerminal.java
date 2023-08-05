package com.github.glodblock.epp.common.parts;

import appeng.api.config.Settings;
import appeng.api.config.ShowPatternProviders;
import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.api.util.IConfigManager;
import appeng.api.util.IConfigurableObject;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import appeng.parts.PartModel;
import appeng.parts.reporting.AbstractDisplayPart;
import appeng.util.ConfigManager;
import com.github.glodblock.epp.EPP;
import com.github.glodblock.epp.container.ContainerExPatternTerminal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.List;

public class PartExPatternAccessTerminal extends AbstractDisplayPart implements IConfigurableObject {

    public static List<ResourceLocation> MODELS = Arrays.asList(
            new ResourceLocation(EPP.MODID, "part/ex_pattern_access_terminal_off"),
            new ResourceLocation(EPP.MODID, "part/ex_pattern_access_terminal_on")
    );

    public static final IPartModel MODELS_OFF = new PartModel(MODEL_BASE, MODELS.get(0), MODEL_STATUS_OFF);
    public static final IPartModel MODELS_ON = new PartModel(MODEL_BASE, MODELS.get(1), MODEL_STATUS_ON);
    public static final IPartModel MODELS_HAS_CHANNEL = new PartModel(MODEL_BASE, MODELS.get(1), MODEL_STATUS_HAS_CHANNEL);

    private final ConfigManager configManager = new ConfigManager(() -> this.getHost().markForSave());

    public PartExPatternAccessTerminal(IPartItem<?> partItem) {
        super(partItem, true);
        this.configManager.registerSetting(Settings.TERMINAL_SHOW_PATTERN_PROVIDERS, ShowPatternProviders.VISIBLE);
    }

    @Override
    public boolean onPartActivate(Player player, InteractionHand hand, Vec3 pos) {
        if (!super.onPartActivate(player, hand, pos) && !isClientSide()) {
            MenuOpener.open(ContainerExPatternTerminal.TYPE, player, MenuLocators.forPart(this));
        }
        return true;
    }

    @Override
    public IPartModel getStaticModels() {
        return this.selectModel(MODELS_OFF, MODELS_ON, MODELS_HAS_CHANNEL);
    }

    @Override
    public IConfigManager getConfigManager() {
        return configManager;
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        configManager.writeToNBT(tag);
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        configManager.readFromNBT(tag);
    }

}
