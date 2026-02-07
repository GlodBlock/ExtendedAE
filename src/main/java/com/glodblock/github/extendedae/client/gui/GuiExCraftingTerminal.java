package com.glodblock.github.extendedae.client.gui;

import appeng.client.gui.me.common.MEStorageScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.TabButton;
import appeng.core.AEConfig;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.InventoryActionPacket;
import appeng.helpers.InventoryAction;
import com.glodblock.github.extendedae.api.CraftingMode;
import com.glodblock.github.extendedae.client.gui.widget.OutputResultSlot;
import com.glodblock.github.extendedae.client.gui.widget.panel.AnvilPanel;
import com.glodblock.github.extendedae.client.gui.widget.panel.CraftingPanel;
import com.glodblock.github.extendedae.client.gui.widget.panel.ExPanel;
import com.glodblock.github.extendedae.client.gui.widget.panel.SmithingPanel;
import com.glodblock.github.extendedae.client.gui.widget.panel.StonecutterPanel;
import com.glodblock.github.extendedae.container.ContainerExCraftingTerminal;
import com.glodblock.github.extendedae.network.EPPNetworkHandler;
import com.glodblock.github.glodium.network.packet.CGenericPacket;
import com.glodblock.github.glodium.network.packet.sync.IActionHolder;
import com.glodblock.github.glodium.network.packet.sync.Paras;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class GuiExCraftingTerminal extends MEStorageScreen<ContainerExCraftingTerminal> implements IActionHolder {

    private final Map<CraftingMode, ExPanel> modePanels = new EnumMap<>(CraftingMode.class);
    private final Map<CraftingMode, TabButton> modeTabButtons = new EnumMap<>(CraftingMode.class);
    private final Map<String, Consumer<Paras>> actions = createHolder();

    public GuiExCraftingTerminal(ContainerExCraftingTerminal menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        for (var mode : CraftingMode.values()) {
            var panel = switch (mode) {
                case CRAFTING -> new CraftingPanel(this, this.widgets);
                case STONECUTTER -> new StonecutterPanel(this, this.widgets);
                case SMITHING -> new SmithingPanel(this, this.widgets);
                case ANVIL -> new AnvilPanel(this, this.widgets);
            };
            var tabButton = new TabButton(panel.getTabIconItem(), panel.getTabTooltip(), btn -> EPPNetworkHandler.INSTANCE.sendToServer(new CGenericPacket("set_mode", mode.ordinal())));
            tabButton.setStyle(TabButton.Style.HORIZONTAL);
            var modeIndex = modeTabButtons.size();
            this.widgets.add("modePanel" + modeIndex, panel);
            this.widgets.add("modeTabButton" + modeIndex, tabButton);
            this.modeTabButtons.put(mode, tabButton);
            this.modePanels.put(mode, panel);
        }
    }

    @Override
    public @NotNull List<Component> getTooltipFromContainerItem(@NotNull ItemStack stack) {
        return super.getTooltipFromContainerItem(stack);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        for (var mode : CraftingMode.values()) {
            var selected = this.menu.getCurrentMode() == mode;
            this.modeTabButtons.get(mode).setSelected(selected);
            this.modePanels.get(mode).setVisible(selected);
        }
    }

    @Override
    public void onClose() {
        if (AEConfig.instance().isClearGridOnClose()) {
            this.getMenu().clearAllGrid();
        }
        super.onClose();
    }

    @Override
    protected void slotClicked(@Nullable Slot slot, int slotIdx, int mouseButton, ClickType clickType) {
        if (slot instanceof OutputResultSlot) {
            InventoryAction action;
            if (hasShiftDown()) {
                action = InventoryAction.CRAFT_SHIFT;
            } else if (InputConstants.isKeyDown(getMinecraft().getWindow().getWindow(), GLFW.GLFW_KEY_SPACE)) {
                action = InventoryAction.CRAFT_ALL;
            } else {
                action = mouseButton == 1 ? InventoryAction.CRAFT_STACK : InventoryAction.CRAFT_ITEM;
            }
            final InventoryActionPacket p = new InventoryActionPacket(action, slotIdx, 0);
            NetworkHandler.instance().sendToServer(p);
            return;
        }
        super.slotClicked(slot, slotIdx, mouseButton, clickType);
    }

    @Override
    public @NotNull Map<String, Consumer<Paras>> getActionMap() {
        return this.actions;
    }

}
