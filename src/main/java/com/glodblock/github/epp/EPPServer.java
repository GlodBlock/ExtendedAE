package com.glodblock.github.epp;

import com.glodblock.github.epp.common.EPPItemAndBlock;
import com.glodblock.github.epp.common.RegistryHandler;
import com.glodblock.github.epp.config.EPPConfig;
import net.fabricmc.api.ModInitializer;

public class EPPServer extends EPP implements ModInitializer {

	@Override
	public void onInitialize() {
		EPPConfig.onInit();
		EPPItemAndBlock.init(RegistryHandler.INSTANCE);
		RegistryHandler.INSTANCE.runRegister();
		RegistryHandler.INSTANCE.onInit();
		this.common();
	}

}