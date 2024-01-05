package com.github.glodblock.epp.util;

import appeng.api.storage.cells.CellState;
import appeng.blockentity.storage.DriveBlockEntity;
import appeng.crafting.pattern.AECraftingPattern;
import appeng.helpers.patternprovider.PatternContainer;
import com.glodblock.github.glodium.reflect.ReflectKit;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Ae2Reflect {

    private static final Field fContainerTracker_serverId;
    private static final Field fContainerTracker_container;
    private static final Field fUseOnContext_hitResult;
    private static final Field fDriveBlockEntity_clientSideCellState;
    private static final Field fDriveBlockEntity_clientSideCellItems;
    private static final Field fDriveBlockEntity_clientSideOnline;
    private static final Method mDriveBlockEntity_updateClientSideState;
    private static final Method mAECraftingPattern_getCompressedIndexFromSparse;

    static {
        try {
            fContainerTracker_serverId = ReflectKit.reflectField(Class.forName("appeng.menu.implementations.PatternAccessTermMenu$ContainerTracker"), "serverId");
            fContainerTracker_container = ReflectKit.reflectField(Class.forName("appeng.menu.implementations.PatternAccessTermMenu$ContainerTracker"), "container");
            fUseOnContext_hitResult = ReflectKit.reflectField(UseOnContext.class, "hitResult", "f_43705_");
            fDriveBlockEntity_clientSideCellState = ReflectKit.reflectField(DriveBlockEntity.class, "clientSideCellState");
            fDriveBlockEntity_clientSideCellItems = ReflectKit.reflectField(DriveBlockEntity.class, "clientSideCellItems");
            fDriveBlockEntity_clientSideOnline = ReflectKit.reflectField(DriveBlockEntity.class, "clientSideOnline");
            mDriveBlockEntity_updateClientSideState = ReflectKit.reflectMethod(DriveBlockEntity.class, "updateClientSideState");
            mAECraftingPattern_getCompressedIndexFromSparse = ReflectKit.reflectMethod(AECraftingPattern.class, "getCompressedIndexFromSparse", int.class);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize AE2 reflection hacks!", e);
        }
    }

    public static long getContainerID(Object owner) {
        return ReflectKit.readField(owner, fContainerTracker_serverId);
    }

    public static PatternContainer getContainer(Object owner) {
        return ReflectKit.readField(owner, fContainerTracker_container);
    }

    public static BlockHitResult getHitResult(UseOnContext owner) {
        return ReflectKit.readField(owner, fUseOnContext_hitResult);
    }

    public static void updateDriveClientSideState(DriveBlockEntity owner) {
        ReflectKit.executeMethod(owner, mDriveBlockEntity_updateClientSideState);
    }

    public static CellState[] getCellState(DriveBlockEntity owner) {
        return ReflectKit.readField(owner, fDriveBlockEntity_clientSideCellState);
    }

    public static boolean getClientOnline(DriveBlockEntity owner) {
        return ReflectKit.readField(owner, fDriveBlockEntity_clientSideOnline);
    }

    public static void setClientOnline(DriveBlockEntity owner, boolean val) {
        ReflectKit.writeField(owner, fDriveBlockEntity_clientSideOnline, val);
    }

    public static Item[] getCellItem(DriveBlockEntity owner) {
        return ReflectKit.readField(owner, fDriveBlockEntity_clientSideCellItems);
    }

    public static int getCompressIndex(AECraftingPattern owner, int id) {
        return ReflectKit.executeMethod2(owner, mAECraftingPattern_getCompressedIndexFromSparse, id);
    }

}
