package com.glodblock.github.extendedae.client.gui.widget.panel;

import appeng.client.Point;
import appeng.client.gui.Tooltip;
import appeng.client.gui.WidgetContainer;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.widgets.Scrollbar;
import appeng.menu.SlotSemantics;
import com.glodblock.github.extendedae.api.CraftingMode;
import com.glodblock.github.extendedae.client.ExSemantics;
import com.glodblock.github.extendedae.client.gui.GuiExCraftingTerminal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public class StonecutterPanel extends ExPanel {

    private static final Blitter BG = Blitter.texture("guis/ex_crafting_modes.png").src(0, 141, 126, 68);
    private static final Blitter BG_SLOT = BG.copy().src(126, 141, 16, 18);
    private static final Blitter BG_SLOT_SELECTED = BG.copy().src(126, 159, 16, 18);
    private static final Blitter BG_SLOT_HOVER = BG.copy().src(126, 177, 16, 18);
    private static final int COLS = 4;
    private static final int ROWS = 3;
    private final Scrollbar scrollbar;

    public StonecutterPanel(GuiExCraftingTerminal<?> screen, WidgetContainer widgets) {
        super(screen, widgets);
        this.scrollbar = widgets.addScrollBar("stonecuttingScrollbar", Scrollbar.SMALL);
        this.scrollbar.setRange(0, 0, COLS);
        this.scrollbar.setCaptureMouseWheel(false);
    }

    @Override
    public void updateBeforeRender() {
        // Set up the scroll bar to have a range only for the rows outside the viewport
        var totalRows = (this.menu.getStonecutterRecipes().size() + COLS - 1) / COLS;
        this.scrollbar.setRange(0, totalRows - ROWS, ROWS);
    }

    @Override
    public void drawBackgroundLayer(GuiGraphics guiGraphics, Rect2i bounds, Point mouse) {
        BG.dest(bounds.getX() + 24, bounds.getY() + bounds.getHeight() - 164).blit(guiGraphics);
        this.drawRecipes(guiGraphics, bounds, mouse);
    }

    @Override
    public boolean onMouseWheel(Point mousePos, double delta) {
        return this.scrollbar.onMouseWheel(mousePos, delta);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        this.scrollbar.setVisible(visible);
        this.screen.setSlotsHidden(SlotSemantics.STONECUTTING_INPUT, !visible);
        this.screen.setSlotsHidden(ExSemantics.EX_1, !visible);
    }

    @Override
    public boolean onMouseDown(Point mousePos, int button) {
        var recipe = this.getRecipeAt(mousePos);
        if (recipe != null) {
            this.menu.selectedStonecutterRecipe = recipe;
            this.sendPacket("stonecutter_select", recipe.toString());
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public Tooltip getTooltip(int mouseX, int mouseY) {
        var recipe = this.getRecipeAt(new Point(mouseX, mouseY));
        if (recipe != null) {
            var recipeOpt = this.getLevel().getRecipeManager().byKey(recipe);
            if (recipeOpt.isPresent()) {
                var lines = this.screen.getTooltipFromContainerItem(recipeOpt.get().getResultItem(this.getRegistryAccess()));
                return new Tooltip(lines);
            }
        }
        return null;
    }

    private ResourceLocation getRecipeAt(Point point) {
        var recipes = this.menu.getStonecutterRecipes();
        if (!recipes.isEmpty()) {
            var startIndex = this.scrollbar.getCurrentScroll() * COLS;
            var endIndex = startIndex + COLS * ROWS;
            for (int i = startIndex; i < endIndex && i < recipes.size(); ++i) {
                var slotBounds = this.getRecipeBounds(i - startIndex);
                if (point.isIn(slotBounds)) {
                    return recipes.get(i);
                }
            }
        }
        return null;
    }

    private RegistryAccess getRegistryAccess() {
        return Objects.requireNonNull(Minecraft.getInstance().level).registryAccess();
    }

    private Level getLevel() {
        return this.menu.getPlayer().level();
    }

    private Rect2i getRecipeBounds(int index) {
        var col = index % COLS;
        var row = index / COLS;
        int slotX = x + 59 + col * BG_SLOT.getSrcWidth();
        int slotY = y + 8 + row * BG_SLOT.getSrcHeight();
        return new Rect2i(slotX, slotY, BG_SLOT.getSrcWidth(), BG_SLOT.getSrcHeight());
    }

    private void drawRecipes(GuiGraphics guiGraphics, Rect2i bounds, Point mouse) {
        var recipes = this.menu.getStonecutterRecipes().stream()
                .map(id -> this.getLevel().getRecipeManager().byKey(id))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
        if (recipes.size() != this.menu.getStonecutterRecipes().size()) {
            // Recipe isn't match between server and client
            return;
        }
        var startIndex = this.scrollbar.getCurrentScroll() * COLS;
        var endIndex = startIndex + ROWS * COLS;
        var selectedRecipe = this.menu.selectedStonecutterRecipe;

        for (int i = startIndex; i < endIndex && i < recipes.size(); ++i) {
            var slotBounds = this.getRecipeBounds(i - startIndex);
            var recipe = recipes.get(i);
            boolean selected = recipe.getId().equals(selectedRecipe);
            Blitter blitter = BG_SLOT;
            if (selected) {
                blitter = BG_SLOT_SELECTED;
            } else if (mouse.isIn(slotBounds)) {
                blitter = BG_SLOT_HOVER;
            }
            var renderX = bounds.getX() + slotBounds.getX();
            var renderY = bounds.getY() + slotBounds.getY();
            blitter.dest(renderX, renderY - 1).blit(guiGraphics);
            ItemStack resultItem = recipe.getResultItem(getRegistryAccess());
            guiGraphics.renderItem(resultItem, renderX, renderY);
            guiGraphics.renderItemDecorations(Minecraft.getInstance().font, resultItem, renderX, renderY);
        }
    }

    @Override
    public ItemStack getTabIconItem() {
        return new ItemStack(Items.STONECUTTER);
    }

    @Override
    public Component getTabTooltip() {
        return CraftingMode.STONECUTTER.getDisplayName();
    }

}
