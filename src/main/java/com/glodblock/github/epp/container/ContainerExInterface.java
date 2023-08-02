package com.glodblock.github.epp.container;

import appeng.api.config.Settings;
import appeng.api.util.IConfigManager;
import appeng.helpers.InterfaceLogicHost;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.implementations.SetStockAmountMenu;
import appeng.menu.implementations.UpgradeableMenu;
import appeng.menu.slot.AppEngSlot;
import appeng.menu.slot.FakeSlot;
import com.glodblock.github.epp.client.ExSemantics;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;

public class ContainerExInterface extends UpgradeableMenu<InterfaceLogicHost> {

    public static final String ACTION_OPEN_SET_AMOUNT = "setAmount";

    public static final ScreenHandlerType<ContainerExInterface> TYPE = MenuTypeBuilder
            .create(ContainerExInterface::new, InterfaceLogicHost.class)
            .build("ex_interface");

    public ContainerExInterface(int id, PlayerInventory ip, InterfaceLogicHost host) {
        super(TYPE, id, ip, host);
        registerClientAction(ACTION_OPEN_SET_AMOUNT, Integer.class, this::openSetAmountMenu);
        var logic = host.getInterfaceLogic();
        var config = logic.getConfig().createMenuWrapper();
        for (int x = 0; x < config.size(); x++) {
            this.addSlot(new FakeSlot(config, x), x < 9 ? ExSemantics.EX_1 : ExSemantics.EX_3);
        }
        var storage = logic.getStorage().createMenuWrapper();
        for (int x = 0; x < storage.size(); x++) {
            this.addSlot(new AppEngSlot(storage, x), x < 9 ? ExSemantics.EX_2 : ExSemantics.EX_4);
        }
    }

    @Override
    protected void loadSettingsFromHost(IConfigManager cm) {
        this.setFuzzyMode(cm.getSetting(Settings.FUZZY_MODE));
    }

    public void openSetAmountMenu(int configSlot) {
        if (isClientSide()) {
            sendClientAction(ACTION_OPEN_SET_AMOUNT, configSlot);
        } else {
            var stack = getHost().getConfig().getStack(configSlot);
            if (stack != null) {
                SetStockAmountMenu.open((ServerPlayerEntity) getPlayer(), getLocator(), configSlot,
                        stack.what(), (int) stack.amount());
            }
        }
    }
}