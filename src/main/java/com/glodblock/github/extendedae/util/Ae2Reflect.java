package com.glodblock.github.extendedae.util;

import appeng.api.behaviors.StackTransferContext;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGrid;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.storage.IStorageService;
import appeng.api.stacks.AEKey;
import appeng.api.storage.cells.CellState;
import appeng.api.storage.cells.StorageCell;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.storage.DriveBlockEntity;
import appeng.blockentity.storage.IOPortBlockEntity;
import appeng.crafting.pattern.AECraftingPattern;
import appeng.helpers.InterfaceLogic;
import appeng.helpers.patternprovider.PatternContainer;
import appeng.menu.implementations.PatternAccessTermMenu;
import appeng.parts.AEBasePart;
import appeng.parts.automation.AbstractLevelEmitterPart;
import appeng.parts.automation.ExportBusPart;
import appeng.parts.automation.IOBusPart;
import appeng.util.ConfigInventory;
import appeng.util.inv.AppEngInternalInventory;
import com.glodblock.github.glodium.reflect.FieldAccessor;
import com.glodblock.github.glodium.reflect.MethodAccessor;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

public class Ae2Reflect {

    private static final FieldAccessor fContainerTracker_serverId;
    private static final FieldAccessor fContainerTracker_container;
    private static final FieldAccessor fContainerTracker_server;
    private static final FieldAccessor fDriveBlockEntity_clientSideCellState;
    private static final FieldAccessor fDriveBlockEntity_clientSideCellItems;
    private static final FieldAccessor fDriveBlockEntity_clientSideOnline;
    private static final FieldAccessor fAbstractLevelEmitterPart_prevState;
    private static final FieldAccessor fAEBaseBlockEntity_customName;
    private static final FieldAccessor fAEBasePart_customName;
    private static final FieldAccessor fAECraftingPattern_recipeHolder;
    private static final FieldAccessor fAECraftingPattern_output;
    private static final FieldAccessor fIOPortBlockEntity_inputCells;
    private static final FieldAccessor fIOPortBlockEntity_upgrades;
    private static final FieldAccessor fInterfaceLogic_config;
    private static final FieldAccessor fInterfaceLogic_storage;
    private static final FieldAccessor fPatternAccessTermMenu_byId;
    private static final MethodAccessor mDriveBlockEntity_updateClientSideState;
    private static final MethodAccessor mAECraftingPattern_getCompressedIndexFromSparse;
    private static final MethodAccessor mIOBusPart_updateState;
    private static final MethodAccessor mExportBusPart_createTransferContext;
    private static final MethodAccessor mIOPortBlockEntity_transferContents;
    private static final MethodAccessor mIOPortBlockEntity_moveSlot;
    private static final MethodAccessor mInterfaceLogic_onConfigRowChanged;
    private static final MethodAccessor mInterfaceLogic_isAllowedInStorageSlot;
    private static final MethodAccessor mInterfaceLogic_onStorageChanged;
    private static final MethodAccessor mPatternAccessTermMenu_isVisible;

    static {
        fContainerTracker_serverId = FieldAccessor.of("appeng.menu.implementations.PatternAccessTermMenu$ContainerTracker", "serverId");
        fContainerTracker_container = FieldAccessor.of("appeng.menu.implementations.PatternAccessTermMenu$ContainerTracker", "container");
        fContainerTracker_server = FieldAccessor.of("appeng.menu.implementations.PatternAccessTermMenu$ContainerTracker", "server");
        fDriveBlockEntity_clientSideCellState = FieldAccessor.of(DriveBlockEntity.class, "clientSideCellState");
        fDriveBlockEntity_clientSideCellItems = FieldAccessor.of(DriveBlockEntity.class, "clientSideCellItems");
        fDriveBlockEntity_clientSideOnline = FieldAccessor.of(DriveBlockEntity.class, "clientSideOnline");
        fAbstractLevelEmitterPart_prevState = FieldAccessor.of(AbstractLevelEmitterPart.class, "prevState");
        fAEBaseBlockEntity_customName = FieldAccessor.of(AEBaseBlockEntity.class, "customName");
        fAEBasePart_customName = FieldAccessor.of(AEBasePart.class, "customName");
        fAECraftingPattern_recipeHolder = FieldAccessor.of(AECraftingPattern.class, "recipeHolder");
        fAECraftingPattern_output = FieldAccessor.of(AECraftingPattern.class, "output");
        fIOPortBlockEntity_inputCells = FieldAccessor.of(IOPortBlockEntity.class, "inputCells");
        fIOPortBlockEntity_upgrades = FieldAccessor.of(IOPortBlockEntity.class, "upgrades");
        fInterfaceLogic_config = FieldAccessor.of(InterfaceLogic.class, "config");
        fInterfaceLogic_storage = FieldAccessor.of(InterfaceLogic.class, "storage");
        fPatternAccessTermMenu_byId = FieldAccessor.of(PatternAccessTermMenu.class, "byId");
        mDriveBlockEntity_updateClientSideState = MethodAccessor.of(DriveBlockEntity.class, "updateClientSideState");
        mAECraftingPattern_getCompressedIndexFromSparse = MethodAccessor.of(AECraftingPattern.class, "getCompressedIndexFromSparse", int.class);
        mIOBusPart_updateState = MethodAccessor.of(IOBusPart.class, "updateState");
        mExportBusPart_createTransferContext = MethodAccessor.of(ExportBusPart.class, "createTransferContext", IStorageService.class, IEnergyService.class);
        mIOPortBlockEntity_transferContents = MethodAccessor.of(IOPortBlockEntity.class, "transferContents", IGrid.class, StorageCell.class, long.class);
        mIOPortBlockEntity_moveSlot = MethodAccessor.of(IOPortBlockEntity.class, "moveSlot", int.class);
        mInterfaceLogic_onConfigRowChanged = MethodAccessor.of(InterfaceLogic.class, "onConfigRowChanged");
        mInterfaceLogic_isAllowedInStorageSlot = MethodAccessor.of(InterfaceLogic.class, "isAllowedInStorageSlot", int.class, AEKey.class);
        mInterfaceLogic_onStorageChanged = MethodAccessor.of(InterfaceLogic.class, "onStorageChanged");
        mPatternAccessTermMenu_isVisible = MethodAccessor.of(PatternAccessTermMenu.class, "isVisible", PatternContainer.class);
    }

    public static long getContainerID(Object owner) {
        return fContainerTracker_serverId.get(owner);
    }

    public static PatternContainer getContainer(Object owner) {
        return fContainerTracker_container.get(owner);
    }

    public static InternalInventory getServerInventory(Object owner) {
        return fContainerTracker_server.get(owner);
    }

    public static Long2ObjectOpenHashMap<Object> getIDMap(Object owner) {
        return fPatternAccessTermMenu_byId.get(owner);
    }

    public static boolean checkVisibility(PatternAccessTermMenu owner, PatternContainer container) {
        return mPatternAccessTermMenu_isVisible.get(owner, container);
    }

    public static void updateDriveClientSideState(DriveBlockEntity owner) {
        mDriveBlockEntity_updateClientSideState.execute(owner);
    }

    public static CellState[] getCellState(DriveBlockEntity owner) {
        return fDriveBlockEntity_clientSideCellState.get(owner);
    }

    public static boolean getClientOnline(DriveBlockEntity owner) {
        return fDriveBlockEntity_clientSideOnline.get(owner);
    }

    public static void setClientOnline(DriveBlockEntity owner, boolean val) {
        fDriveBlockEntity_clientSideOnline.set(owner, val);
    }

    public static Item[] getCellItem(DriveBlockEntity owner) {
        return fDriveBlockEntity_clientSideCellItems.get(owner);
    }

    public static int getCompressIndex(AECraftingPattern owner, int id) {
        return mAECraftingPattern_getCompressedIndexFromSparse.get(owner, id);
    }

    public static boolean getPrevState(AbstractLevelEmitterPart owner) {
        return fAbstractLevelEmitterPart_prevState.get(owner);
    }

    public static void setCustomName(Object owner, Component name) {
        if (owner instanceof AEBaseBlockEntity) {
            fAEBaseBlockEntity_customName.set(owner, name);
        } else if (owner instanceof AEBasePart) {
            fAEBasePart_customName.set(owner, name);
        }
    }

    public static void updatePartState(IOBusPart owner) {
        mIOBusPart_updateState.execute(owner);
    }

    @SuppressWarnings("UnstableApiUsage")
    public static StackTransferContext getExportContext(ExportBusPart owner, IStorageService storageService, IEnergyService energyService) {
        return mExportBusPart_createTransferContext.get(owner, storageService, energyService);
    }

    public static RecipeHolder<?> getCraftRecipe(AECraftingPattern owner) {
        return fAECraftingPattern_recipeHolder.get(owner);
    }

    public static ItemStack getCraftRecipeResult(AECraftingPattern owner) {
        return fAECraftingPattern_output.get(owner);
    }

    public static AppEngInternalInventory getInputCellInv(IOPortBlockEntity owner) {
        return fIOPortBlockEntity_inputCells.get(owner);
    }

    public static void setIOPortUpgrade(IOPortBlockEntity owner, IUpgradeInventory val) {
        fIOPortBlockEntity_upgrades.set(owner, val);
    }

    public static long transferItemsFromCell(IOPortBlockEntity owner, IGrid grid, StorageCell cellInv, long itemsToMove) {
        return mIOPortBlockEntity_transferContents.get(owner, grid, cellInv, itemsToMove);
    }

    public static boolean moveSlotInCell(IOPortBlockEntity owner, int x) {
        return mIOPortBlockEntity_moveSlot.get(owner, x);
    }

    public static void setInterfaceStorage(InterfaceLogic owner, ConfigInventory storage) {
        fInterfaceLogic_storage.set(owner, storage);
    }

    public static void setInterfaceConfig(InterfaceLogic owner, ConfigInventory config) {
        fInterfaceLogic_config.set(owner, config);
    }

    public static void onInterfaceConfigChange(InterfaceLogic owner) {
        mInterfaceLogic_onConfigRowChanged.execute(owner);
    }

    public static boolean isInterfaceSlotAllowed(InterfaceLogic owner, int slot, AEKey key) {
        return mInterfaceLogic_isAllowedInStorageSlot.get(owner, slot, key);
    }

    public static void onInterfaceStorageChange(InterfaceLogic owner) {
        mInterfaceLogic_onStorageChanged.execute(owner);
    }

}
