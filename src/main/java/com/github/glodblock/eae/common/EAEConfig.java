package com.github.glodblock.eae.common;

import com.github.glodblock.eae.EAETags;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
@Config(modid = EAETags.MOD_ID, name = EAETags.MOD_ID)
public class EAEConfig {

    @SubscribeEvent
    public static void onConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(EAETags.MOD_ID)) {
            ConfigManager.sync(EAETags.MOD_ID, Config.Type.INSTANCE);
        }
    }

    @Config.Comment({
            "ME Infinity Cell Supported Item List",
            "Format: \"<modid>:<name>:<metadata>\", where metadata is optional by default.",
            "For example: \"minecraft:cobblestone\" or \"appliedenergistics2:part:36\" (metadata)."})
    @Config.Name("Item IDs")
    @Config.RequiresMcRestart
    public static String[] infItem = new String[] {"minecraft:cobblestone"};

    @Config.Comment({
            "ME Infinity Cell Supported Fluid List",
            "Use fluid name as id by default, for example: \"water\" or \"\"."})
    @Config.Name("Fluid IDs")
    @Config.RequiresMcRestart
    public static String[] infFluid = new String[] {"water"};

}
