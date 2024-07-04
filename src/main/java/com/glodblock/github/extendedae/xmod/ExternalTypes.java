package com.glodblock.github.extendedae.xmod;

import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.AEKeyTypes;
import net.minecraft.resources.ResourceLocation;

public class ExternalTypes {

    public static AEKeyType GAS;
    public static AEKeyType MANA;
    public static AEKeyType FLUX;
    public static AEKeyType SOURCE;

    static {
        try {
            GAS = AEKeyTypes.get(ResourceLocation.parse("appmek:chemical"));
        } catch (Exception e) {
            GAS = null;
        }
        try {
            MANA = AEKeyTypes.get(ResourceLocation.parse("appbot:mana"));
        } catch (Exception e) {
            MANA = null;
        }
        try {
            FLUX = AEKeyTypes.get(ResourceLocation.parse("appflux:flux"));
        } catch (Exception e) {
            FLUX = null;
        }
        try {
            SOURCE = AEKeyTypes.get(ResourceLocation.parse("arseng:source"));
        } catch (Exception e) {
            SOURCE = null;
        }
    }

}
