package com.glodblock.github.ae2netanalyser.common;

import appeng.items.AEBaseItem;
import com.glodblock.github.ae2netanalyser.AEAnalyser;
import com.glodblock.github.ae2netanalyser.container.ContainerAnalyser;
import com.glodblock.github.ae2netanalyser.container.ContainerProfiler;
import com.glodblock.github.glodium.registry.RegistryHandler;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

public class AEARegistryHandler extends RegistryHandler {

    public static AEARegistryHandler INSTANCE;

    private final DeferredRegister<@NotNull MenuType<?>> containers;

    public AEARegistryHandler(IEventBus modBus) {
        super(AEAnalyser.MODID, modBus);
        this.containers = DeferredRegister.create(BuiltInRegistries.MENU, AEAnalyser.MODID);
        this.containers.register(modBus);
        this.loadContainers();
    }

    private void loadContainers() {
        this.containers.register("network_analyser", () -> ContainerAnalyser.TYPE);
        this.containers.register("tick_analyser", () -> ContainerProfiler.TYPE);
    }

    public void init() {

    }

    public void registerTab(Registry<@NotNull CreativeModeTab> registry) {
        var tab = CreativeModeTab.builder()
                .icon(() -> new ItemStack(AEASingletons.ANALYSER.get()))
                .title(Component.translatable("itemGroup.ae2netanalyser"))
                .displayItems((p, o) -> {
                    for (var entry : this.items.getEntries()) {
                        if (entry.get() instanceof AEBaseItem aeItem) {
                            aeItem.addToMainCreativeTab(p, o);
                        } else {
                            o.accept(entry.get());
                        }
                    }
                })
                .build();
        Registry.register(registry, AEAnalyser.id("tab_main"), tab);
    }

}
