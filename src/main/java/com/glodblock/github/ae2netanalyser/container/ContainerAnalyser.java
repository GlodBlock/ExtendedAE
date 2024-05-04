package com.glodblock.github.ae2netanalyser.container;

import appeng.menu.AEBaseMenu;
import appeng.menu.implementations.MenuTypeBuilder;
import com.glodblock.github.ae2netanalyser.common.AEAItems;
import com.glodblock.github.ae2netanalyser.common.inventory.DummyItemInventory;
import com.glodblock.github.ae2netanalyser.common.items.ItemNetworkAnalyzer;
import com.glodblock.github.ae2netanalyser.network.AEANetworkHandler;
import com.glodblock.github.ae2netanalyser.network.packets.SAnalyserConfigInit;
import com.glodblock.github.glodium.network.packet.sync.IActionHolder;
import com.glodblock.github.glodium.network.packet.sync.Paras;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Consumer;

public class ContainerAnalyser extends AEBaseMenu implements IActionHolder {

    private final Map<String, Consumer<Paras>> actions = createHolder();

    public static final MenuType<ContainerAnalyser> TYPE = MenuTypeBuilder
            .create(ContainerAnalyser::new, DummyItemInventory.class)
            .build("network_analyser");

    public ContainerAnalyser(int id, Inventory playerInventory, DummyItemInventory host) {
        super(TYPE, id, playerInventory, host);
        this.actions.put("update", o -> {
            if (this.getPlayer() instanceof ServerPlayer sp) {
                AEANetworkHandler.INSTANCE.sendTo(new SAnalyserConfigInit(AEAItems.ANALYSER.getConfig(host.getItemStack())), sp);
            }
        });
    }

    public void saveConfig(ItemNetworkAnalyzer.AnalyserConfig config) {
        var stack = this.itemMenuHost.getItemStack();
        if (stack.getItem() == AEAItems.ANALYSER) {
            AEAItems.ANALYSER.saveConfig(config, stack);
        }
    }

    @NotNull
    @Override
    public Map<String, Consumer<Paras>> getActionMap() {
        return this.actions;
    }
}
