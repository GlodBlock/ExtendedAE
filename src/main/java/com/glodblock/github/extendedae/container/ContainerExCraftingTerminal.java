package com.glodblock.github.extendedae.container;

import appeng.api.config.Actionable;
import appeng.api.inventories.InternalInventory;
import appeng.api.stacks.AEFluidKey;
import appeng.helpers.IMenuCraftingPacket;
import appeng.helpers.InventoryAction;
import appeng.menu.SlotSemantics;
import appeng.menu.guisync.GuiSync;
import appeng.menu.guisync.PacketWritable;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.me.common.MEStorageMenu;
import appeng.menu.slot.CraftingMatrixSlot;
import appeng.util.inv.PlayerInternalInventory;
import com.glodblock.github.extendedae.api.CraftingMode;
import com.glodblock.github.extendedae.client.ExSemantics;
import com.glodblock.github.extendedae.client.gui.widget.OutputResultSlot;
import com.glodblock.github.extendedae.common.parts.PartExCraftingTerminal;
import com.glodblock.github.extendedae.util.EPPTags;
import com.glodblock.github.glodium.network.packet.sync.IActionHolder;
import com.glodblock.github.glodium.network.packet.sync.Paras;
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
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    @GuiSync(0)
    public CraftingMode currentMode;
    @GuiSync(1)
    public int selectedStonecutterRecipe;
    @GuiSync(2)
    public String itemName = "";
    @GuiSync(3)
    public int anvilMaterialCost;
    @GuiSync(4)
    public int anvilCost;
    @GuiSync(5)
    public StonecutterRecipeList stonecutterRecipes = new StonecutterRecipeList();

    public ContainerExCraftingTerminal(int id, Inventory playerInventory, PartExCraftingTerminal host) {
        super(TYPE, id, playerInventory, host, true);
        this.host = host;
        this.currentMode = host.getCurrentMode();
        this.setupCraftingSlots();
        this.setupStonecutterSlots();
        this.setupSmithingSlots();
        this.setupAnvilSlots();
        this.updateCurrentRecipeAndOutput(true);
        this.actions.put("set_mode", o -> this.setMode(o.get(0)));
        this.actions.put("stonecutter_select", o -> this.selectStonecutterRecipe(o.get(0)));
        this.actions.put("craft_clearCraftingGrid", o -> this.clearCraftingGrid(this.craftingInputSlots[0].index));
        this.actions.put("craft_clearToPlayerInv", o -> this.clearToPlayerInventory(PartExCraftingTerminal.INV_CRAFTING));
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
                    return ContainerExCraftingTerminal.this.getAndPerformAnvilCraft(p, false);
                }

            }, ExSemantics.EX_4);
        }
    }

    private void selectStonecutterRecipe(int index) {
        this.selectedStonecutterRecipe = index;
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
                            int newLevel = leftLevel == rightLevel ? leftLevel + 1 : Math.max(leftLevel, rightLevel);
                            newLevel = Math.min(newLevel, enchant.getMaxLevel());
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
                                int rarityCost = switch (enchant.getRarity()) {
                                    case COMMON -> 1;
                                    case UNCOMMON -> 2;
                                    case RARE -> 4;
                                    case VERY_RARE -> 8;
                                };
                                baseCost += newLevel * rarityCost;
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
                        int newLevel = leftLevel == bookLevel ? leftLevel + 1 : Math.max(leftLevel, bookLevel);
                        newLevel = Math.min(newLevel, enchant.getMaxLevel());

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
                            int rarityCost = switch (enchant.getRarity()) {
                                case COMMON -> 1;
                                case UNCOMMON -> 2;
                                case RARE -> 4;
                                case VERY_RARE -> 8;
                            };
                            baseCost += newLevel * rarityCost;
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
        if (result.getBaseRepairCost() < repairCost) {
            result.setRepairCost(repairCost);
        }
        this.anvilOutputSlot.set(result);
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
        long xpNeeded = levelsRequired * 7L;
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

    @NotNull
    @Override
    public Map<String, Consumer<Paras>> getActionMap() {
        return this.actions;
    }

    @Override
    public InternalInventory getCraftingMatrix() {
        return this.host.getSubInventory(PartExCraftingTerminal.INV_CRAFTING);
    }

    @Override
    public boolean useRealItems() {
        return true;
    }

    public @Nullable Recipe<Container> getCurrentRecipe() {
        return this.currentRecipe;
    }

    public void clearToPlayerInventory(ResourceLocation id) {
        var craftingGridInv = this.host.getSubInventory(id);
        var playerInv = new PlayerInternalInventory(getPlayerInventory());
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

    public void clearCraftingGrid(int slotIndex) {
        if (this.getPlayer() instanceof ServerPlayer sp) {
            this.doAction(sp, InventoryAction.MOVE_REGION, slotIndex, 0);
        }
    }

    public void clearAllGrid() {
        this.clearCraftingGrid(this.craftingInputSlots[0].index);
        this.clearCraftingGrid(this.stonecutterInputSlots[0].index);
        this.clearCraftingGrid(this.smithingInputSlots[0].index);
        this.clearCraftingGrid(this.anvilInputSlots[0].index);
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
                if (recipes.size() > this.selectedStonecutterRecipe) {
                    this.currentRecipe = recipes.get(this.selectedStonecutterRecipe);
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
