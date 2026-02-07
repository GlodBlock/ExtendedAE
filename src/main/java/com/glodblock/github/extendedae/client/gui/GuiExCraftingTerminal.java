package com.glodblock.github.extendedae.client.gui;

import appeng.api.config.ActionItems;
import appeng.client.gui.me.common.MEStorageScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ActionButton;
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
import com.glodblock.github.extendedae.util.CacheHolder;
import com.glodblock.github.glodium.network.packet.CGenericPacket;
import com.glodblock.github.glodium.network.packet.sync.IActionHolder;
import com.glodblock.github.glodium.network.packet.sync.Paras;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class GuiExCraftingTerminal extends MEStorageScreen<ContainerExCraftingTerminal> implements IActionHolder {

    private final Map<CraftingMode, ExPanel> modePanels = new EnumMap<>(CraftingMode.class);
    private final Map<CraftingMode, TabButton> modeTabButtons = new EnumMap<>(CraftingMode.class);
    private final Map<String, Consumer<Paras>> actions = createHolder();
    private final CacheHolder<Boolean> lackXP = CacheHolder.empty();

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
        var clearCraftingGrid = new ActionButton(ActionItems.STASH, b -> EPPNetworkHandler.INSTANCE.sendToServer(new CGenericPacket("clearCraftingGrid")));
        var clearToPlayerInv = new ActionButton(ActionItems.STASH_TO_PLAYER_INV, b -> EPPNetworkHandler.INSTANCE.sendToServer(new CGenericPacket("clearToPlayerInv")));
        this.widgets.add("clearCraftingGrid", clearCraftingGrid);
        this.widgets.add("clearToPlayerInv", clearToPlayerInv);
        this.actions.put("play_sound", o -> this.playSound(o.get(0)));
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
    protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
        if (this.menu.getCurrentMode() == CraftingMode.ANVIL) {
            if (this.hoveredSlot instanceof OutputResultSlot) {
                var output = this.hoveredSlot.getItem();
                if (!output.isEmpty() && this.getLackXP()) {
                    var itemTooltip = new ArrayList<>(this.getTooltipFromContainerItem(output));
                    itemTooltip.add(Component.translatable("gui.expatternprovider.anvil.not_enough_xp").withStyle(ChatFormatting.RED));
                    this.drawTooltip(guiGraphics, x, y, itemTooltip);
                }
            }
        }
        super.renderTooltip(guiGraphics, x, y);
    }

    private boolean getLackXP() {
        if (this.lackXP.isEmpty()) {
            this.lackXP.update(this.menu.getAndPerformAnvilCraft(this.getPlayer(), true).isEmpty());
        }
        if (((System.currentTimeMillis() / 250) & 1) != 0) {
            this.lackXP.update(this.menu.getAndPerformAnvilCraft(this.getPlayer(), true).isEmpty());
        }
        return this.lackXP.get();
    }

    private void playSound(int index) {
        var mode = CraftingMode.fromOrdinal(index);
        var sound = switch (mode) {
            case ANVIL -> SoundEvents.ANVIL_USE;
            case STONECUTTER -> SoundEvents.UI_STONECUTTER_TAKE_RESULT;
            case SMITHING -> SoundEvents.SMITHING_TABLE_USE;
            default -> null;
        };
        if (sound != null) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sound, 1.0F));
        }
    }

    @Override
    public @NotNull Map<String, Consumer<Paras>> getActionMap() {
        return this.actions;
    }

}
