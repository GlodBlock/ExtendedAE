package com.glodblock.github.ae2netanalyser.common;

import appeng.core.AppEng;
import appeng.items.AEBaseItem;
import com.glodblock.github.ae2netanalyser.AEAnalyser;
import com.glodblock.github.ae2netanalyser.container.ContainerAnalyser;
import com.glodblock.github.glodium.registry.RegistryHandler;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.apache.commons.lang3.tuple.Pair;

public class AEARegistryHandler extends RegistryHandler {

    public static final AEARegistryHandler INSTANCE = new AEARegistryHandler();

    public AEARegistryHandler() {
        super(AEAnalyser.MODID);
    }

    @Override
    public void runRegister() {
        super.runRegister();
        this.onRegisterContainer();
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
                .displayItems((__, o) -> {
                    for (Pair<String, Item> entry : items) {
                        if (entry.getRight() instanceof AEBaseItem aeItem) {
                            aeItem.addToMainCreativeTab(o);
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
