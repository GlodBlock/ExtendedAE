package com.github.glodblock.extendedae.util;

import appeng.api.behaviors.StackTransferContext;
import appeng.api.networking.IGrid;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.storage.IStorageService;
import appeng.api.storage.cells.CellState;
import appeng.api.storage.cells.StorageCell;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.storage.DriveBlockEntity;
import appeng.blockentity.storage.IOPortBlockEntity;
import appeng.crafting.pattern.AECraftingPattern;
import appeng.helpers.patternprovider.PatternContainer;
import appeng.parts.AEBasePart;
import appeng.parts.automation.AbstractLevelEmitterPart;
import appeng.parts.automation.ExportBusPart;
import appeng.parts.automation.IOBusPart;
import appeng.util.inv.AppEngInternalInventory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingRecipe;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class Ae2Reflect {

    private static final Field fContainerTracker_serverId;
    private static final Field fContainerTracker_container;
    private static final Field fDriveBlockEntity_clientSideCellState;
    private static final Field fDriveBlockEntity_clientSideCellItems;
    private static final Field fDriveBlockEntity_clientSideOnline;
    private static final Field fAbstractLevelEmitterPart_prevState;
    private static final Field fAEBaseBlockEntity_customName;
    private static final Field fAEBasePart_customName;
    private static final Field fIOPortBlockEntity_inputCells;
    private static final Field fIOPortBlockEntity_upgrades;
    private static final Field fAECraftingPattern_recipe;
    private static final Method mDriveBlockEntity_updateClientSideState;
    private static final Method mAECraftingPattern_getCompressedIndexFromSparse;
    private static final Method mIOPortBlockEntity_transferContents;
    private static final Method mIOPortBlockEntity_moveSlot;
    private static final Method mIOBusPart_updateState;
    private static final Method mExportBusPart_createTransferContext;

    static {
        try {
            fContainerTracker_serverId = reflectField(Class.forName("appeng.menu.implementations.PatternAccessTermMenu$ContainerTracker"), "serverId");
            fContainerTracker_container = reflectField(Class.forName("appeng.menu.implementations.PatternAccessTermMenu$ContainerTracker"), "container");
            fDriveBlockEntity_clientSideCellState = reflectField(DriveBlockEntity.class, "clientSideCellState");
            fDriveBlockEntity_clientSideCellItems = reflectField(DriveBlockEntity.class, "clientSideCellItems");
            fDriveBlockEntity_clientSideOnline = reflectField(DriveBlockEntity.class, "clientSideOnline");
            fAbstractLevelEmitterPart_prevState = reflectField(AbstractLevelEmitterPart.class, "prevState");
            fAEBaseBlockEntity_customName = reflectField(AEBaseBlockEntity.class, "customName");
            fAEBasePart_customName = reflectField(AEBasePart.class, "customName");
            fIOPortBlockEntity_inputCells = reflectField(IOPortBlockEntity.class, "inputCells");
            fIOPortBlockEntity_upgrades = reflectField(IOPortBlockEntity.class, "upgrades");
            fAECraftingPattern_recipe = reflectField(AECraftingPattern.class, "recipe");
            mDriveBlockEntity_updateClientSideState = reflectMethod(DriveBlockEntity.class, "updateClientSideState");
            mAECraftingPattern_getCompressedIndexFromSparse = reflectMethod(AECraftingPattern.class, "getCompressedIndexFromSparse", int.class);
            mIOPortBlockEntity_transferContents = reflectMethod(IOPortBlockEntity.class, "transferContents", IGrid.class, StorageCell.class, long.class);
            mIOPortBlockEntity_moveSlot = reflectMethod(IOPortBlockEntity.class, "moveSlot", int.class);
            mIOBusPart_updateState = reflectMethod(IOBusPart.class, "updateState");
            mExportBusPart_createTransferContext = reflectMethod(ExportBusPart.class, "createTransferContext", IStorageService.class, IEnergyService.class);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize AE2 reflection hacks!", e);
        }
    }

    public static Method reflectMethod(Class<?> owner, String name, Class<?>... paramTypes) throws NoSuchMethodException {
        return reflectMethod(owner, new String[]{name}, paramTypes);
    }

    @SuppressWarnings("all")
    public static Method reflectMethod(Class<?> owner, String[] names, Class<?>... paramTypes) throws NoSuchMethodException {
        Method m = null;
        for (String name : names) {
            try {
                m = owner.getDeclaredMethod(name, paramTypes);
                if (m != null) break;
            }
            catch (NoSuchMethodException ignore) {
            }
        }
        if (m == null) throw new NoSuchMethodException("Can't find field from " + Arrays.toString(names));
        m.setAccessible(true);
        return m;
    }

    @SuppressWarnings("all")
    public static Field reflectField(Class<?> owner, String ...names) throws NoSuchFieldException {
        Field f = null;
        for (String name : names) {
            try {
                f = owner.getDeclaredField(name);
                if (f != null) break;
            }
            catch (NoSuchFieldException ignore) {
            }
        }
        if (f == null) throw new NoSuchFieldException("Can't find field from " + Arrays.toString(names));
        f.setAccessible(true);
        return f;
    }

    @SuppressWarnings("unchecked")
    public static <T> T readField(Object owner, Field field) {
        try {
            return (T)field.get(owner);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to read field: " + field);
        }
    }

    public static void writeField(Object owner, Field field, Object value) {
        try {
            field.set(owner, value);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to write field: " + field);
        }
    }

    public static void executeMethod(Object owner, Method method, Object ... args) {
        try {
            method.invoke(owner, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Failed to execute method: " + method);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T executeMethod2(Object owner, Method method, Object ... args) {
        try {
            return (T) method.invoke(owner, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Failed to execute method: " + method);
        }
    }

    public static long getContainerID(Object owner) {
        return readField(owner, fContainerTracker_serverId);
    }

    public static PatternContainer getContainer(Object owner) {
        return readField(owner, fContainerTracker_container);
    }

    public static void updateDriveClientSideState(DriveBlockEntity owner) {
        executeMethod(owner, mDriveBlockEntity_updateClientSideState);
    }

    public static CellState[] getCellState(DriveBlockEntity owner) {
        return Ae2Reflect.readField(owner, fDriveBlockEntity_clientSideCellState);
    }

    public static boolean getClientOnline(DriveBlockEntity owner) {
        return Ae2Reflect.readField(owner, fDriveBlockEntity_clientSideOnline);
    }

    public static void setClientOnline(DriveBlockEntity owner, boolean val) {
        Ae2Reflect.writeField(owner, fDriveBlockEntity_clientSideOnline, val);
    }

    public static Item[] getCellItem(DriveBlockEntity owner) {
        return Ae2Reflect.readField(owner, fDriveBlockEntity_clientSideCellItems);
    }

    public static int getCompressIndex(AECraftingPattern owner, int id) {
        return Ae2Reflect.executeMethod2(owner, mAECraftingPattern_getCompressedIndexFromSparse, id);
    }

    public static boolean getPrevState(AbstractLevelEmitterPart owner) {
        return Ae2Reflect.readField(owner, fAbstractLevelEmitterPart_prevState);
    }

    public static void setCustomName(Object owner, Component name) {
        if (owner instanceof AEBaseBlockEntity) {
            Ae2Reflect.writeField(owner, fAEBaseBlockEntity_customName, name);
        } else if (owner instanceof AEBasePart) {
            Ae2Reflect.writeField(owner, fAEBasePart_customName, name);
        }
    }

    public static AppEngInternalInventory getInputCellInv(IOPortBlockEntity owner) {
        return readField(owner, fIOPortBlockEntity_inputCells);
    }

    public static void setIOPortUpgrade(IOPortBlockEntity owner, IUpgradeInventory val) {
        writeField(owner, fIOPortBlockEntity_upgrades, val);
    }

    public static long transferItemsFromCell(IOPortBlockEntity owner, IGrid grid, StorageCell cellInv, long itemsToMove) {
        return executeMethod2(owner, mIOPortBlockEntity_transferContents, grid, cellInv, itemsToMove);
    }

    public static boolean moveSlotInCell(IOPortBlockEntity owner, int x) {
        return executeMethod2(owner, mIOPortBlockEntity_moveSlot, x);
    }

    public static void updatePartState(IOBusPart owner) {
        executeMethod(owner, mIOBusPart_updateState);
    }

    @SuppressWarnings("UnstableApiUsage")
    public static StackTransferContext getExportContext(ExportBusPart owner, IStorageService storageService, IEnergyService energyService) {
        return executeMethod2(owner, mExportBusPart_createTransferContext, storageService, energyService);
    }

    public static CraftingRecipe getCraftRecipe(AECraftingPattern owner) {
        return readField(owner, fAECraftingPattern_recipe);
    }

}
