package com.glodblock.github.extendedae.xmod.appliede.containers;

import appeng.api.stacks.AEItemKey;
import appeng.menu.SlotSemantic;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.implementations.UpgradeableMenu;
import appeng.menu.slot.AppEngSlot;
import appeng.menu.slot.FakeSlot;
import com.glodblock.github.extendedae.api.IPage;
import com.glodblock.github.extendedae.client.ExSemantics;
import gripe._90.appliede.me.misc.EMCInterfaceLogicHost;
import gripe._90.appliede.menu.EMCSetStockAmountMenu;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;

import java.util.ArrayList;
import java.util.List;

public class ContainerExEMCInterface extends UpgradeableMenu<EMCInterfaceLogicHost> implements IPage {

    public static final String ACTION_OPEN_SET_AMOUNT = "setAmount";

    public static final MenuType<ContainerExEMCInterface> TYPE = MenuTypeBuilder
            .create(ContainerExEMCInterface::new, EMCInterfaceLogicHost.class)
            .build("ex_emc_interface");

    private static final int PAGE = 18;
    private static final int LINE = 9;
    private static final SlotSemantic[] CONFIG_PATTERN = new SlotSemantic[] {
            ExSemantics.EX_1, ExSemantics.EX_3, ExSemantics.EX_5, ExSemantics.EX_7
    };
    private static final SlotSemantic[] STORAGE_PATTERN = new SlotSemantic[] {
            ExSemantics.EX_2, ExSemantics.EX_4, ExSemantics.EX_6, ExSemantics.EX_8
    };
    private final List<Slot> configSlots = new ArrayList<>();
    @GuiSync(7)
    public int page;

    public ContainerExEMCInterface(int id, Inventory ip, EMCInterfaceLogicHost host) {
        super(TYPE, id, ip, host);
        registerClientAction(ACTION_OPEN_SET_AMOUNT, Integer.class, this::openSetAmountMenu);
        var logic = host.getInterfaceLogic();
        var config = logic.getConfig().createMenuWrapper();
        for (int x = 0; x < config.size(); x++) {
            int page = x / PAGE;
            int row = (x - page * PAGE) / LINE;
            this.configSlots.add(this.addSlot(new FakeSlot(config, x), CONFIG_PATTERN[2 * page + row]));
        }
        var storage = logic.getStorage().createMenuWrapper();
        for (int x = 0; x < storage.size(); x++) {
            int page = x / PAGE;
            int row = (x - page * PAGE) / LINE;
            this.addSlot(new AppEngSlot(storage, x), STORAGE_PATTERN[2 * page + row]);
        }
    }

    public List<Slot> getConfigSlots() {
        return this.configSlots;
    }

    public void showPage(int page) {
        for (int index = 0; index < 4; index ++) {
            var slots = this.getSlots(CONFIG_PATTERN[index]);
            slots.addAll(this.getSlots(STORAGE_PATTERN[index]));
            for (var slot : slots) {
                if (slot instanceof AppEngSlot as) {
                    as.setActive(page == (index / 2));
                }
            }
        }
    }

    public void openSetAmountMenu(int configSlot) {
        if (isClientSide()) {
            sendClientAction(ACTION_OPEN_SET_AMOUNT, configSlot);
        } else {
            var stack = getHost().getConfig().getStack(configSlot);
            if (stack != null && stack.what() instanceof AEItemKey item) {
                EMCSetStockAmountMenu.open((ServerPlayer) getPlayer(), getLocator(), configSlot, item, (int) stack.amount());
            }
        }
    }

    @Override
    public void broadcastChanges() {
        if (this.getHost() instanceof IPage pg) {
            this.page = pg.getPage();
        }
        super.broadcastChanges();
    }

    @Override
    public void setPage(int page) {
        this.page = page;
        if (this.getHost() instanceof IPage pg) {
            pg.setPage(page);
        }
    }

    @Override
    public int getPage() {
        return this.page;
    }
}
