package com.glodblock.github.extendedae.container;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.storage.IPatternAccessTermMenuHost;
import appeng.blockentity.crafting.IMolecularAssemblerSupportedPattern;
import appeng.core.definitions.AEBlocks;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.implementations.PatternAccessTermMenu;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.util.Ae2Reflect;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ContainerExPatternTerminal extends PatternAccessTermMenu {

    public static final MenuType<@NotNull ContainerExPatternTerminal> TYPE = MenuTypeBuilder
            .create(ContainerExPatternTerminal::new, IPatternAccessTermMenuHost.class)
            .buildUnregistered(ExtendedAE.id("ex_pattern_access_terminal"));

    public ContainerExPatternTerminal(int id, Inventory ip, IPatternAccessTermMenuHost host) {
        super(TYPE, id, ip, host, true);
    }

    public ContainerExPatternTerminal(MenuType<?> type, int id, Inventory ip, IPatternAccessTermMenuHost host, boolean bindInventory) {
        super(type, id, ip, host, bindInventory);
    }

    @Override
    public void quickMovePattern(ServerPlayer player, int clickedSlot, List<Long> allowedPatternContainers) {
        if (clickedSlot < 0 || clickedSlot >= this.slots.size()) {
            return;
        }
        Slot sourceSlot = getSlot(clickedSlot);
        if (!isPlayerSideSlot(sourceSlot)) {
            return;
        }
        ItemStack sourceStack = sourceSlot.getItem();
        if (sourceStack.getCount() != 1) {
            return;
        }
        var pattern = PatternDetailsHelper.decodePattern(sourceStack, player.level());
        if (pattern == null) {
            return;
        }
        boolean molecularAssemblerPattern = pattern instanceof IMolecularAssemblerSupportedPattern;
        // Collect possible targets
        List<Object> targets = new ArrayList<>();
        for (var id : allowedPatternContainers) {
            var inv = Ae2Reflect.getIDMap(this).get(id.longValue());
            if (inv == null) {
                continue;
            }
            var container = Ae2Reflect.getContainer(inv);
            // Check pattern container exists and is visible
            if (Ae2Reflect.checkVisibility(this, container)) {
                var icon = container.getTerminalGroup().icon();
                boolean molecularAssembler = icon != null && (icon.is(AEBlocks.MOLECULAR_ASSEMBLER.asItem()) || icon.is(EAESingletons.ASSEMBLER_MATRIX_PATTERN) || icon.is(EAESingletons.EX_ASSEMBLER));
                if (molecularAssemblerPattern == molecularAssembler) {
                    targets.add(inv);
                }
            }
        }
        // Try to insert in each container until we succeed
        for (var target : targets) {
            var targetContainer = Ae2Reflect.getServerInventory(target);
            if (targetContainer.addItems(sourceStack).isEmpty()) {
                sourceSlot.set(ItemStack.EMPTY);
                return;
            }
        }
    }

}
