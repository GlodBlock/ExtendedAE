package com.glodblock.github.extendedae.api;

import net.minecraft.network.chat.Component;

public enum CraftingMode {

    CRAFTING("crafting"),
    STONECUTTER("stonecutter"),
    SMITHING("smithing"),
    ANVIL("anvil");

    private final String name;

    CraftingMode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Component getDisplayName() {
        return Component.translatable("gui.expatternprovider.mode." + name);
    }

    public static CraftingMode fromOrdinal(int ordinal) {
        if (ordinal >= 0 && ordinal < values().length) {
            return values()[ordinal];
        }
        return CRAFTING;
    }
}
