package com.glodblock.github.extendedae.xmod;

import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.AEKeyTypes;
import net.minecraft.resources.Identifier;

public class ExternalTypes {

    public static AEKeyType GAS;
    public static AEKeyType MANA;
    public static AEKeyType FLUX;
    public static AEKeyType SOURCE;

    static {
        try {
            GAS = AEKeyTypes.get(Identifier.parse("appmek:chemical"));
        } catch (Exception e) {
            GAS = null;
        }
        try {
            MANA = AEKeyTypes.get(Identifier.parse("appbot:mana"));
        } catch (Exception e) {
            MANA = null;
        }
        try {
            FLUX = AEKeyTypes.get(Identifier.parse("appflux:flux"));
        } catch (Exception e) {
            FLUX = null;
        }
        try {
            SOURCE = AEKeyTypes.get(Identifier.parse("arseng:source"));
        } catch (Exception e) {
            SOURCE = null;
        }
    }

}
