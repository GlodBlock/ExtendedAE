package com.glodblock.github.ae2netanalyser.container;

import appeng.menu.AEBaseMenu;
import appeng.menu.implementations.MenuTypeBuilder;
import com.glodblock.github.ae2netanalyser.AEAnalyser;
import com.glodblock.github.ae2netanalyser.common.AEASingletons;
import com.glodblock.github.ae2netanalyser.common.inventory.DummyItemInventory;
import com.glodblock.github.ae2netanalyser.common.items.ItemNetworkAnalyzer;
import com.glodblock.github.glodium.network.packet.sync.ActionMap;
import com.glodblock.github.glodium.network.packet.sync.IActionHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.NotNull;

public class ContainerAnalyser extends AEBaseMenu implements IActionHolder {

    private final ActionMap actions = ActionMap.create();
    public ItemNetworkAnalyzer.AnalyserConfig initConfig = ItemNetworkAnalyzer.defaultConfig;

    public static final MenuType<@NotNull ContainerAnalyser> TYPE = MenuTypeBuilder
            .create(ContainerAnalyser::new, DummyItemInventory.class)
            .withInitialData(
                    (host, buffer) -> host.getItemStack().getOrDefault(AEASingletons.ANALYZER_CONFIG, ItemNetworkAnalyzer.defaultConfig).writeToBytes(buffer),
                    (b, container, buffer) -> container.initConfig = ItemNetworkAnalyzer.AnalyserConfig.readFromBytes(buffer)
            )
            .buildUnregistered(AEAnalyser.id("network_analyser"));

    public ContainerAnalyser(int id, Inventory playerInventory, DummyItemInventory host) {
        super(TYPE, id, playerInventory, host);
    }

    public void saveConfig(ItemNetworkAnalyzer.AnalyserConfig config) {
        @SuppressWarnings("DataFlowIssue") var stack = this.itemMenuHost.getItemStack();
        if (stack.getItem() == AEASingletons.ANALYSER) {
            stack.set(AEASingletons.ANALYZER_CONFIG, config);
        }
    }

    @NotNull
    @Override
    public ActionMap getActionMap() {
        return this.actions;
    }
}
