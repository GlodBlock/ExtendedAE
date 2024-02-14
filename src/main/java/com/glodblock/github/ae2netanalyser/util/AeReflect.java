package com.glodblock.github.ae2netanalyser.util;

import appeng.parts.AEBasePart;
import appeng.parts.p2p.P2PTunnelPart;
import com.glodblock.github.ae2netanalyser.AEAnalyser;
import com.glodblock.github.glodium.reflect.ReflectKit;
import net.minecraft.network.chat.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class AeReflect {

    private static final Method mP2PTunnelPart_setOutput;
    private static final Field fAEBasePart_customName;

    static {
        try {
            mP2PTunnelPart_setOutput = ReflectKit.reflectMethod(P2PTunnelPart.class, "setOutput", boolean.class);
            fAEBasePart_customName = ReflectKit.reflectField(AEBasePart.class, "customName");
        } catch (NoSuchMethodException | NoSuchFieldException e) {
            AEAnalyser.LOGGER.error("Fail to initialize.");
            throw new RuntimeException(e);
        }
    }

    public static void setP2POutput(P2PTunnelPart<?> owner, boolean value) {
        ReflectKit.executeMethod(owner, mP2PTunnelPart_setOutput, value);
    }

    public static void setCustomName(AEBasePart owner, Component name) {
        ReflectKit.writeField(owner, fAEBasePart_customName, name);
    }

}
