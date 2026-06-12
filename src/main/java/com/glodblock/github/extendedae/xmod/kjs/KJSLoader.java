package com.glodblock.github.extendedae.xmod.kjs;

import com.glodblock.github.extendedae.xmod.ModConstants;
import com.glodblock.github.glodium.registry.RegistryHandler;
import com.glodblock.github.glodium.xmod.ThirdParty;
import com.glodblock.github.glodium.xmod.XModLoader;

@ThirdParty(ModConstants.KJS)
public class KJSLoader implements XModLoader {

    @Override
    public String modid() {
        return ModConstants.KJS;
    }

    @Override
    public void loadCommon() {

    }

    @Override
    public void loadClient() {
        for (var bind : InfinityCellBuilder.MODEL_BINDINGS) {
            bind.run();
        }
    }

    @Override
    public void onRegister(RegistryHandler handler) {

    }

}
