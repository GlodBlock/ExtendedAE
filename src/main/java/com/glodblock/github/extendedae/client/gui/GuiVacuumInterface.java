package com.glodblock.github.extendedae.client.gui;

import appeng.api.config.FuzzyMode;
import appeng.api.config.RedstoneMode;
import appeng.api.config.Settings;
import appeng.client.gui.Icon;
import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ServerSettingToggleButton;
import appeng.client.gui.widgets.SettingToggleButton;
import appeng.core.definitions.AEItems;
import com.glodblock.github.extendedae.client.button.ActionEPPButton;
import com.glodblock.github.extendedae.client.button.CycleEPPButton;
import com.glodblock.github.extendedae.client.gui.subgui.WorkAreaConfig;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.container.ContainerVacuumInterface;
import com.glodblock.github.extendedae.network.EAENetworkHandler;
import com.glodblock.github.extendedae.network.packet.CEAEGenericPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class GuiVacuumInterface extends UpgradeableScreen<ContainerVacuumInterface> {

    private final CycleEPPButton renderBtn;
    private final CycleEPPButton speedBtn;
    private final SettingToggleButton<RedstoneMode> redstoneMode;
    private final SettingToggleButton<FuzzyMode> fuzzyMode;

    public GuiVacuumInterface(ContainerVacuumInterface menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        this.renderBtn = new CycleEPPButton();
        this.renderBtn.addActionPair(
                Icon.PATTERN_TERMINAL_VISIBLE,
                Component.translatable("gui.extendedae.vacuum_interface.render_enable"),
                b -> EAENetworkHandler.INSTANCE.sendToServer(new CEAEGenericPacket("display", true))
        );
        this.renderBtn.addActionPair(
                Icon.PATTERN_TERMINAL_ALL,
                Component.translatable("gui.extendedae.vacuum_interface.render_disable"),
                b -> EAENetworkHandler.INSTANCE.sendToServer(new CEAEGenericPacket("display", false))
        );
        this.speedBtn = new CycleEPPButton();
        this.speedBtn.addActionPair(
                Icon.SCHEDULING_DEFAULT,
                Component.translatable("gui.extendedae.vacuum_interface.normal"),
                b -> EAENetworkHandler.INSTANCE.sendToServer(new CEAEGenericPacket("urgent", true))
        );
        this.speedBtn.addActionPair(
                Icon.SCHEDULING_ROUND_ROBIN,
                Component.translatable("gui.extendedae.vacuum_interface.urgent"),
                b -> EAENetworkHandler.INSTANCE.sendToServer(new CEAEGenericPacket("urgent", false))
        );
        var config = new ActionEPPButton(b -> this.openOutputConfig(), Icon.PLACEMENT_BLOCK);
        config.setMessage(Component.translatable("gui.extendedae.vacuum_interface.set_work_area"));
        this.addToLeftToolbar(config);
        this.addToLeftToolbar(this.speedBtn);
        this.addToLeftToolbar(this.renderBtn);
        this.redstoneMode = new ServerSettingToggleButton<>(Settings.REDSTONE_CONTROLLED, RedstoneMode.IGNORE);
        this.addToLeftToolbar(this.redstoneMode);
        this.fuzzyMode = new ServerSettingToggleButton<>(Settings.FUZZY_MODE, FuzzyMode.IGNORE_ALL);
        this.addToLeftToolbar(this.fuzzyMode);
    }

    private void openOutputConfig() {
        if (this.getMenu().getHost() != null) {
            switchToScreen(new WorkAreaConfig<>(
                    this,
                    new ItemStack(EAESingletons.VACUUM_INTERFACE),
                    (delta, seq, size) -> EAENetworkHandler.INSTANCE.sendToServer(new CEAEGenericPacket("config_data", delta, seq, size)),
                    (seq, size) -> {
                        BlockPos data = size ? this.getMenu().getSize() : this.getMenu().getOffset();
                        return switch (seq) {
                            case 0 -> data.getX();
                            case 1 -> data.getY();
                            case 2 -> data.getZ();
                            default -> 0;
                        };
                    }));
        }
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        if (this.menu.displayArea) {
            this.renderBtn.setState(1);
        } else {
            this.renderBtn.setState(0);
        }
        if (this.menu.urgent) {
            this.speedBtn.setState(1);
        } else {
            this.speedBtn.setState(0);
        }
        this.redstoneMode.set(menu.getRedStoneMode());
        this.redstoneMode.setVisibility(menu.hasUpgrade(AEItems.REDSTONE_CARD));
        this.fuzzyMode.set(menu.getFuzzyMode());
        this.fuzzyMode.setVisibility(menu.hasUpgrade(AEItems.FUZZY_CARD));
    }

    public interface DataSetter {

        void set(int delta, int seq, boolean size);

    }

    public interface DataGetter {

        int get(int seq, boolean size);

    }

}
