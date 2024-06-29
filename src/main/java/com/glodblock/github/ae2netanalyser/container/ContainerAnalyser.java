package com.glodblock.github.ae2netanalyser.container;

import appeng.menu.AEBaseMenu;
import appeng.menu.implementations.MenuTypeBuilder;
import com.glodblock.github.ae2netanalyser.common.AEAComponents;
import com.glodblock.github.ae2netanalyser.common.AEAItems;
import com.glodblock.github.ae2netanalyser.common.inventory.DummyItemInventory;
import com.glodblock.github.ae2netanalyser.common.items.ItemNetworkAnalyzer;
import com.glodblock.github.ae2netanalyser.network.AEANetworkHandler;
import com.glodblock.github.ae2netanalyser.network.packets.SAnalyserConfigInit;
import com.glodblock.github.glodium.network.packet.sync.ActionMap;
import com.glodblock.github.glodium.network.packet.sync.IActionHolder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.NotNull;

public class ContainerAnalyser extends AEBaseMenu implements IActionHolder {

    private final ActionMap actions = ActionMap.create();

    public static final MenuType<ContainerAnalyser> TYPE = MenuTypeBuilder
            .create(ContainerAnalyser::new, DummyItemInventory.class)
            .build("network_analyser");

    public ContainerAnalyser(int id, Inventory playerInventory, DummyItemInventory host) {
        super(TYPE, id, playerInventory, host);
        this.actions.put("update", o -> {
            if (this.getPlayer() instanceof ServerPlayer sp) {
                AEANetworkHandler.INSTANCE.sendTo(new SAnalyserConfigInit(host.getItemStack().getOrDefault(AEAComponents.ANALYZER_CONFIG, ItemNetworkAnalyzer.defaultConfig)), sp);
            }
        });
    }

    public void saveConfig(ItemNetworkAnalyzer.AnalyserConfig config) {
        @SuppressWarnings("DataFlowIssue") var stack = this.itemMenuHost.getItemStack();
        if (stack.getItem() == AEAItems.ANALYSER) {
            stack.set(AEAComponents.ANALYZER_CONFIG, config);
        }
    }

    @NotNull
    @Override
    public ActionMap getActionMap() {
        return this.actions;
    }
}
