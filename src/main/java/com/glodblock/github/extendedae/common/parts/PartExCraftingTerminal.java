package com.glodblock.github.extendedae.common.parts;

import appeng.api.inventories.InternalInventory;
import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.parts.PartModel;
import appeng.parts.reporting.AbstractTerminalPart;
import appeng.util.inv.AppEngInternalInventory;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.api.CraftingMode;
import com.glodblock.github.extendedae.container.ContainerExCraftingTerminal;
import com.glodblock.github.extendedae.util.FCUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.List;

public class PartExCraftingTerminal extends AbstractTerminalPart {

    public static List<ResourceLocation> MODELS = Arrays.asList(
            new ResourceLocation(ExtendedAE.MODID, "part/ex_crafting_terminal_off"),
            new ResourceLocation(ExtendedAE.MODID, "part/ex_crafting_terminal_on")
    );

    public static final IPartModel MODELS_OFF = new PartModel(MODEL_BASE, MODELS.get(0), MODEL_STATUS_OFF);
    public static final IPartModel MODELS_ON = new PartModel(MODEL_BASE, MODELS.get(1), MODEL_STATUS_ON);
    public static final IPartModel MODELS_HAS_CHANNEL = new PartModel(MODEL_BASE, MODELS.get(1), MODEL_STATUS_HAS_CHANNEL);
    public static final ResourceLocation INV_CRAFTING = ExtendedAE.id("craft");
    public static final ResourceLocation INV_STONECUTTING = ExtendedAE.id("stonecutting");
    public static final ResourceLocation INV_SMITHING = ExtendedAE.id("smithing");
    public static final ResourceLocation INV_ANVIL = ExtendedAE.id("anvil");

    private CraftingMode currentMode = CraftingMode.CRAFTING;
    private final AppEngInternalInventory craftingGrid = new AppEngInternalInventory(this, 9);
    private final AppEngInternalInventory stonecuttingGrid = new AppEngInternalInventory(this, 1);
    private final AppEngInternalInventory smithingGrid = new AppEngInternalInventory(this, 3);
    private final AppEngInternalInventory anvilGrid = new AppEngInternalInventory(this, 2);

    public PartExCraftingTerminal(IPartItem<?> partItem) {
        super(partItem);
    }

    @Override
    public IPartModel getStaticModels() {
        return this.selectModel(MODELS_OFF, MODELS_ON, MODELS_HAS_CHANNEL);
    }

    @Override
    public void addAdditionalDrops(List<ItemStack> drops, boolean wrenched) {
        super.addAdditionalDrops(drops, wrenched);
        FCUtil.addDrops(this.craftingGrid, drops);
        FCUtil.addDrops(this.stonecuttingGrid, drops);
        FCUtil.addDrops(this.smithingGrid, drops);
        FCUtil.addDrops(this.anvilGrid, drops);
    }

    @Override
    public void clearContent() {
        super.clearContent();
        this.craftingGrid.clear();
        this.stonecuttingGrid.clear();
        this.smithingGrid.clear();
        this.anvilGrid.clear();
    }

    public CraftingMode getCurrentMode() {
        return this.currentMode;
    }

    public void setCurrentMode(CraftingMode mode) {
        this.currentMode = mode;
        this.getHost().markForSave();
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        tag.putInt("craftingMode", currentMode.ordinal());
        this.craftingGrid.writeToNBT(tag, "craftingGrid");
        this.stonecuttingGrid.writeToNBT(tag, "stonecuttingGrid");
        this.smithingGrid.writeToNBT(tag, "smithingGrid");
        this.anvilGrid.writeToNBT(tag, "anvilGrid");
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        this.currentMode = CraftingMode.fromOrdinal(tag.getInt("craftingMode"));
        this.craftingGrid.readFromNBT(tag, "craftingGrid");
        this.stonecuttingGrid.readFromNBT(tag, "stonecuttingGrid");
        this.smithingGrid.readFromNBT(tag, "smithingGrid");
        this.anvilGrid.readFromNBT(tag, "anvilGrid");
    }

    @Override
    public MenuType<?> getMenuType(Player p) {
        return ContainerExCraftingTerminal.TYPE;
    }

    @Override
    public InternalInventory getSubInventory(ResourceLocation id) {
        if (id.equals(INV_CRAFTING)) {
            return this.craftingGrid;
        }
        if (id.equals(INV_STONECUTTING)) {
            return this.stonecuttingGrid;
        }
        if (id.equals(INV_SMITHING)) {
            return this.smithingGrid;
        }
        if (id.equals(INV_ANVIL)) {
            return this.anvilGrid;
        }
        return super.getSubInventory(id);
    }

}
