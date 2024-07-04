package com.glodblock.github.extendedae.common.parts;

import appeng.api.config.Settings;
import appeng.api.config.ShowPatternProviders;
import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.api.storage.ILinkStatus;
import appeng.api.storage.IPatternAccessTermMenuHost;
import appeng.api.util.IConfigManager;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import appeng.parts.PartModel;
import appeng.parts.reporting.AbstractDisplayPart;
import appeng.util.ConfigManager;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.container.ContainerExPatternTerminal;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.List;

public class PartExPatternAccessTerminal extends AbstractDisplayPart implements IPatternAccessTermMenuHost {

    public static List<ResourceLocation> MODELS = Arrays.asList(
            ResourceLocation.fromNamespaceAndPath(ExtendedAE.MODID, "part/ex_pattern_access_terminal_off"),
            ResourceLocation.fromNamespaceAndPath(ExtendedAE.MODID, "part/ex_pattern_access_terminal_on")
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
    public boolean onUseWithoutItem(Player player, Vec3 pos) {
        if (!super.onUseWithoutItem(player, pos) && !isClientSide()) {
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
    public void writeToNBT(CompoundTag tag, HolderLookup.Provider registries) {
        super.writeToNBT(tag, registries);
        configManager.writeToNBT(tag, registries);
    }

    @Override
    public void readFromNBT(CompoundTag tag, HolderLookup.Provider registries) {
        super.readFromNBT(tag, registries);
        configManager.readFromNBT(tag, registries);
    }

    @Override
    public ILinkStatus getLinkStatus() {
        return ILinkStatus.ofManagedNode(getMainNode());
    }

}
