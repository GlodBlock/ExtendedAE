package com.glodblock.github.extendedae.container;

import appeng.api.config.Actionable;
import appeng.api.inventories.InternalInventory;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.InventoryActionPacket;
import appeng.helpers.IMenuCraftingPacket;
import appeng.helpers.InventoryAction;
import appeng.menu.SlotSemantics;
import appeng.menu.guisync.GuiSync;
import appeng.menu.guisync.PacketWritable;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.me.common.MEStorageMenu;
import appeng.menu.me.crafting.CraftConfirmMenu;
import appeng.menu.me.items.CraftingTermMenu;
import appeng.menu.slot.CraftingMatrixSlot;
import appeng.util.inv.PlayerInternalInventory;
import com.glodblock.github.extendedae.api.CraftingMode;
import com.glodblock.github.extendedae.client.ExSemantics;
import com.glodblock.github.extendedae.client.gui.widget.OutputResultSlot;
import com.glodblock.github.extendedae.common.parts.PartExCraftingTerminal;
import com.glodblock.github.extendedae.network.EPPNetworkHandler;
import com.glodblock.github.extendedae.util.EPPTags;
import com.glodblock.github.glodium.network.packet.SGenericPacket;
import com.glodblock.github.glodium.network.packet.sync.IActionHolder;
import com.glodblock.github.glodium.network.packet.sync.Paras;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringUtil;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class ContainerExCraftingTerminal extends MEStorageMenu implements IMenuCraftingPacket, IActionHolder {

    public static final MenuType<ContainerExCraftingTerminal> TYPE = MenuTypeBuilder
            .create(ContainerExCraftingTerminal::new, PartExCraftingTerminal.class)
            .build("ex_crafting_terminal");

    private static final long MB_PER_XP = 20;
    private static List<Fluid> XP_FLUIDS = new ArrayList<>();

    private final Map<String, Consumer<Paras>> actions = createHolder();
    private final PartExCraftingTerminal host;
    private final CraftingMatrixSlot[] craftingInputSlots = new CraftingMatrixSlot[9];
    private final CraftingMatrixSlot[] stonecutterInputSlots = new CraftingMatrixSlot[1];
    private final CraftingMatrixSlot[] smithingInputSlots = new CraftingMatrixSlot[3];
    private final CraftingMatrixSlot[] anvilInputSlots = new CraftingMatrixSlot[2];
    private OutputResultSlot craftingOutputSlot;
    private OutputResultSlot stonecutterOutputSlot;
    private OutputResultSlot smithingOutputSlot;
    private OutputResultSlot anvilOutputSlot;
    private final CraftingContainer recipeTestContainer = new TransientCraftingContainer(this, 3, 3);
    @Nullable
    private Recipe<Container> currentRecipe;
    private int anvilMaterialCost;

    @GuiSync(0)
    public CraftingMode currentMode;
    @GuiSync(1)
    @Nullable
    public ResourceLocation selectedStonecutterRecipe;
    @GuiSync(2)
    public String itemName = "";
    @GuiSync(3)
    public int anvilCost;
    @GuiSync(4)
    public StonecutterRecipeList stonecutterRecipes = new StonecutterRecipeList();

    public ContainerExCraftingTerminal(int id, Inventory playerInventory, PartExCraftingTerminal host) {
        super(TYPE, id, playerInventory, host, true);
        this.host = host;
        this.currentMode = host.getCurrentMode();
        this.selectedStonecutterRecipe = host.getStonecuttingRecipe();
        this.setupCraftingSlots();
        this.setupStonecutterSlots();
        this.setupSmithingSlots();
        this.setupAnvilSlots();
        this.updateCurrentRecipeAndOutput(true);
        this.actions.put("set_mode", o -> this.setMode(o.get(0)));
        this.actions.put("stonecutter_select", o -> this.selectStonecutterRecipe(o.get(0)));
        this.actions.put("clearCraftingGrid", o -> this.clearCraftingGrid());
        this.actions.put("clearToPlayerInv", o -> this.clearToPlayerInventory());
        this.actions.put("anvil_rename", o -> this.setItemName(o.get(0)));
    }

    private void setupCraftingSlots() {
        var inv = this.host.getSubInventory(PartExCraftingTerminal.INV_CRAFTING);
        if (inv != null) {
            for (int i = 0; i < 9; i++) {
                this.addSlot(this.craftingInputSlots[i] = new CraftingMatrixSlot(this, inv, i), SlotSemantics.CRAFTING_GRID);
            }
            this.addSlot(this.craftingOutputSlot = new OutputResultSlot(this.getPlayer(), this.getActionSource(), this.powerSource, host.getInventory(), inv, inv, this, RecipeType.CRAFTING), SlotSemantics.CRAFTING_RESULT);
        }
    }

    private void setupStonecutterSlots() {
        var inv = this.host.getSubInventory(PartExCraftingTerminal.INV_STONECUTTING);
        if (inv != null) {
            this.addSlot(this.stonecutterInputSlots[0] = new CraftingMatrixSlot(this, inv, 0), SlotSemantics.STONECUTTING_INPUT);
            this.addSlot(this.stonecutterOutputSlot = new OutputResultSlot(this.getPlayer(), this.getActionSource(), this.powerSource, host.getInventory(), inv, inv, this, RecipeType.STONECUTTING), ExSemantics.EX_1);
        }
    }

    private void setupSmithingSlots() {
        var inv = this.host.getSubInventory(PartExCraftingTerminal.INV_SMITHING);
        if (inv != null) {
            this.addSlot(this.smithingInputSlots[0] = new CraftingMatrixSlot(this, inv, 0), SlotSemantics.SMITHING_TABLE_TEMPLATE);
            this.addSlot(this.smithingInputSlots[1] = new CraftingMatrixSlot(this, inv, 1), SlotSemantics.SMITHING_TABLE_BASE);
            this.addSlot(this.smithingInputSlots[2] = new CraftingMatrixSlot(this, inv, 2), SlotSemantics.SMITHING_TABLE_ADDITION);
            this.addSlot(this.smithingOutputSlot = new OutputResultSlot(this.getPlayer(), this.getActionSource(), this.powerSource, host.getInventory(), inv, inv, this, RecipeType.SMITHING), SlotSemantics.SMITHING_TABLE_RESULT);
        }
    }

    private void setupAnvilSlots() {
        var inv = this.host.getSubInventory(PartExCraftingTerminal.INV_ANVIL);
        if (inv != null) {
            this.addSlot(this.anvilInputSlots[0] = new CraftingMatrixSlot(this, inv, 0), ExSemantics.EX_2);
            this.addSlot(this.anvilInputSlots[1] = new CraftingMatrixSlot(this, inv, 1), ExSemantics.EX_3);
            this.addSlot(this.anvilOutputSlot = new OutputResultSlot(this.getPlayer(), this.getActionSource(), this.powerSource, host.getInventory(), inv, inv, this, null) {

                @Override
                protected ItemStack specialHandler(Player p, ItemStack result, CraftingContainer container) {
                    if (!ContainerExCraftingTerminal.this.getAndPerformAnvilCraft(p, true).isEmpty()) {
                        return ContainerExCraftingTerminal.this.getAndPerformAnvilCraft(p, false);
                    } else {
                        return ItemStack.EMPTY;
                    }
                }

            }, ExSemantics.EX_4);
        }
    }

    private void selectStonecutterRecipe(String name) {
        this.selectedStonecutterRecipe = ResourceLocation.tryParse(name);
        this.host.setStonecuttingRecipe(this.selectedStonecutterRecipe);
        this.updateCurrentRecipeAndOutput(true);
    }

    public CraftingMode getCurrentMode() {
        return this.currentMode;
    }

    public void setMode(int mode) {
        this.currentMode = CraftingMode.fromOrdinal(mode);
        this.host.setCurrentMode(this.currentMode);
        this.updateCurrentRecipeAndOutput(true);
        this.broadcastChanges();
    }

    public boolean isAnvilRecipeValid() {
        return this.anvilOutputSlot.hasItem();
    }

    public boolean isInputEmpty() {
        return !this.anvilInputSlots[0].hasItem() && !this.anvilInputSlots[1].hasItem();
    }

    public List<ResourceLocation> getStonecutterRecipes() {
        return this.stonecutterRecipes.recipes();
    }

    @Override
    public void doAction(ServerPlayer player, InventoryAction action, int slot, long id) {
        if (slot < 0 || slot >= this.slots.size()) {
            return;
        }
        var s = this.getSlot(slot);
        if (s instanceof OutputResultSlot outputSlot) {
            switch (action) {
                case CRAFT_SHIFT:
                case CRAFT_ALL:
                case CRAFT_ITEM:
                case CRAFT_STACK:
                    outputSlot.doClick(action, player);
                default:
            }
        }
        super.doAction(player, action, slot, id);
    }

    @Override
    public void slotsChanged(@NotNull Container container) {
        if (container != this.recipeTestContainer) {
            super.slotsChanged(container);
            if (this.isServerSide()) {
                this.updateCurrentRecipeAndOutput(false);
            }
        }
    }

    public void setItemName(String name) {
        this.itemName = name;
        if (this.isServerSide()) {
            this.updateCurrentRecipeAndOutput(true);
        }
    }

    public void playSound() {
        if (this.getPlayer() instanceof ServerPlayer sp) {
            EPPNetworkHandler.INSTANCE.sendTo(new SGenericPacket("play_sound", this.currentMode.ordinal()), sp);
        }
    }

    private void setAnvilOutput() {
        ItemStack left = this.anvilInputSlots[0].getItem();
        ItemStack right = this.anvilInputSlots[1].getItem();

        this.anvilCost = 0;
        this.anvilMaterialCost = 0;
        this.anvilOutputSlot.set(ItemStack.EMPTY);

        // Update available liquid XP
        if (left.isEmpty()) {
            return;
        }

        ItemStack result = left.copy();
        int baseCost = 0;
        int repairCost = left.getBaseRepairCost() + (right.isEmpty() ? 0 : right.getBaseRepairCost());
        boolean increaseCost = false;
        boolean hasOperation = false;

        // Renaming
        if (!StringUtil.isNullOrEmpty(this.itemName)) {
            String currentName = left.hasCustomHoverName() ? left.getHoverName().getString() : "";
            if (!this.itemName.equals(currentName)) {
                baseCost += 1;
                result.setHoverName(Component.literal(this.itemName));
                hasOperation = true;
            }
        } else if (left.hasCustomHoverName()) {
            baseCost += 1;
            result.resetHoverName();
            hasOperation = true;
        }

        // Combining items
        if (!right.isEmpty()) {
            if (left.getCount() != 1) {
                this.anvilOutputSlot.set(ItemStack.EMPTY);
                return;
            }
            if (left.getItem() == right.getItem() && left.isDamageableItem()) {
                // Repair by combining
                int leftDurability = left.getMaxDamage() - left.getDamageValue();
                int rightDurability = right.getMaxDamage() - right.getDamageValue();
                int bonus = left.getMaxDamage() * 12 / 100;
                int newDurability = Math.min(left.getMaxDamage(), leftDurability + rightDurability + bonus);
                int repairAmount = newDurability - leftDurability;
                if (repairAmount > 0) {
                    result.setDamageValue(left.getMaxDamage() - newDurability);
                    baseCost += 2;
                    hasOperation = true;
                }
                // Transfer enchantments
                Map<Enchantment, Integer> rightEnchants = EnchantmentHelper.getEnchantments(right);
                if (!rightEnchants.isEmpty()) {
                    Map<Enchantment, Integer> leftEnchants = EnchantmentHelper.getEnchantments(result);
                    for (Map.Entry<Enchantment, Integer> entry : rightEnchants.entrySet()) {
                        Enchantment enchant = entry.getKey();
                        int rightLevel = entry.getValue();
                        int leftLevel = leftEnchants.getOrDefault(enchant, 0);
                        if (enchant.canEnchant(left)) {
                            int newLevel = Math.max(leftLevel, rightLevel);
                            if (leftLevel == rightLevel) {
                                newLevel += 1;
                                if (newLevel > enchant.getMaxLevel()) {
                                    continue;
                                }
                            }
                            // Check compatibility
                            boolean compatible = true;
                            for (Enchantment existing : leftEnchants.keySet()) {
                                if (existing != enchant && !enchant.isCompatibleWith(existing)) {
                                    compatible = false;
                                    baseCost += 1;
                                    break;
                                }
                            }
                            if (compatible) {
                                leftEnchants.put(enchant, newLevel);
                                int rarityCost = this.getEnchantCost(enchant);
                                baseCost += newLevel * rarityCost;
                                increaseCost = true;
                                hasOperation = true;
                            }
                        }
                    }
                    EnchantmentHelper.setEnchantments(leftEnchants, result);
                }
            } else if (right.getItem() == Items.ENCHANTED_BOOK) {
                Map<Enchantment, Integer> bookEnchants = EnchantmentHelper.getEnchantments(right);
                Map<Enchantment, Integer> leftEnchants = EnchantmentHelper.getEnchantments(result);
                for (Map.Entry<Enchantment, Integer> entry : bookEnchants.entrySet()) {
                    Enchantment enchant = entry.getKey();
                    int bookLevel = entry.getValue();
                    int leftLevel = leftEnchants.getOrDefault(enchant, 0);
                    if (enchant.canEnchant(left) || left.getItem() == Items.ENCHANTED_BOOK) {
                        int newLevel = Math.max(leftLevel, bookLevel);
                        if (leftLevel == bookLevel) {
                            newLevel += 1;
                            if (newLevel > enchant.getMaxLevel()) {
                                continue;
                            }
                        }
                        // Check compatibility
                        boolean compatible = true;
                        for (Enchantment existing : leftEnchants.keySet()) {
                            if (existing != enchant && !enchant.isCompatibleWith(existing)) {
                                compatible = false;
                                baseCost += 1;
                                break;
                            }
                        }
                        if (compatible) {
                            leftEnchants.put(enchant, newLevel);
                            int rarityCost = this.getEnchantCost(enchant);
                            // See Anvil Menu's code
                            rarityCost = Math.max(rarityCost / 2, 1);
                            baseCost += newLevel * rarityCost;
                            increaseCost = true;
                            hasOperation = true;
                        }
                    }
                }
                if (hasOperation) {
                    EnchantmentHelper.setEnchantments(leftEnchants, result);
                }
            } else if (left.isDamageableItem() && left.getItem().isValidRepairItem(left, right)) {
                // Repair with material
                int damagePerMaterial = Math.max(1, left.getMaxDamage() / 4);
                int materialsNeeded = 0;
                int currentDamage = left.getDamageValue();
                while (currentDamage > 0 && materialsNeeded < right.getCount()) {
                    currentDamage = Math.max(0, currentDamage - damagePerMaterial);
                    materialsNeeded++;
                    baseCost += 1;
                }
                if (materialsNeeded > 0) {
                    result.setDamageValue(currentDamage);
                    this.anvilMaterialCost = materialsNeeded;
                    hasOperation = true;
                }
            } else {
                // invalid combine
                result = ItemStack.EMPTY;
                hasOperation = true;
            }
        }

        if (!hasOperation) {
            return;
        }

        // Calculate final cost
        this.anvilCost = repairCost + baseCost;

        // Update repair cost on result
        if (increaseCost) {
            result.setRepairCost(repairCost * 2 + 1);
        }
        this.anvilOutputSlot.set(result);
    }

    private int getEnchantCost(Enchantment enchantment) {
        return switch (enchantment.getRarity()) {
            case COMMON -> 1;
            case UNCOMMON -> 2;
            case RARE -> 4;
            case VERY_RARE -> 8;
        };
    }

    @NotNull
    private List<Fluid> getXPFluids() {
        if (XP_FLUIDS == null) {
            initXPFluid();
        }
        return XP_FLUIDS;
    }

    @NotNull
    public ItemStack getAndPerformAnvilCraft(Player player, boolean simulate) {
        if (this.anvilCost <= 0) {
            return ItemStack.EMPTY;
        }
        int levelsRequired = this.anvilCost;
        if (!player.getAbilities().instabuild) {
            // First consume liquid XP, then player XP
            int remainingCost = this.consumeLiquidXp(levelsRequired, simulate);
            if (remainingCost > 0) {
                if (!simulate) {
                    player.giveExperienceLevels(-remainingCost);
                } else {
                    if (player.experienceLevel < remainingCost) {
                        return ItemStack.EMPTY;
                    }
                }
            }
        }
        var result = this.anvilOutputSlot.getItem().copy();
        if (!simulate) {
            // Consume input items
            this.anvilInputSlots[0].set(ItemStack.EMPTY);
            if (this.anvilMaterialCost > 0) {
                ItemStack right = this.anvilInputSlots[1].getItem();
                right.shrink(this.anvilMaterialCost);
                this.anvilInputSlots[1].set(right);
            } else {
                this.anvilInputSlots[1].set(ItemStack.EMPTY);
            }
        }
        return result;
    }

    // Return the needed level - fluid experience
    private int consumeLiquidXp(int levelsRequired, boolean simulate) {
        var grid = this.host.getMainNode().getGrid();
        if (grid == null) {
            return levelsRequired;
        }

        var storage = grid.getStorageService();
        var inventory = storage.getInventory();
        var source = this.getActionSource();
        if (inventory == null) {
            return levelsRequired;
        }

        // Convert levels to XP points (rough approximation)
        // Level N requires about N*7 XP points on average
        long xpNeeded = getTotalXpForLevel(levelsRequired);
        long mbNeeded = xpNeeded * MB_PER_XP;

        for (var fluid : this.getXPFluids()) {
            if (fluid != null) {
                var key = AEFluidKey.of(fluid);
                var extracted = inventory.extract(key, mbNeeded, Actionable.ofSimulate(simulate), source);
                mbNeeded -= extracted;
                if (mbNeeded <= 0) {
                    break;
                }
            }
        }
        var fluidXp = xpNeeded * MB_PER_XP - Math.max(mbNeeded, 0);
        return Math.toIntExact(levelsRequired - Math.round((double) fluidXp / MB_PER_XP / 7));
    }

    public static int getTotalXpForLevel(int level) {
        if (level <= 16) {
            return level * level + 6 * level;
        } else if (level <= 31) {
            return (int) (2.5 * level * level - 40.5 * level + 360);
        } else {
            return (int) (4.5 * level * level - 162.5 * level + 2220);
        }
    }

    @NotNull
    @Override
    public Map<String, Consumer<Paras>> getActionMap() {
        return this.actions;
    }

    @Override
    public InternalInventory getCraftingMatrix() {
        return switch (this.currentMode) {
            case CRAFTING -> this.host.getSubInventory(PartExCraftingTerminal.INV_CRAFTING);
            case SMITHING -> this.host.getSubInventory(PartExCraftingTerminal.INV_SMITHING);
            case STONECUTTER -> this.host.getSubInventory(PartExCraftingTerminal.INV_STONECUTTING);
            case ANVIL -> this.host.getSubInventory(PartExCraftingTerminal.INV_ANVIL);
        };
    }

    @Override
    public boolean useRealItems() {
        return true;
    }

    public @Nullable Recipe<Container> getCurrentRecipe() {
        return this.currentRecipe;
    }

    public void clearToPlayerInventory() {
        switch (this.currentMode) {
            case CRAFTING -> this.clearToPlayerInventory(PartExCraftingTerminal.INV_CRAFTING);
            case SMITHING -> this.clearToPlayerInventory(PartExCraftingTerminal.INV_SMITHING);
            case STONECUTTER -> this.clearToPlayerInventory(PartExCraftingTerminal.INV_STONECUTTING);
            case ANVIL -> this.clearToPlayerInventory(PartExCraftingTerminal.INV_ANVIL);
        }
    }

    public void clearToPlayerInventory(ResourceLocation id) {
        var craftingGridInv = this.host.getSubInventory(id);
        var playerInv = new PlayerInternalInventory(this.getPlayerInventory());
        if (craftingGridInv != null) {
            for (int i = 0; i < craftingGridInv.size(); ++i) {
                for (int emptyLoop = 0; emptyLoop < 2; ++emptyLoop) {
                    boolean allowEmpty = emptyLoop == 1;
                    // Hotbar first
                    final int HOTBAR_SIZE = 9;
                    for (int j = HOTBAR_SIZE; j-- > 0;) {
                        if (playerInv.getStackInSlot(j).isEmpty() == allowEmpty) {
                            craftingGridInv.setItemDirect(i, playerInv.getSlotInv(j).addItems(craftingGridInv.getStackInSlot(i)));
                        }
                    }
                    // Rest of inventory
                    for (int j = HOTBAR_SIZE; j < Inventory.INVENTORY_SIZE; ++j) {
                        if (playerInv.getStackInSlot(j).isEmpty() == allowEmpty) {
                            craftingGridInv.setItemDirect(i, playerInv.getSlotInv(j).addItems(craftingGridInv.getStackInSlot(i)));
                        }
                    }
                }
            }
        }
        this.updateCurrentRecipeAndOutput(false);
    }

    public void clearCraftingGrid() {
        switch (this.currentMode) {
            case CRAFTING -> this.clearCraftingGrid(this.craftingInputSlots[0].index);
            case SMITHING -> {
                this.clearCraftingGrid(this.smithingInputSlots[0].index);
                this.clearCraftingGrid(this.smithingInputSlots[1].index);
                this.clearCraftingGrid(this.smithingInputSlots[2].index);
            }
            case STONECUTTER -> this.clearCraftingGrid(this.stonecutterInputSlots[0].index);
            case ANVIL -> {
                this.clearCraftingGrid(this.anvilInputSlots[0].index);
                this.clearCraftingGrid(this.anvilInputSlots[1].index);
            }
        }
    }

    public void clearCraftingGrid(int slotIndex) {
        if (this.getPlayer() instanceof ServerPlayer sp) {
            this.doAction(sp, InventoryAction.MOVE_REGION, slotIndex, 0);
        } else {
            var p = new InventoryActionPacket(InventoryAction.MOVE_REGION, slotIndex, 0);
            NetworkHandler.instance().sendToServer(p);
        }
    }

    public void clearAllGrid() {
        this.clearCraftingGrid(this.craftingInputSlots[0].index);
        this.clearCraftingGrid(this.stonecutterInputSlots[0].index);
        this.clearCraftingGrid(this.smithingInputSlots[0].index);
        this.clearCraftingGrid(this.smithingInputSlots[1].index);
        this.clearCraftingGrid(this.smithingInputSlots[2].index);
        this.clearCraftingGrid(this.anvilInputSlots[0].index);
        this.clearCraftingGrid(this.anvilInputSlots[1].index);
    }

    @Override
    public void startAutoCrafting(List<AutoCraftEntry> toCraft) {
        CraftConfirmMenu.openWithCraftingList(getActionHost(), (ServerPlayer) getPlayer(), getLocator(), toCraft);
    }

    @SuppressWarnings("unchecked")
    private void updateCurrentRecipeAndOutput(boolean forceUpdate) {
        boolean hasChanged = forceUpdate;
        CraftingMatrixSlot[] inputSlots;
        RecipeType<?> type;
        Consumer<ItemStack> setOutput;
        if (this.getCurrentMode() == CraftingMode.CRAFTING) {
            inputSlots = this.craftingInputSlots;
            type = RecipeType.CRAFTING;
            setOutput = stack -> this.craftingOutputSlot.set(stack);
        } else if (this.getCurrentMode() == CraftingMode.SMITHING) {
            inputSlots = this.smithingInputSlots;
            type = RecipeType.SMITHING;
            setOutput = stack -> this.smithingOutputSlot.set(stack);
        } else if (this.getCurrentMode() == CraftingMode.STONECUTTER) {
            inputSlots = this.stonecutterInputSlots;
            type = RecipeType.STONECUTTING;
            setOutput = stack -> this.stonecutterOutputSlot.set(stack);
        } else {
            inputSlots = this.anvilInputSlots;
            type = null;
            setOutput = stack -> this.setAnvilOutput();
        }
        for (int x = 0; x < 9; x++) {
            var stack = ItemStack.EMPTY;
            if (x < inputSlots.length) {
                stack = inputSlots[x].getItem();
            }
            if (!ItemStack.isSameItemSameTags(stack, this.recipeTestContainer.getItem(x))) {
                hasChanged = true;
                this.recipeTestContainer.setItem(x, stack.copy());
            }
        }
        if (!hasChanged) {
            return;
        }
        var level = this.getPlayerInventory().player.level();
        if (type != null) {
            if (type == RecipeType.STONECUTTING) {
                var recipes = level.getRecipeManager().getRecipesFor((RecipeType<Recipe<Container>>) type, this.recipeTestContainer, level);
                this.stonecutterRecipes = new StonecutterRecipeList();
                recipes.forEach(recipe -> this.stonecutterRecipes.recipes.add(recipe.getId()));
                if (this.selectedStonecutterRecipe != null) {
                    this.currentRecipe = recipes.stream().filter(r -> r.getId().equals(this.selectedStonecutterRecipe)).findAny().orElse(null);
                } else {
                    this.currentRecipe = null;
                }
            } else {
                this.currentRecipe = level.getRecipeManager().getRecipeFor((RecipeType<Recipe<Container>>) type, this.recipeTestContainer, level).orElse(null);
            }
        } else {
            this.currentRecipe = null;
        }
        if (this.currentRecipe == null) {
            setOutput.accept(ItemStack.EMPTY);
        } else {
            setOutput.accept(this.currentRecipe.assemble(this.recipeTestContainer, level.registryAccess()));
        }
    }

    @SuppressWarnings("deprecation")
    public static void initXPFluid() {
        XP_FLUIDS = new ArrayList<>();
        BuiltInRegistries.FLUID.getTagOrEmpty(EPPTags.XP_LIQUID).forEach(h -> XP_FLUIDS.add(h.get()));
        BuiltInRegistries.FLUID.getTagOrEmpty(EPPTags.EXPERIENCE).forEach(h -> XP_FLUIDS.add(h.get()));
    }

    @Override
    public boolean hasIngredient(Ingredient ingredient, Object2IntOpenHashMap<Object> reservedAmounts) {
        var slots = switch (this.currentMode) {
            case CRAFTING -> this.craftingInputSlots;
            case SMITHING -> this.smithingInputSlots;
            case STONECUTTER -> this.stonecutterInputSlots;
            case ANVIL -> this.anvilInputSlots;
        };
        for (var slot : slots) {
            var stackInSlot = slot.getItem();
            if (!stackInSlot.isEmpty() && ingredient.test(stackInSlot)) {
                var reservedAmount = reservedAmounts.getOrDefault(slot, 0);
                if (stackInSlot.getCount() > reservedAmount) {
                    reservedAmounts.merge(slot, 1, Integer::sum);
                    return true;
                }
            }
        }
        return super.hasIngredient(ingredient, reservedAmounts);
    }

    public CraftingTermMenu.MissingIngredientSlots findMissingIngredients(Map<Integer, Ingredient> ingredients) {

        // Try to figure out if any slots have missing ingredients
        // Find every "slot" (in JEI parlance) that has no equivalent item in the item repo or player inventory
        Set<Integer> missingSlots = new HashSet<>(); // missing but not craftable
        Set<Integer> craftableSlots = new HashSet<>(); // missing but craftable
        // We need to track how many of a given item stack we've already used for other slots in the recipe.
        // Otherwise, recipes that need 4x<item> will not correctly show missing items if at least 1 of <item> is in
        // the grid.
        var reservedGridAmounts = new Object2IntOpenHashMap<>();
        var playerItems = this.getPlayerInventory().items;
        var reservedPlayerItems = new int[playerItems.size()];

        for (var entry : ingredients.entrySet()) {
            var ingredient = entry.getValue();

            boolean found = false;
            // Player inventory is cheaper to check
            for (int i = 0; i < playerItems.size(); i++) {
                // Do not consider locked slots
                if (isPlayerInventorySlotLocked(i)) {
                    continue;
                }

                var stack = playerItems.get(i);
                if (stack.getCount() - reservedPlayerItems[i] > 0 && ingredient.test(stack)) {
                    reservedPlayerItems[i]++;
                    found = true;
                    break;
                }
            }

            // Then check the terminal screen's repository of network items
            if (!found) {
                // We use AE stacks to get an easily comparable item type key that ignores stack size
                if (hasIngredient(ingredient, reservedGridAmounts)) {
                    reservedGridAmounts.merge(ingredient, 1, Integer::sum);
                    found = true;
                }
            }

            // Check the terminal once again, but this time for craftable items
            if (!found) {
                for (var stack : ingredient.getItems()) {
                    if (isCraftable(stack)) {
                        craftableSlots.add(entry.getKey());
                        found = true;
                        break;
                    }
                }
            }

            if (!found) {
                missingSlots.add(entry.getKey());
            }
        }

        return new CraftingTermMenu.MissingIngredientSlots(missingSlots, craftableSlots);
    }

    protected boolean isCraftable(ItemStack itemStack) {
        var clientRepo = getClientRepo();
        if (clientRepo != null) {
            for (var stack : clientRepo.getAllEntries()) {
                if (AEItemKey.matches(stack.getWhat(), itemStack) && stack.isCraftable()) {
                    return true;
                }
            }
        }
        return false;
    }

    public record StonecutterRecipeList(List<ResourceLocation> recipes) implements PacketWritable {

        public StonecutterRecipeList() {
            this(new ArrayList<>());
        }

        @SuppressWarnings("unused")
        public StonecutterRecipeList(FriendlyByteBuf data) {
            this();
            int size = data.readInt();
            while (size > 0) {
                size--;
                this.recipes.add(data.readResourceLocation());
            }
        }

        @Override
        public void writeToPacket(FriendlyByteBuf data) {
            data.writeInt(this.recipes.size());
            for (ResourceLocation recipe : this.recipes) {
                data.writeResourceLocation(recipe);
            }
        }

        @Override
        public int hashCode() {
            return this.recipes.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof StonecutterRecipeList that) {
                return that.recipes.equals(this.recipes);
            }
            return false;
        }

    }

}
