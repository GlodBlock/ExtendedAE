package com.glodblock.github.ae2netanalyser.common;

import appeng.core.AppEng;
import appeng.items.AEBaseItem;
import com.glodblock.github.ae2netanalyser.AEAnalyser;
import com.glodblock.github.ae2netanalyser.container.ContainerAnalyser;
import com.glodblock.github.glodium.registry.RegistryHandler;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class AEARegistryHandler extends RegistryHandler {

    public static final AEARegistryHandler INSTANCE = new AEARegistryHandler();

    private final List<Pair<String, DataComponentType<?>>> components = new ArrayList<>();

    public AEARegistryHandler() {
        super(AEAnalyser.MODID);
    }

    @Override
    public void runRegister() {
        super.runRegister();
        this.registerComponents();
        this.onRegisterContainer();
    }

    private void registerComponents() {
        this.components.forEach(e -> Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, AEAnalyser.id(e.getLeft()), e.getRight()));
    }

    public void comp(String name, DataComponentType<?> component) {
        this.components.add(Pair.of(name, component));
    }

    private void onRegisterContainer() {
        Registry.register(BuiltInRegistries.MENU, AppEng.makeId("network_analyser"), ContainerAnalyser.TYPE);
    }

    public void init() {

    }

    public void registerTab(Registry<CreativeModeTab> registry) {
        var tab = CreativeModeTab.builder()
                .icon(() -> new ItemStack(AEAItems.ANALYSER))
                .title(Component.translatable("itemGroup.ae2netanalyser"))
                .displayItems((p, o) -> {
                    for (Pair<String, Item> entry : items) {
                        if (entry.getRight() instanceof AEBaseItem aeItem) {
                            aeItem.addToMainCreativeTab(p, o);
                        } else {
                            o.accept(entry.getRight());
                        }
                    }
                    for (Pair<String, Block> entry : blocks) {
                        o.accept(entry.getRight());
                    }
                })
                .build();
        Registry.register(registry, AEAnalyser.id("tab_main"), tab);
    }

}
