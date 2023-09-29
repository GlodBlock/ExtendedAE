package com.github.glodblock.eae.proxy;

import appeng.api.AEApi;
import com.github.glodblock.eae.common.EAEItemAndBlock;
import com.github.glodblock.eae.handler.InfinityCellHandler;
import com.github.glodblock.eae.register.ServerRegister;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {

    public final ServerRegister regHandler = createRegistryHandler();

    public ServerRegister createRegistryHandler() {
        return new ServerRegister();
    }

    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this.regHandler);
        EAEItemAndBlock.init(this.regHandler);
    }

    public void init(FMLInitializationEvent event) {
        this.regHandler.onInit();
    }

    public void postInit(FMLPostInitializationEvent event) {
        AEApi.instance().registries().cell().addCellHandler(new InfinityCellHandler());
    }

}
