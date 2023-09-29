package com.github.glodblock.eae.proxy;

import com.github.glodblock.eae.register.ClientRegister;
import com.github.glodblock.eae.register.ServerRegister;

public class ClientProxy extends CommonProxy {

    @Override
    public ServerRegister createRegistryHandler() {
        return new ClientRegister();
    }

}
