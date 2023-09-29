package com.github.glodblock.eae.common;

import com.github.glodblock.eae.ExtendedAE;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
@Config(modid = ExtendedAE.MODID, name = "extendedae")
public class EAEConfig {

    @SubscribeEvent
    public static void onConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(ExtendedAE.MODID)) {
            ConfigManager.sync(ExtendedAE.MODID, Config.Type.INSTANCE);
        }
    }

    @Config.Comment("ME Infinity Cell type (Item's ID)")
    @Config.Name("Item IDs")
    public static String[] infItem = new String[] {"minecraft:cobblestone"};

    @Config.Comment("ME Infinity Cell type (Fluid's ID)")
    @Config.Name("Fluid IDs")
    public static String[] infFluid = new String[] {"water"};

}
