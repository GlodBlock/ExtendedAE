package com.glodblock.github.extendedae.common.parts;

import appeng.api.config.Settings;
import appeng.api.config.ShowPatternProviders;
import appeng.api.parts.IPartItem;
import appeng.api.storage.ILinkStatus;
import appeng.api.storage.IPatternAccessTermMenuHost;
import appeng.api.util.IConfigManager;
import appeng.blockentity.AEModelData;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import appeng.parts.reporting.AbstractDisplayPart;
import appeng.util.ConfigManager;
import com.glodblock.github.extendedae.container.ContainerExPatternTerminal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.model.data.ModelData;

public class PartExPatternAccessTerminal extends AbstractDisplayPart implements IPatternAccessTermMenuHost {

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
    public void collectModelData(ModelData.Builder builder) {
        super.collectModelData(builder);
        builder.with(AEModelData.SPIN, getSpin());
    }

    @Override
    public IConfigManager getConfigManager() {
        return configManager;
    }

    @Override
    public void writeToNBT(ValueOutput tag) {
        super.writeToNBT(tag);
        configManager.writeToNBT(tag);
    }

    @Override
    public void readFromNBT(ValueInput tag) {
        super.readFromNBT(tag);
        configManager.readFromNBT(tag);
    }

    @Override
    public ILinkStatus getLinkStatus() {
        return ILinkStatus.ofManagedNode(getMainNode());
    }

}
