package com.github.glodblock.eae.register;

import com.github.glodblock.eae.ExtendedAE;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.tuple.Pair;

public class ClientRegister extends ServerRegister {

    @SubscribeEvent
    public void onRegisterModels(ModelRegistryEvent event) {
        for (Pair<String, Block> entry : blocks) {
            registerModel(entry.getLeft(), Item.getItemFromBlock(entry.getRight()));
        }
        for (Pair<String, Item> entry : items) {
            registerModel(entry.getLeft(), entry.getRight());
        }
    }

    private static void registerModel(String key, Item item) {
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(ExtendedAE.id(key), "inventory"));
    }

}
