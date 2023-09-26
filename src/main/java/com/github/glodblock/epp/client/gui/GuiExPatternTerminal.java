package com.github.glodblock.epp.client.gui;

import appeng.api.config.Settings;
import appeng.api.config.ShowPatternProviders;
import appeng.api.config.TerminalStyle;
import appeng.api.crafting.IPatternDetails;
import appeng.api.implementations.blockentities.PatternContainerGroup;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.me.patternaccess.PatternContainerRecord;
import appeng.client.gui.me.patternaccess.PatternSlot;
import appeng.client.gui.style.PaletteColor;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.AETextField;
import appeng.client.gui.widgets.Scrollbar;
import appeng.client.gui.widgets.ServerSettingToggleButton;
import appeng.client.gui.widgets.SettingToggleButton;
import appeng.client.guidebook.document.LytRect;
import appeng.client.guidebook.render.SimpleRenderContext;
import appeng.core.AEConfig;
import appeng.core.AppEng;
import appeng.core.localization.GuiText;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.InventoryActionPacket;
import appeng.crafting.pattern.EncodedPatternItem;
import appeng.helpers.InventoryAction;
import com.github.glodblock.epp.client.button.HighlightButton;
import com.github.glodblock.epp.container.ContainerExPatternTerminal;
import com.google.common.collect.HashMultimap;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.core.BlockPos;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

// 1.12holic
public class GuiExPatternTerminal extends AEBaseScreen<ContainerExPatternTerminal> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GuiExPatternTerminal.class);

    private static final int GUI_WIDTH = 209;
    private static final int MAGIC_NUMBER = 50;
    private static final int GUI_TOP_AND_BOTTOM_PADDING = 54;

    private static final int GUI_PADDING_X = 22;
    private static final int GUI_PADDING_Y = 6;

    private static final int GUI_HEADER_HEIGHT = 51;
    private static final int GUI_FOOTER_HEIGHT = 97;
    private static final int COLUMNS = 9;

    /**
     * Additional margin in pixel for a text row inside the scrolling box.
     */
    private static final int PATTERN_PROVIDER_NAME_MARGIN_X = 2;

    /**
     * The maximum length for the string of a text row in pixel.
     */
    private static final int TEXT_MAX_WIDTH = 155;

    /**
     * Height of a table-row in pixels.
     */
    private static final int ROW_HEIGHT = 18;

    /**
     * Size of a slot in both x and y dimensions in pixel, most likely always the same as ROW_HEIGHT.
     */
    private static final int SLOT_SIZE = ROW_HEIGHT;

    // Bounding boxes of key areas in the UI texture.
    // The upper part of the UI, anything above the scrollable area (incl. its top border)
    private static final Rect2i HEADER_BBOX = new Rect2i(0, 0, GUI_WIDTH, GUI_HEADER_HEIGHT);
    // Background for a text row in the scroll-box.
    // Spans across the whole texture including the right and left borders including the scrollbar.
    // Covers separate textures for the top, middle and bottoms rows for more customization.
    private static final Rect2i ROW_TEXT_TOP_BBOX = new Rect2i(0, 51, GUI_WIDTH, ROW_HEIGHT);
    private static final Rect2i ROW_TEXT_MIDDLE_BBOX = new Rect2i(0, 87, GUI_WIDTH, ROW_HEIGHT);
    private static final Rect2i ROW_TEXT_BOTTOM_BBOX = new Rect2i(0, 123, GUI_WIDTH, ROW_HEIGHT);
    // Background for a inventory row in the scroll-box.
    // Spans across the whole texture including the right and left borders including the scrollbar.
    // Covers separate textures for the top, middle and bottoms rows for more customization.
    private static final Rect2i ROW_INVENTORY_TOP_BBOX = new Rect2i(0, 69, GUI_WIDTH, ROW_HEIGHT);
    private static final Rect2i ROW_INVENTORY_MIDDLE_BBOX = new Rect2i(0, 105, GUI_WIDTH, ROW_HEIGHT);
    private static final Rect2i ROW_INVENTORY_BOTTOM_BBOX = new Rect2i(0, 141, GUI_WIDTH, ROW_HEIGHT);
    // This is the lower part of the UI, anything below the scrollable area (incl. its bottom border)
    private static final Rect2i FOOTER_BBOX = new Rect2i(0, 159, GUI_WIDTH, GUI_FOOTER_HEIGHT);

    private static final Comparator<PatternContainerGroup> GROUP_COMPARATOR = Comparator
            .comparing(group -> group.name().getString().toLowerCase(Locale.ROOT));

    private final HashMap<Long, PatternContainerRecord> byId = new HashMap<>();
    private final HashMap<Integer, HighlightButton> highlightBtns = new HashMap<>();
    private final HashMap<Long, PatternProviderInfo> infoMap = new HashMap<>();
    // Used to show multiple pattern providers with the same name under a single header
    private final HashMultimap<PatternContainerGroup, PatternContainerRecord> byGroup = HashMultimap.create();
    private final ArrayList<PatternContainerGroup> groups = new ArrayList<>();
    private final ArrayList<Row> rows = new ArrayList<>();

    private final Map<String, Set<Object>> cachedSearches = new WeakHashMap<>();
    private final Set<ItemStack> matchedStack = new ObjectOpenCustomHashSet<>(new Hash.Strategy<>() {
        @Override
        public int hashCode(ItemStack o) {
            return o.getItem().hashCode()
                    ^ o.getDamageValue()
                    ^ (o.hasTag() ? o.getTag().hashCode() : 0xFFFFFFFF);
        }

        @Override
        public boolean equals(ItemStack a, ItemStack b) {
            return a == b || (a != null && b != null && ItemStack.isSameItemSameTags(a, b));
        }
    });
    private final Set<PatternContainerRecord> matchedProvider = new HashSet<>();
    private final Scrollbar scrollbar;
    private final AETextField searchOutField;
    private final AETextField searchInField;

    private int visibleRows = 0;

    private final ServerSettingToggleButton<ShowPatternProviders> showPatternProviders;

    public GuiExPatternTerminal(ContainerExPatternTerminal menu, Inventory playerInventory,
                                   Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        this.scrollbar = widgets.addScrollBar("scrollbar");
        this.imageWidth = GUI_WIDTH;

        TerminalStyle terminalStyle = AEConfig.instance().getTerminalStyle();
        this.addToLeftToolbar(
                new SettingToggleButton<>(Settings.TERMINAL_STYLE, terminalStyle, this::toggleTerminalStyle));

        showPatternProviders = new ServerSettingToggleButton<>(Settings.TERMINAL_SHOW_PATTERN_PROVIDERS,
                ShowPatternProviders.VISIBLE);

        this.addToLeftToolbar(showPatternProviders);

        this.searchOutField = widgets.addTextField("search_out");
        this.searchOutField.setResponder(str -> this.refreshList());
        this.searchOutField.setPlaceholder(GuiText.SearchPlaceholder.text());
        this.searchOutField.setTooltipMessage(Collections.singletonList(Component.translatable("gui.expatternprovider.ex_pattern_access_terminal.tooltip.01")));

        this.searchInField = widgets.addTextField("search_in");
        this.searchInField.setResponder(str -> this.refreshList());
        this.searchInField.setPlaceholder(GuiText.SearchPlaceholder.text());
        this.searchInField.setTooltipMessage(Collections.singletonList(Component.translatable("gui.expatternprovider.ex_pattern_access_terminal.tooltip.02")));

    }

    @Override
    public void init() {
        this.visibleRows = config.getTerminalStyle().getRows(
                (this.height - GUI_HEADER_HEIGHT - GUI_FOOTER_HEIGHT - GUI_TOP_AND_BOTTOM_PADDING + MAGIC_NUMBER) / ROW_HEIGHT);
        if (this.visibleRows < 2) {
            this.visibleRows = 2;
        }
        // Render inventory in correct place.
        this.imageHeight = GUI_HEADER_HEIGHT + GUI_FOOTER_HEIGHT + this.visibleRows * ROW_HEIGHT;

        super.init();
        this.setInitialFocus(this.searchOutField);
        this.highlightBtns.forEach((k, v) -> {v.setVisibility(false); addRenderableWidget(v);});

        // numLines may have changed, recalculate scroll bar.
        this.resetScrollbar();
    }

    @Override
    public void drawFG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX,
                       int mouseY) {

        this.menu.slots.removeIf(slot -> slot instanceof PatternSlot);
        this.highlightBtns.forEach((key, value) -> value.setVisibility(false));

        int textColor = style.getColor(PaletteColor.DEFAULT_TEXT_COLOR).toARGB();

        final int scrollLevel = scrollbar.getCurrentScroll();
        int i = 0;
        for (; i < this.visibleRows; ++i) {
            if (scrollLevel + i < this.rows.size()) {
                var row = this.rows.get(scrollLevel + i);
                if (highlightBtns.containsKey(scrollLevel + i)) {
                    var btn = highlightBtns.get(scrollLevel + i);
                    btn.setPosition(this.leftPos + GUI_PADDING_X - SLOT_SIZE, this.topPos + (i + 1) * SLOT_SIZE + 34);
                    btn.setVisibility(true);
                }
                if (row instanceof SlotsRow slotsRow) {
                    // Note: We have to shift everything after the header up by 1 to avoid black line duplication.
                    var container = slotsRow.container;
                    for (int col = 0; col < slotsRow.slots; col++) {
                        var slot = new PatternSlot(
                                container,
                                slotsRow.offset + col,
                                col * SLOT_SIZE + GUI_PADDING_X,
                                (i + 1) * SLOT_SIZE + 34);
                        this.menu.slots.add(slot);
                        if (!this.searchOutField.getValue().isEmpty() || !this.searchInField.getValue().isEmpty()) {
                            if (this.matchedStack.contains(slot.getItem())) {
                                fillRect(guiGraphics, new Rect2i(slot.x, slot.y, 16, 16), 0x8A00FF00);
                            } else if (!this.matchedProvider.contains(container)) {
                                fillRect(guiGraphics, new Rect2i(slot.x, slot.y, 16, 16), 0x6A000000);
                            }
                        }
                    }
                } else if (row instanceof GroupHeaderRow headerRow) {
                    var group = headerRow.group;
                    if (group.icon() != null) {
                        var renderContext = new SimpleRenderContext(LytRect.empty(), guiGraphics);
                        renderContext.renderItem(
                                group.icon().toStack(),
                                GUI_PADDING_X + PATTERN_PROVIDER_NAME_MARGIN_X,
                                GUI_PADDING_Y + GUI_HEADER_HEIGHT + i * ROW_HEIGHT,
                                8,
                                8);
                    }

                    final int rows = this.byGroup.get(group).size();

                    FormattedText displayName;
                    if (rows > 1) {
                        displayName = Component.empty()
                                .append(group.name())
                                .append(Component.literal(" (" + rows + ')'));
                    } else {
                        displayName = group.name();
                    }

                    var text = Language.getInstance().getVisualOrder(
                            this.font.substrByWidth(displayName, TEXT_MAX_WIDTH - 10));

                    guiGraphics.drawString(font, text, GUI_PADDING_X + PATTERN_PROVIDER_NAME_MARGIN_X + 10,
                            GUI_PADDING_Y + GUI_HEADER_HEIGHT + i * ROW_HEIGHT, textColor, false);
                }
            }
        }
    }

    @Override
    protected void renderTooltip(@NotNull GuiGraphics guiGraphics, int x, int y) {
        // Draw line tooltip
        if (hoveredSlot == null) {
            var hoveredLineIndex = getHoveredLineIndex(x, y);
            if (hoveredLineIndex != -1) {
                var row = rows.get(hoveredLineIndex);
                if (row instanceof GroupHeaderRow headerRow && !headerRow.group.tooltip().isEmpty()) {
                    guiGraphics.renderTooltip(font, headerRow.group.tooltip(), Optional.empty(), x, y);
                    return;
                }
            }
        }
        super.renderTooltip(guiGraphics, x, y);
    }

    private int getHoveredLineIndex(int x, int y) {
        x = x - leftPos - GUI_PADDING_X;
        y = y - topPos - SLOT_SIZE; // Header is exactly one slot in size
        if (x < 0 || y < 0) {
            return -1;
        }
        if (x >= SLOT_SIZE * COLUMNS || y >= visibleRows * ROW_HEIGHT) {
            return -1;
        }

        var rowIndex = scrollbar.getCurrentScroll() + y / ROW_HEIGHT;
        if (rowIndex < 0 || rowIndex >= rows.size()) {
            return -1;
        }
        return rowIndex;
    }

    @Override
    public boolean mouseClicked(double xCoord, double yCoord, int btn) {
        if (btn == 1 && this.searchOutField.isMouseOver(xCoord, yCoord)) {
            this.searchOutField.setValue("");
        }
        if (btn == 1 && this.searchInField.isMouseOver(xCoord, yCoord)) {
            this.searchInField.setValue("");
        }

        return super.mouseClicked(xCoord, yCoord, btn);
    }

    @Override
    protected void slotClicked(Slot slot, int slotIdx, int mouseButton, ClickType clickType) {
        if (slot instanceof PatternSlot) {
            InventoryAction action = null;

            switch (clickType) {
                case PICKUP: // pickup / set-down.
                    action = mouseButton == 1 ? InventoryAction.SPLIT_OR_PLACE_SINGLE
                            : InventoryAction.PICKUP_OR_SET_DOWN;
                    break;
                case QUICK_MOVE:
                    action = mouseButton == 1 ? InventoryAction.PICKUP_SINGLE : InventoryAction.SHIFT_CLICK;
                    break;

                case CLONE: // creative dupe:
                    if (getPlayer().getAbilities().instabuild) {
                        action = InventoryAction.CREATIVE_DUPLICATE;
                    }

                    break;

                default:
                case THROW: // drop item:
            }

            if (action != null) {
                PatternSlot machineSlot = (PatternSlot) slot;
                final InventoryActionPacket p = new InventoryActionPacket(action, machineSlot.getSlotIndex(),
                        machineSlot.getMachineInv().getServerId());
                NetworkHandler.instance().sendToServer(p);
            }

            return;
        }

        super.slotClicked(slot, slotIdx, mouseButton, clickType);
    }

    @Override
    public void drawBG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX,
                       int mouseY, float partialTicks) {
        // Draw the top of the dialog
        blit(guiGraphics, offsetX, offsetY, HEADER_BBOX);

        final int scrollLevel = scrollbar.getCurrentScroll();

        int currentY = offsetY + GUI_HEADER_HEIGHT;

        // Draw the footer now so slots will draw on top of it
        blit(guiGraphics, offsetX, currentY + this.visibleRows * ROW_HEIGHT, FOOTER_BBOX);

        for (int i = 0; i < this.visibleRows; ++i) {
            // Draw the dialog background for this row
            // Skip 1 pixel for the first row in order to not over-draw on the top scrollbox border,
            // and do the same but for the bottom border on the last row
            boolean firstLine = i == 0;
            boolean lastLine = i == this.visibleRows - 1;

            // Draw the background for the slots in an inventory row
            Rect2i bbox = selectRowBackgroundBox(false, firstLine, lastLine);
            blit(guiGraphics, offsetX, currentY, bbox);
            if (scrollLevel + i < this.rows.size()) {
                var row = this.rows.get(scrollLevel + i);
                if (row instanceof SlotsRow slotsRow) {
                    bbox = selectRowBackgroundBox(true, firstLine, lastLine);
                    bbox.setWidth(GUI_PADDING_X + SLOT_SIZE * slotsRow.slots - 1);
                    blit(guiGraphics, offsetX, currentY, bbox);
                }
            }

            currentY += ROW_HEIGHT;
        }
    }

    private Rect2i selectRowBackgroundBox(boolean isInvLine, boolean firstLine, boolean lastLine) {
        if (isInvLine) {
            if (firstLine) {
                return ROW_INVENTORY_TOP_BBOX;
            } else if (lastLine) {
                return ROW_INVENTORY_BOTTOM_BBOX;
            } else {
                return ROW_INVENTORY_MIDDLE_BBOX;
            }
        } else if (firstLine) {
            return ROW_TEXT_TOP_BBOX;
        } else if (lastLine) {
            return ROW_TEXT_BOTTOM_BBOX;
        } else {
            return ROW_TEXT_MIDDLE_BBOX;
        }
    }

    @Override
    public boolean charTyped(char character, int key) {
        if (character == ' ' && ((this.searchOutField.getValue().isEmpty() && this.searchOutField.isFocused()) || (this.searchInField.getValue().isEmpty() && this.searchInField.isFocused()))) {
            return true;
        }
        return super.charTyped(character, key);
    }

    public void clear() {
        this.byId.clear();
        this.infoMap.clear();
        // invalid caches on refresh
        this.cachedSearches.clear();
        this.refreshList();
    }

    public void postTileInfo(long id, BlockPos pos, ResourceKey<Level> dim) {
        this.infoMap.put(id, new PatternProviderInfo(pos, dim));
        this.refreshList();
    }

    public void postFullUpdate(long inventoryId,
                               long sortBy,
                               PatternContainerGroup group,
                               int inventorySize,
                               Int2ObjectMap<ItemStack> slots) {
        var record = new PatternContainerRecord(inventoryId, inventorySize, sortBy, group);
        this.byId.put(inventoryId, record);

        var inventory = record.getInventory();
        for (var entry : slots.int2ObjectEntrySet()) {
            inventory.setItemDirect(entry.getIntKey(), entry.getValue());
        }

        // invalid caches on refresh
        this.cachedSearches.clear();
        this.refreshList();
    }

    public void postIncrementalUpdate(long inventoryId,
                                      Int2ObjectMap<ItemStack> slots) {
        var record = byId.get(inventoryId);
        if (record == null) {
            LOGGER.warn("Ignoring incremental update for unknown inventory id {}", inventoryId);
            return;
        }

        var inventory = record.getInventory();
        for (var entry : slots.int2ObjectEntrySet()) {
            inventory.setItemDirect(entry.getIntKey(), entry.getValue());
        }
    }

    @Override
    public void updateBeforeRender() {
        super.updateBeforeRender();
        this.showPatternProviders.set(this.menu.getShownProviders());
    }

    /**
     * Rebuilds the list of pattern providers.
     * <p>
     * Respects a search term if present (ignores case) and adding only matching patterns.
     */
    private void refreshList() {
        this.byGroup.clear();
        this.highlightBtns.forEach((k, v) -> this.removeWidget(v));
        this.highlightBtns.clear();
        this.matchedStack.clear();
        this.matchedProvider.clear();

        final String outputFilter = this.searchOutField.getValue().toLowerCase();
        final String inputFilter = this.searchInField.getValue().toLowerCase();

        final Set<Object> cachedSearch = this.getCacheForSearchTerm("out:" + outputFilter + "in:" + inputFilter);
        final boolean rebuild = cachedSearch.isEmpty();

        for (PatternContainerRecord entry : this.byId.values()) {
            // ignore inventory if not doing a full rebuild or cache already marks it as miss.
            if (!rebuild && !cachedSearch.contains(entry)) {
                continue;
            }

            // Shortcut to skip any filter if search term is ""/empty
            boolean found = outputFilter.isEmpty() && inputFilter.isEmpty();

            // Search if the current inventory holds a pattern containing the search term.
            if (!found) {
                for (ItemStack itemStack : entry.getInventory()) {
                    if (!outputFilter.isEmpty()) {
                        found = this.itemStackMatchesSearchTerm(itemStack, outputFilter, true);
                    } else {
                        found = true;
                    }
                    if (!inputFilter.isEmpty() && found) {
                        found = this.itemStackMatchesSearchTerm(itemStack, inputFilter, false);
                    }
                }
            }

            // if found, filter skipped or machine name matching the search term, add it
            if (found || (entry.getSearchName().contains(outputFilter) && entry.getSearchName().contains(inputFilter))) {
                this.byGroup.put(entry.getGroup(), entry);
                cachedSearch.add(entry);
                if (entry.getSearchName().contains(outputFilter) && entry.getSearchName().contains(inputFilter)) {
                    this.matchedProvider.add(entry);
                }
            } else {
                cachedSearch.remove(entry);
            }
        }

        this.groups.clear();
        this.groups.addAll(this.byGroup.keySet());

        this.groups.sort(GROUP_COMPARATOR);

        this.rows.clear();
        this.rows.ensureCapacity(this.getMaxRows());

        for (var group : this.groups) {
            this.rows.add(new GroupHeaderRow(group));

            var containers = new ArrayList<>(this.byGroup.get(group));
            Collections.sort(containers);
            for (var container : containers) {
                var inventory = container.getInventory();
                if (inventory.size() > 0) {
                    var info = this.infoMap.get(container.getServerId());
                    var btn = new HighlightButton();
                    btn.setMultiplier(this.playerToBlockDis(info.pos()));
                    btn.setTarget(info.pos, info.playerWorld);
                    btn.setSuccessJob(() -> {
                        if (this.getPlayer() != null && info.pos != null && info.playerWorld != null) {
                            this.getPlayer().displayClientMessage(Component.translatable("chat.ex_pattern_access_terminal.pos", info.pos.toShortString(), info.playerWorld.location().getPath()), false);
                        }
                    });
                    btn.setTooltip(Tooltip.create(Component.translatable("gui.expatternprovider.ex_pattern_access_terminal.tooltip.03")));
                    btn.setVisibility(false);
                    this.highlightBtns.put(this.rows.size(), this.addRenderableWidget(btn));
                }
                for (var offset = 0; offset < inventory.size(); offset += COLUMNS) {
                    var slots = Math.min(inventory.size() - offset, COLUMNS);
                    var containerRow = new SlotsRow(container, offset, slots);
                    this.rows.add(containerRow);
                }
            }
        }

        // lines may have changed - recalculate scroll bar.
        this.resetScrollbar();
    }

    private double playerToBlockDis(BlockPos pos) {
        if (pos == null) {
            return 0;
        }
        var ps = this.getPlayer().getOnPos();
        return pos.distSqr(ps);
    }

    /**
     * Should be called whenever this.lines.size() or this.numLines changes.
     */
    private void resetScrollbar() {
        // Needs to take the border into account, so offset for 1 px on the top and bottom.
        scrollbar.setHeight(this.visibleRows * ROW_HEIGHT - 2);
        scrollbar.setRange(0, this.rows.size() - this.visibleRows, 2);
    }

    private boolean itemStackMatchesSearchTerm(ItemStack itemStack, String searchTerm, boolean checkOut) {
        if (itemStack.isEmpty()) {
            return false;
        }

        IPatternDetails result = null;
        if (itemStack.getItem() instanceof EncodedPatternItem pattern) {
            result = pattern.decode(itemStack, this.menu.getPlayer().level(), false);
        }
        if (result == null) {
            return false;
        }

        var list = checkOut ?
                Arrays.asList(result.getOutputs()) :
                Arrays.stream(result.getInputs()).map(i -> i.getPossibleInputs()[0]).collect(Collectors.toList());
        for (var item : list) {
            if (item != null) {
                var displayName = item.what().getDisplayName().getString().toLowerCase();
                if (displayName.contains(searchTerm)) {
                    this.matchedStack.add(itemStack);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Tries to retrieve a cache for a with search term as keyword.
     * <p>
     * If this cache should be empty, it will populate it with an earlier cache if available or at least the cache for
     * the empty string.
     *
     * @param searchTerm the corresponding search
     * @return a Set matching a superset of the search term
     */
    private Set<Object> getCacheForSearchTerm(String searchTerm) {
        if (!this.cachedSearches.containsKey(searchTerm)) {
            this.cachedSearches.put(searchTerm, new HashSet<>());
        }

        final Set<Object> cache = this.cachedSearches.get(searchTerm);

        if (cache.isEmpty() && searchTerm.length() > 1) {
            cache.addAll(this.getCacheForSearchTerm(searchTerm.substring(0, searchTerm.length() - 1)));
        }

        return cache;
    }

    private void reinitialize() {
        this.children().removeAll(this.renderables);
        this.renderables.clear();
        this.init();
    }

    private void toggleTerminalStyle(SettingToggleButton<TerminalStyle> btn, boolean backwards) {
        TerminalStyle next = btn.getNextValue(backwards);
        AEConfig.instance().setTerminalStyle(next);
        btn.set(next);
        this.reinitialize();
    }

    /**
     * The max amount of unique names and each inv row. Not affected by the filtering.
     *
     * @return max amount of unique names and each inv row
     */
    private int getMaxRows() {
        return this.groups.size() + this.byId.size();
    }

    /**
     * A version of blit that lets us pass a source rectangle
     *
     * @see GuiGraphics#blit(ResourceLocation, int, int, int, int, int, int)
     */
    private void blit(GuiGraphics guiGraphics, int offsetX, int offsetY, Rect2i srcRect) {
        var texture = AppEng.makeId("textures/guis/ex_pattern_access_terminal.png");
        guiGraphics.blit(texture, offsetX, offsetY, srcRect.getX(), srcRect.getY(), srcRect.getWidth(),
                srcRect.getHeight());
    }

    sealed interface Row {
    }

    /**
     * A row containing a header for a group.
     */
    record GroupHeaderRow(PatternContainerGroup group) implements Row {
    }

    /**
     * A row containing slots for a subset of a pattern container inventory.
     */
    record SlotsRow(PatternContainerRecord container, int offset, int slots) implements Row {
    }

    public record PatternProviderInfo(@Nullable BlockPos pos, @Nullable ResourceKey<Level> playerWorld) {

    }
}