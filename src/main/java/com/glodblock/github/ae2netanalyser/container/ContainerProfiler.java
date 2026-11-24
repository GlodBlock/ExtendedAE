package com.glodblock.github.ae2netanalyser.container;

import appeng.menu.AEBaseMenu;
import appeng.menu.implementations.MenuTypeBuilder;
import com.glodblock.github.ae2netanalyser.AEAnalyser;
import com.glodblock.github.ae2netanalyser.common.AEASingletons;
import com.glodblock.github.ae2netanalyser.common.inventory.DummyItemInventory;
import com.glodblock.github.ae2netanalyser.common.items.ItemTickAnalyzer;
import com.glodblock.github.glodium.network.packet.sync.ActionMap;
import com.glodblock.github.glodium.network.packet.sync.IActionHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.NotNull;

public class ContainerProfiler extends AEBaseMenu implements IActionHolder {

    private final ActionMap actions = ActionMap.create();
    public ItemTickAnalyzer.TickConfig initConfig = ItemTickAnalyzer.defaultConfig;

    public static final MenuType<@NotNull ContainerProfiler> TYPE = MenuTypeBuilder
            .create(ContainerProfiler::new, DummyItemInventory.class)
            .withInitialData(
                    (host, buffer) -> host.getItemStack().getOrDefault(AEASingletons.TICK_CONFIG, ItemTickAnalyzer.defaultConfig).writeToBytes(buffer),
                    (_, container, buffer) -> container.initConfig = ItemTickAnalyzer.TickConfig.readFromBytes(buffer)
            )
            .buildUnregistered(AEAnalyser.id("tick_analyser"));

    public ContainerProfiler(int id, Inventory playerInventory, DummyItemInventory host) {
        super(TYPE, id, playerInventory, host);
    }

    public void saveConfig(ItemTickAnalyzer.TickConfig config) {
        @SuppressWarnings("DataFlowIssue") var stack = this.itemMenuHost.getItemStack();
        if (stack.getItem() == AEASingletons.TICK_ANALYSER.get()) {
            stack.set(AEASingletons.TICK_CONFIG, config);
        }
    }

    @NotNull
    @Override
    public ActionMap getActionMap() {
        return this.actions;
    }
}
