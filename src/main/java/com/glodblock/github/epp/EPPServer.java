package com.glodblock.github.epp;

import com.glodblock.github.epp.common.EPPItemAndBlock;
import com.glodblock.github.epp.common.RegistryHandler;
import net.fabricmc.api.ModInitializer;

public class EPPServer extends EPP implements ModInitializer {

	@Override
	public void onInitialize() {
		EPPItemAndBlock.init(RegistryHandler.INSTANCE);
		RegistryHandler.INSTANCE.runRegister();
		RegistryHandler.INSTANCE.onInit();
	}

}