package com.glodblock.github.extendedae.datagen;

import appeng.api.util.AEColor;
import appeng.core.ConventionTags;
import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.core.definitions.AEParts;
import appeng.recipes.handlers.InscriberProcessType;
import appeng.recipes.handlers.InscriberRecipeBuilder;
import appeng.recipes.transform.TransformCircumstance;
import appeng.recipes.transform.TransformRecipeBuilder;
import com.glodblock.github.appflux.common.AFSingletons;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.config.ConfigCondition;
import com.glodblock.github.extendedae.recipe.CircuitCutterRecipeBuilder;
import com.glodblock.github.extendedae.recipe.CrystalAssemblerRecipeBuilder;
import com.glodblock.github.extendedae.recipe.CrystalFixerRecipeBuilder;
import com.glodblock.github.extendedae.recipe.PulverizerRecipeBuilder;
import com.glodblock.github.extendedae.util.EAETags;
import com.glodblock.github.extendedae.xmod.ModConstants;
import com.glodblock.github.glodium.util.GlodUtil;

import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.references.BlockIds;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.Tags.BlockItems;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.conditions.NotCondition;
import net.neoforged.neoforge.common.conditions.TagEmptyCondition;
import org.jetbrains.annotations.NotNull;

public class EAERecipeProvider extends RecipeProvider {

    static String C = "has_item";
    protected final HolderGetter<@NotNull Fluid> fluids;

    public EAERecipeProvider(
                    BootstrapContext<Recipe<?>> recipeOutput, BootstrapContext<Advancement> advancementOutput) {
        super(recipeOutput, advancementOutput);
        this.fluids = recipeOutput.lookup(Registries.FLUID);
    }

    @Override
    protected void buildRecipes() {
        var quartzBlock = Ingredient.of(Blocks.CHISELED_QUARTZ_BLOCK, Blocks.QUARTZ_BLOCK, Blocks.QUARTZ_PILLAR);
        // Extended Pattern Provider
        CrystalAssemblerRecipeBuilder
                .assemble(EAESingletons.EX_PATTERN_PROVIDER, this.items, this.fluids)
                .input(ConventionTags.PATTERN_PROVIDER)
                .input(AEItems.CAPACITY_CARD, 3)
                .input(Blocks.CRAFTING_TABLE, 3)
                .input(EAESingletons.CONCURRENT_PROCESSOR)
                .input(ConventionTags.GLASS_CABLE, 6)
                .power(10000)
                .save(this.output, ExtendedAE.id("assembler/ex_pattern_provider"));
        ShapelessRecipeBuilder
                .shapeless(this.items, RecipeCategory.MISC, EAESingletons.PATTERN_PROVIDER_UPGRADE)
                .requires(EAETags.EX_PATTERN_PROVIDER)
                .requires(Tags.Items.INGOTS)
                .unlockedBy(C, has(EAETags.EX_PATTERN_PROVIDER))
                .save(this.output);
        ShapelessRecipeBuilder
                .shapeless(this.items, RecipeCategory.MISC, EAESingletons.EX_PATTERN_PROVIDER_PART)
                .requires(EAESingletons.EX_PATTERN_PROVIDER)
                .unlockedBy(C, has(EAESingletons.EX_PATTERN_PROVIDER_PART))
                .save(this.output);
        ShapelessRecipeBuilder
                .shapeless(this.items, RecipeCategory.MISC, EAESingletons.EX_PATTERN_PROVIDER)
                .requires(EAESingletons.EX_PATTERN_PROVIDER_PART)
                .unlockedBy(C, has(EAESingletons.EX_PATTERN_PROVIDER))
                .save(this.output);

        // Extended Interface
        CrystalAssemblerRecipeBuilder
                .assemble(EAESingletons.EX_INTERFACE, this.items, this.fluids)
                .input(ConventionTags.INTERFACE)
                .input(AEItems.CAPACITY_CARD, 3)
                .input(ConventionTags.GLASS, 3)
                .input(EAESingletons.CONCURRENT_PROCESSOR)
                .input(ConventionTags.GLASS_CABLE, 6)
                .power(10000)
                .save(this.output, ExtendedAE.id("assembler/ex_interface"));
        ShapelessRecipeBuilder
                .shapeless(this.items, RecipeCategory.MISC, EAESingletons.INTERFACE_UPGRADE)
                .requires(EAETags.EX_INTERFACE)
                .requires(Tags.Items.INGOTS)
                .unlockedBy(C, has(EAETags.EX_INTERFACE))
                .save(this.output);
        ShapelessRecipeBuilder
                .shapeless(this.items, RecipeCategory.MISC, EAESingletons.EX_INTERFACE_PART)
                .requires(EAESingletons.EX_INTERFACE)
                .unlockedBy(C, has(EAESingletons.EX_INTERFACE_PART))
                .save(this.output);
        ShapelessRecipeBuilder
                .shapeless(this.items, RecipeCategory.MISC, EAESingletons.EX_INTERFACE)
                .requires(EAESingletons.EX_INTERFACE_PART)
                .unlockedBy(C, has(EAESingletons.EX_INTERFACE_PART))
                .save(this.output);

        // Infinity Cell
        ShapedRecipeBuilder
                .shaped(this.items, RecipeCategory.MISC, EAESingletons.INFINITY_WATER_CELL)
                .pattern("CWC")
                .pattern("WXW")
                .pattern("III")
                .define('C', AEBlocks.QUARTZ_GLASS)
                .define('W', Items.WATER_BUCKET)
                .define('X', AEItems.CELL_COMPONENT_16K)
                .define('I', ConventionTags.DIAMOND)
                .unlockedBy(C, has(EAESingletons.INFINITY_WATER_CELL))
                .save(this.output);
        ShapedRecipeBuilder
                .shaped(this.items, RecipeCategory.MISC, EAESingletons.INFINITY_COBBLESTONE_CELL)
                .pattern("CLC")
                .pattern("WXW")
                .pattern("III")
                .define('C', AEBlocks.QUARTZ_GLASS)
                .define('L', Items.LAVA_BUCKET)
                .define('W', Items.WATER_BUCKET)
                .define('X', AEItems.CELL_COMPONENT_16K)
                .define('I', ConventionTags.DIAMOND)
                .unlockedBy(C, has(EAESingletons.INFINITY_COBBLESTONE_CELL))
                .save(this.output);

        // Extended IO Bus
        CrystalAssemblerRecipeBuilder
                .assemble(EAESingletons.EX_EXPORT_BUS, this.items, this.fluids)
                .input(AEParts.EXPORT_BUS)
                .input(AEItems.SPEED_CARD, 3)
                .input(Blocks.PISTON, 2)
                .input(AEItems.FORMATION_CORE)
                .power(7000)
                .save(this.output, ExtendedAE.id("assembler/ex_export_bus"));
        CrystalAssemblerRecipeBuilder
                .assemble(EAESingletons.EX_IMPORT_BUS, this.items, this.fluids)
                .input(AEParts.IMPORT_BUS)
                .input(AEItems.SPEED_CARD, 3)
                .input(Blocks.PISTON, 2)
                .input(AEItems.ANNIHILATION_CORE)
                .power(7000)
                .save(this.output, ExtendedAE.id("assembler/ex_import_bus"));
        ShapelessRecipeBuilder
                .shapeless(this.items, RecipeCategory.MISC, EAESingletons.IO_BUS_UPGRADE)
                .requires(EAESingletons.EX_IMPORT_BUS)
                .requires(Tags.Items.INGOTS)
                .unlockedBy(C, has(EAESingletons.EX_IMPORT_BUS))
                .save(this.output, ExtendedAE.stringId("ex_bus_upgrade_in"));
        ShapelessRecipeBuilder
                .shapeless(this.items, RecipeCategory.MISC, EAESingletons.IO_BUS_UPGRADE)
                .requires(EAESingletons.EX_EXPORT_BUS)
                .requires(Tags.Items.INGOTS)
                .unlockedBy(C, has(EAESingletons.EX_EXPORT_BUS))
                .save(this.output, ExtendedAE.stringId("ex_bus_upgrade_out"));

        // Extended Pattern Access Terminal
        CrystalAssemblerRecipeBuilder
                .assemble(EAESingletons.EX_PATTERN_TERMINAL, this.items, this.fluids)
                .input(AEParts.PATTERN_ACCESS_TERMINAL)
                .input(Blocks.REDSTONE_LAMP)
                .input(AEItems.LOGIC_PROCESSOR, 2)
                .input(AEBlocks.QUARTZ_FIXTURE, 4)
                .power(1000)
                .save(this.output, ExtendedAE.id("assembler/ex_pattern_access_terminal"));
        ShapelessRecipeBuilder
                .shapeless(this.items, RecipeCategory.MISC, EAESingletons.PATTERN_UPGRADE)
                .requires(EAESingletons.EX_PATTERN_TERMINAL)
                .requires(Tags.Items.INGOTS)
                .unlockedBy(C, has(EAESingletons.EX_PATTERN_TERMINAL))
                .save(this.output, ExtendedAE.stringId("ex_pattern_access_terminal_upgrade"));

        // ME Packing Tape
        ShapedRecipeBuilder
                .shaped(this.items, RecipeCategory.MISC, EAESingletons.PACKING_TAPE)
                .pattern("FG ")
                .pattern("PIP")
                .pattern(" GF")
                .define('I', ConventionTags.IRON_INGOT)
                .define('P', Items.PAPER)
                .define('G', Items.SLIME_BALL)
                .define('F', ConventionTags.FLUIX_DUST)
                .unlockedBy(C, has(EAESingletons.PACKING_TAPE))
                .save(this.output);

        // Wireless Connector
        CrystalAssemblerRecipeBuilder
                .assemble(EAESingletons.WIRELESS_CONNECTOR, 2, this.items, this.fluids)
                .input(EAESingletons.MACHINE_FRAME)
                .input(ConventionTags.SMART_DENSE_CABLE, 2)
                .input(AEItems.WIRELESS_RECEIVER, 2)
                .input(AEItems.WIRELESS_BOOSTER, 3)
                .power(50000)
                .save(this.output, ExtendedAE.id("assembler/wireless_connector"));
        CrystalAssemblerRecipeBuilder
                .assemble(EAESingletons.WIRELESS_TOOL, this.items, this.fluids)
                .input(AEItems.WIRELESS_RECEIVER)
                .input(ConventionTags.IRON_INGOT, 2)
                .input(AEItems.CALCULATION_PROCESSOR)
                .input(AEItems.SINGULARITY)
                .power(10000)
                .save(this.output, ExtendedAE.id("assembler/wireless_kit"));

        // Wireless Hub
        CrystalAssemblerRecipeBuilder
                .assemble(EAESingletons.WIRELESS_HUB, this.items, this.fluids)
                .input(EAESingletons.WIRELESS_CONNECTOR)
                .input(ConventionTags.SMART_CABLE, 8)
                .input(EAESingletons.CONCURRENT_PROCESSOR, 4)
                .input(AEBlocks.QUANTUM_LINK)
                .power(50000)
                .save(this.output, ExtendedAE.id("assembler/wireless_hub"));
        ShapelessRecipeBuilder
                .shapeless(this.items, RecipeCategory.MISC, EAESingletons.WIRELESS_CONNECTOR_UPGRADE)
                .requires(EAESingletons.WIRELESS_HUB)
                .requires(Tags.Items.INGOTS)
                .unlockedBy(C, has(EAESingletons.WIRELESS_HUB))
                .save(this.output);

        // Ingredient Buffer
        ShapedRecipeBuilder
                .shaped(this.items, RecipeCategory.MISC, EAESingletons.INGREDIENT_BUFFER)
                .pattern("IKI")
                .pattern("G G")
                .pattern("IKI")
                .define('I', ConventionTags.IRON_INGOT)
                .define('K', AEItems.CELL_COMPONENT_1K)
                .define('G', AEBlocks.QUARTZ_GLASS)
                .unlockedBy(C, has(EAESingletons.INGREDIENT_BUFFER))
                .save(this.output);

        // Extended ME Drive
        CrystalAssemblerRecipeBuilder
                .assemble(EAESingletons.EX_DRIVE, this.items, this.fluids)
                .input(AEBlocks.DRIVE)
                .input(ConventionTags.GLASS_CABLE, 2)
                .input(AEItems.CAPACITY_CARD)
                .input(EAESingletons.CONCURRENT_PROCESSOR)
                .power(20000)
                .save(this.output, ExtendedAE.id("assembler/ex_drive"));
        ShapelessRecipeBuilder
                .shapeless(this.items, RecipeCategory.MISC, EAESingletons.DRIVE_UPGRADE)
                .requires(EAESingletons.EX_DRIVE)
                .requires(Tags.Items.INGOTS)
                .unlockedBy(C, has(EAESingletons.EX_DRIVE))
                .save(this.output);

        // Pattern Modifier
        ShapedRecipeBuilder
                .shaped(this.items, RecipeCategory.MISC, EAESingletons.PATTERN_MODIFIER)
                .pattern("GPG")
                .pattern(" L ")
                .define('G', ConventionTags.dye(DyeColor.GREEN))
                .define('P', AEItems.BLANK_PATTERN)
                .define('L', AEItems.LOGIC_PROCESSOR)
                .unlockedBy(C, has(EAESingletons.PATTERN_MODIFIER))
                .save(this.output);

        // Extended Molecular Assembler
        CrystalAssemblerRecipeBuilder
                .assemble(EAESingletons.EX_ASSEMBLER, this.items, this.fluids)
                .input(AEBlocks.MOLECULAR_ASSEMBLER, 4)
                .input(EAESingletons.CONCURRENT_PROCESSOR, 4)
                .input(ConventionTags.FLUIX_DUST, 4)
                .input(AEItems.ENGINEERING_PROCESSOR, 3)
                .input(AEItems.SPEED_CARD)
                .power(20000)
                .save(this.output, ExtendedAE.id("assembler/ex_molecular_assembler"));

        // Extended Charger
        CrystalAssemblerRecipeBuilder
                .assemble(EAESingletons.EX_CHARGER, this.items, this.fluids)
                .input(AEBlocks.CHARGER)
                .input(AEItems.CAPACITY_CARD)
                .input(EAESingletons.CONCURRENT_PROCESSOR)
                .power(10000)
                .save(this.output, ExtendedAE.id("assembler/ex_charger"));

        // Tag Storage Bus
        ShapedRecipeBuilder
                .shaped(this.items, RecipeCategory.MISC, EAESingletons.TAG_STORAGE_BUS)
                .pattern(" L ")
                .pattern("RBR")
                .pattern(" K ")
                .define('L', AEItems.LOGIC_PROCESSOR)
                .define('R', ConventionTags.REDSTONE)
                .define('B', AEParts.STORAGE_BUS)
                .define('K', Items.BOOK)
                .unlockedBy(C, has(EAESingletons.TAG_STORAGE_BUS))
                .save(this.output);

        // Tag Export Bus
        ShapedRecipeBuilder
                .shaped(this.items, RecipeCategory.MISC, EAESingletons.TAG_EXPORT_BUS)
                .pattern(" L ")
                .pattern("RBR")
                .pattern(" K ")
                .define('L', AEItems.LOGIC_PROCESSOR)
                .define('R', ConventionTags.REDSTONE)
                .define('B', AEParts.EXPORT_BUS)
                .define('K', Items.BOOK)
                .unlockedBy(C, has(EAESingletons.TAG_EXPORT_BUS))
                .save(this.output);

        // Threshold Level Emitter
        ShapedRecipeBuilder
                .shaped(this.items, RecipeCategory.MISC, EAESingletons.THRESHOLD_LEVEL_EMITTER)
                .pattern("RER")
                .pattern(" C ")
                .define('R', Items.REDSTONE_TORCH)
                .define('E', AEParts.LEVEL_EMITTER)
                .define('C', AEItems.CALCULATION_PROCESSOR)
                .unlockedBy(C, has(EAESingletons.THRESHOLD_LEVEL_EMITTER))
                .save(this.output);

        // Mod Storage Bus
        ShapedRecipeBuilder
                .shaped(this.items, RecipeCategory.MISC, EAESingletons.MOD_STORAGE_BUS)
                .pattern(" C ")
                .pattern("RBR")
                .pattern(" K ")
                .define('C', AEItems.CALCULATION_PROCESSOR)
                .define('R', ConventionTags.REDSTONE)
                .define('B', AEParts.STORAGE_BUS)
                .define('K', Items.BOOK)
                .unlockedBy(C, has(EAESingletons.MOD_STORAGE_BUS))
                .save(this.output);

        // Mod Export Bus
        ShapedRecipeBuilder
                .shaped(this.items, RecipeCategory.MISC, EAESingletons.MOD_EXPORT_BUS)
                .pattern(" C ")
                .pattern("RBR")
                .pattern(" K ")
                .define('C', AEItems.CALCULATION_PROCESSOR)
                .define('R', ConventionTags.REDSTONE)
                .define('B', AEParts.EXPORT_BUS)
                .define('K', Items.BOOK)
                .unlockedBy(C, has(EAESingletons.MOD_EXPORT_BUS))
                .save(this.output);

        // Active Formation Plane
        CrystalAssemblerRecipeBuilder
                .assemble(EAESingletons.ACTIVE_FORMATION_PLANE, this.items, this.fluids)
                .input(AEParts.FORMATION_PLANE)
                .input(Blocks.PISTON, 3)
                .input(AEItems.FORMATION_CORE, 2)
                .input(EAESingletons.EX_EXPORT_BUS)
                .power(20000)
                .save(this.output, ExtendedAE.id("assembler/active_formation_plane"));

        // Smart Annihilation Plane
        CrystalAssemblerRecipeBuilder
                .assemble(EAESingletons.SMART_ANNIHILATION_PLANE, this.items, this.fluids)
                .input(AEParts.ANNIHILATION_PLANE)
                .input(Blocks.PISTON, 3)
                .input(AEItems.ANNIHILATION_CORE, 2)
                .input(EAESingletons.EX_IMPORT_BUS)
                .power(20000)
                .save(this.output, ExtendedAE.id("assembler/smart_annihilation_plane"));

        // ME Caner
        CrystalAssemblerRecipeBuilder
                .assemble(EAESingletons.CANER, this.items, this.fluids)
                .input(EAESingletons.MACHINE_FRAME)
                .input(EAESingletons.INGREDIENT_BUFFER)
                .input(AEItems.FORMATION_CORE)
                .input(AEItems.ANNIHILATION_CORE)
                .power(10000)
                .save(this.output, ExtendedAE.id("assembler/caner"));

        // ME Precise Export Bus
        ShapedRecipeBuilder
                .shaped(this.items, RecipeCategory.MISC, EAESingletons.PRECISE_EXPORT_BUS)
                .pattern("PBP")
                .define('B', EAESingletons.EX_EXPORT_BUS)
                .define('P', AEItems.CALCULATION_PROCESSOR)
                .unlockedBy(C, has(EAESingletons.EX_EXPORT_BUS))
                .save(this.output);

        // Wireless Ex PAT
        ShapedRecipeBuilder
                .shaped(this.items, RecipeCategory.MISC, EAESingletons.WIRELESS_EX_PAT)
                .pattern("A")
                .pattern("B")
                .pattern("C")
                .define('A', AEItems.WIRELESS_RECEIVER)
                .define('B', EAESingletons.EX_PATTERN_TERMINAL)
                .define('C', AEBlocks.DENSE_ENERGY_CELL)
                .unlockedBy(C, has(EAESingletons.EX_PATTERN_TERMINAL))
                .save(this.output);

        // Extended IO Port
        CrystalAssemblerRecipeBuilder
                .assemble(EAESingletons.EX_IO_PORT, this.items, this.fluids)
                .input(AEBlocks.IO_PORT)
                .input(AEItems.SPEED_CARD, 4)
                .input(AEItems.LOGIC_PROCESSOR, 3)
                .input(EAESingletons.CONCURRENT_PROCESSOR, 2)
                .power(80000)
                .save(this.output, ExtendedAE.id("assembler/ex_io_port"));

        // Crystal Fixer
        CrystalAssemblerRecipeBuilder
                .assemble(EAESingletons.CRYSTAL_FIXER, this.items, this.fluids)
                .input(EAESingletons.MACHINE_FRAME)
                .input(ConventionTags.ALL_CERTUS_QUARTZ, 4)
                .input(AEParts.QUARTZ_FIBER, 2)
                .power(10000)
                .save(this.output, ExtendedAE.id("assembler/crystal_fixer"));

        // Precise Storage Bus
        ShapedRecipeBuilder
                .shaped(this.items, RecipeCategory.MISC, EAESingletons.PRECISE_STORAGE_BUS)
                .pattern("PBP")
                .define('B', AEParts.STORAGE_BUS)
                .define('P', AEItems.CALCULATION_PROCESSOR)
                .unlockedBy(C, has(AEParts.STORAGE_BUS))
                .save(this.output);

        // Threshold Export Bus
        ShapedRecipeBuilder
                .shaped(this.items, RecipeCategory.MISC, EAESingletons.THRESHOLD_EXPORT_BUS)
                .pattern("LCE")
                .define('L', AEParts.LEVEL_EMITTER)
                .define('C', AEItems.LOGIC_PROCESSOR)
                .define('E', AEParts.EXPORT_BUS)
                .unlockedBy(C, has(AEParts.LEVEL_EMITTER))
                .save(this.output);

        // Fishbig
        ShapedRecipeBuilder
                .shaped(this.items, RecipeCategory.MISC, EAESingletons.FISHBIG)
                .pattern("FFF")
                .pattern("F F")
                .pattern("FFF")
                .define('F', Items.PUFFERFISH)
                .unlockedBy(C, has(EAESingletons.FISHBIG))
                .save(this.output);

        // MDDyue
        ShapedRecipeBuilder
                .shaped(this.items, RecipeCategory.MISC, EAESingletons.MDDYUE)
                .pattern("MMM")
                .pattern("M M")
                .pattern("MMM")
                .define('M', Items.EGG)
                .unlockedBy(C, has(EAESingletons.MDDYUE))
                .save(this.output);

        // Entro Seed
        ShapelessRecipeBuilder
                .shapeless(this.items, RecipeCategory.MISC, EAESingletons.ENTRO_SEED, 2)
                .requires(BlockItems.SANDS.item())
                .requires(ConventionTags.ENDER_PEARL_DUST)
                .requires(ConventionTags.ENDER_PEARL_DUST)
                .requires(ConventionTags.ENDER_PEARL_DUST)
                .requires(ConventionTags.REDSTONE)
                .requires(ConventionTags.REDSTONE)
                .requires(ConventionTags.GLOWSTONE)
                .requires(ConventionTags.GLOWSTONE)
                .requires(AEItems.SKY_DUST)
                .unlockedBy(C, has(ConventionTags.ENDER_PEARL_DUST))
                .save(this.output);

        // Entro Dust
        InscriberRecipeBuilder
                .inscribe(Ingredient.of(this.items.getOrThrow(EAETags.ENTRO_CRYSTAL)), EAESingletons.ENTRO_DUST, 1)
                .setMode(InscriberProcessType.INSCRIBE)
                .save(this.output, ExtendedAE.id("inscriber/crush_entro"));

        // Entro Crystal
        TransformRecipeBuilder
                .transform(this.output,
                        ExtendedAE.id("transform/fix_entro"),
                        EAESingletons.ENTRO_CRYSTAL, 1,
                        TransformCircumstance.fluid(FluidTags.WATER),
                        Ingredient.of(this.items.getOrThrow(EAETags.ENTRO_DUST)),
                        Ingredient.of(AEItems.FLUIX_CRYSTAL)
                );

        // Entro Ingot
        TransformRecipeBuilder
                .transform(this.output,
                        ExtendedAE.id("transform/entro_ingot"),
                        EAESingletons.ENTRO_INGOT, 1,
                        TransformCircumstance.fluid(FluidTags.WATER),
                        Ingredient.of(this.items.getOrThrow(EAETags.ENTRO_DUST)),
                        Ingredient.of(this.items.getOrThrow(ConventionTags.GOLD_INGOT)),
                        Ingredient.of(Items.LAPIS_LAZULI)
                );

        // Machine Frame
        ShapedRecipeBuilder
                .shaped(this.items, RecipeCategory.MISC, EAESingletons.MACHINE_FRAME)
                .pattern("ECE")
                .pattern("IGI")
                .pattern("ECE")
                .define('E', EAETags.ENTRO_INGOT)
                .define('C', ConventionTags.COPPER_INGOT)
                .define('I', ConventionTags.IRON_INGOT)
                .define('G', AEBlocks.QUARTZ_GLASS)
                .unlockedBy(C, has(EAETags.ENTRO_INGOT))
                .save(this.output, ExtendedAE.stringId("machine_frame_normal"));

        ShapedRecipeBuilder
                .shaped(this.items, RecipeCategory.MISC, EAESingletons.MACHINE_FRAME)
                .pattern("EIE")
                .pattern("CGC")
                .pattern("EIE")
                .define('E', EAETags.ENTRO_INGOT)
                .define('C', ConventionTags.COPPER_INGOT)
                .define('I', ConventionTags.IRON_INGOT)
                .define('G', AEBlocks.QUARTZ_GLASS)
                .unlockedBy(C, has(EAETags.ENTRO_INGOT))
                .save(this.output, ExtendedAE.stringId("machine_frame_mirror"));

        // Crystal Assembler
        ShapedRecipeBuilder
                .shaped(this.items, RecipeCategory.MISC, EAESingletons.CRYSTAL_ASSEMBLER)
                .pattern(" T ")
                .pattern("LML")
                .pattern("CKC")
                .define('T', AEParts.CRAFTING_TERMINAL)
                .define('L', AEItems.LOGIC_PROCESSOR)
                .define('M', EAESingletons.MACHINE_FRAME)
                .define('C', ConventionTags.GLASS_CABLE)
                .define('K', AEBlocks.SKY_STONE_TANK)
                .unlockedBy(C, has(EAESingletons.MACHINE_FRAME))
                .save(this.output);

        // Concurrent Processor
        CrystalAssemblerRecipeBuilder
                .assemble(EAESingletons.CONCURRENT_PROCESSOR_PRESS, this.items, this.fluids)
                .input(Items.ENDER_EYE, 3)
                .input(EAETags.ENTRO_CRYSTAL, 4)
                .input(AEItems.SILICON_PRESS)
                .save(this.output, ExtendedAE.id("assembler/concurrent_press"));

        InscriberRecipeBuilder
                .inscribe(this.items.getOrThrow(EAETags.ENTRO_CRYSTAL), EAESingletons.CONCURRENT_PROCESSOR_PRINT, 1)
                .setTop(Ingredient.of(EAESingletons.CONCURRENT_PROCESSOR_PRESS))
                .setMode(InscriberProcessType.INSCRIBE)
                .save(this.output, ExtendedAE.id("inscriber/concurrent_print"));

        InscriberRecipeBuilder.inscribe(this.items.getOrThrow(ConventionTags.REDSTONE), EAESingletons.CONCURRENT_PROCESSOR, 1)
                .setTop(Ingredient.of(EAESingletons.CONCURRENT_PROCESSOR_PRINT))
                .setBottom(Ingredient.of(AEItems.SILICON_PRINT))
                .setMode(InscriberProcessType.PRESS)
                .save(this.output, ExtendedAE.id("inscriber/concurrent_process"));

        // Entro Block
        ShapedRecipeBuilder
                .shaped(this.items, RecipeCategory.MISC, EAESingletons.ENTRO_BLOCK)
                .pattern("CC")
                .pattern("CC")
                .define('C', EAETags.ENTRO_CRYSTAL)
                .unlockedBy(C, has(EAETags.ENTRO_CRYSTAL))
                .save(this.output, ExtendedAE.stringId("entro_compress"));

        ShapelessRecipeBuilder
                .shapeless(this.items, RecipeCategory.MISC, EAESingletons.ENTRO_CRYSTAL, 4)
                .requires(EAETags.ENTRO_BLOCK)
                .unlockedBy(C, has(EAETags.ENTRO_BLOCK))
                .save(this.output, ExtendedAE.stringId("entro_block_unpack"));

        // Budding Block
        CrystalAssemblerRecipeBuilder
                .assemble(EAESingletons.MOSTLY_ENTROIZED_FLUIX_BUDDING, this.items, this.fluids)
                .input(EAESingletons.ENTRO_SEED)
                .input(AEBlocks.FLUIX_BLOCK)
                .fluid(Fluids.WATER, 200)
                .power(10000)
                .save(this.output, ExtendedAE.id("assembler/budding"));

        // Silicon BLock
        ShapedRecipeBuilder
                .shaped(this.items, RecipeCategory.MISC, EAESingletons.SILICON_BLOCK)
                .pattern("SSS")
                .pattern("S#S")
                .pattern("SSS")
                .define('S', ConventionTags.SILICON)
                .define('#', AEItems.SILICON)
                .unlockedBy(C, has(AEItems.SILICON))
                .save(this.output, ExtendedAE.stringId("silicon_compress"));
        ShapelessRecipeBuilder
                .shapeless(this.items, RecipeCategory.MISC, AEItems.SILICON, 9)
                .requires(EAESingletons.SILICON_BLOCK)
                .unlockedBy(C, has(EAESingletons.SILICON_BLOCK))
                .save(this.output, ExtendedAE.stringId("silicon_decompress"));

        // Circuit Cutter
        CrystalAssemblerRecipeBuilder
                .assemble(EAESingletons.CIRCUIT_CUTTER, this.items, this.fluids)
                .input(EAESingletons.MACHINE_FRAME)
                .input(AEItems.ENGINEERING_PROCESSOR, 8)
                .input(AEItems.CALCULATION_PROCESSOR_PRESS)
                .input(AEItems.ENGINEERING_PROCESSOR_PRESS)
                .input(AEItems.LOGIC_PROCESSOR_PRESS)
                .input(AEItems.SILICON_PRESS)
                .input(EAESingletons.CONCURRENT_PROCESSOR_PRESS)
                .input(Blocks.STONECUTTER)
                .power(50000)
                .save(this.output, ExtendedAE.id("assembler/circuit_cutter"));

        // Oversize Interface
        CrystalAssemblerRecipeBuilder
                .assemble(EAESingletons.OVERSIZE_INTERFACE, this.items, this.fluids)
                .input(EAETags.EX_INTERFACE)
                .input(EAESingletons.INGREDIENT_BUFFER)
                .input(EAESingletons.CONCURRENT_PROCESSOR, 2)
                .input(AEItems.ANNIHILATION_CORE, 2)
                .input(AEItems.FORMATION_CORE, 2)
                .power(50000)
                .save(this.output, ExtendedAE.id("assembler/oversize_interface"));
        ShapelessRecipeBuilder
                .shapeless(this.items, RecipeCategory.MISC, EAESingletons.OVERSIZE_INTERFACE_PART)
                .requires(EAESingletons.OVERSIZE_INTERFACE)
                .unlockedBy(C, has(EAESingletons.OVERSIZE_INTERFACE))
                .save(this.output);
        ShapelessRecipeBuilder
                .shapeless(this.items, RecipeCategory.MISC, EAESingletons.OVERSIZE_INTERFACE)
                .requires(EAESingletons.OVERSIZE_INTERFACE_PART)
                .unlockedBy(C, has(EAESingletons.OVERSIZE_INTERFACE_PART))
                .save(this.output);

        // Entro Shard
        ShapedRecipeBuilder
                .shaped(this.items, RecipeCategory.MISC, EAESingletons.ENTRO_CRYSTAL)
                .pattern("SSS")
                .pattern("S S")
                .pattern("SSS")
                .define('S', EAESingletons.ENTRO_SHARD)
                .unlockedBy(C, has(EAESingletons.ENTRO_SHARD))
                .save(this.output, ExtendedAE.stringId("entro_recycle"));

        // Assembler Matrix
        ShapedRecipeBuilder
                .shaped(this.items, RecipeCategory.MISC, EAESingletons.ASSEMBLER_MATRIX_FRAME)
                .pattern("QLQ")
                .pattern("LML")
                .pattern("QLQ")
                .define('Q', ConventionTags.NETHER_QUARTZ)
                .define('L', Items.LAPIS_LAZULI)
                .define('M', EAESingletons.MACHINE_FRAME)
                .unlockedBy(C, has(EAESingletons.MACHINE_FRAME))
                .save(this.output);
        ShapedRecipeBuilder
                .shaped(this.items, RecipeCategory.MISC, EAESingletons.ASSEMBLER_MATRIX_WALL, 2)
                .pattern("BFB")
                .pattern("IQI")
                .pattern("BFB")
                .define('B', Items.IRON_BARS)
                .define('F', ConventionTags.FLUIX_CRYSTAL)
                .define('I', EAETags.ENTRO_INGOT)
                .define('Q', quartzBlock)
                .unlockedBy(C, has(EAESingletons.ENTRO_INGOT))
                .save(this.output);
        ShapedRecipeBuilder
                .shaped(this.items, RecipeCategory.MISC, EAESingletons.ASSEMBLER_MATRIX_GLASS, 2)
                .pattern("BFB")
                .pattern("IQI")
                .pattern("BFB")
                .define('B', AEBlocks.QUARTZ_GLASS)
                .define('F', ConventionTags.FLUIX_CRYSTAL)
                .define('I', EAETags.ENTRO_INGOT)
                .define('Q', quartzBlock)
                .unlockedBy(C, has(EAESingletons.ENTRO_INGOT))
                .save(this.output);
        CrystalAssemblerRecipeBuilder
                .assemble(EAESingletons.ASSEMBLER_MATRIX_CRAFTER, this.items, this.fluids)
                .input(EAESingletons.ASSEMBLER_MATRIX_WALL)
                .input(EAESingletons.EX_ASSEMBLER)
                .input(AEItems.COLORED_LUMEN_PAINT_BALL.item(AEColor.PURPLE), 6)
                .input(AEItems.LOGIC_PROCESSOR)
                .power(50000)
                .save(this.output, ExtendedAE.id("assembler/assembler_matrix_crafter"));
        CrystalAssemblerRecipeBuilder
                .assemble(EAESingletons.ASSEMBLER_MATRIX_PATTERN, this.items, this.fluids)
                .input(EAESingletons.ASSEMBLER_MATRIX_WALL)
                .input(EAETags.EX_PATTERN_PROVIDER)
                .input(AEItems.COLORED_LUMEN_PAINT_BALL.item(AEColor.BLUE), 6)
                .input(AEItems.ENGINEERING_PROCESSOR)
                .power(50000)
                .save(this.output, ExtendedAE.id("assembler/assembler_matrix_pattern"));
        CrystalAssemblerRecipeBuilder
                .assemble(EAESingletons.ASSEMBLER_MATRIX_SPEED, this.items, this.fluids)
                .input(EAESingletons.ASSEMBLER_MATRIX_WALL)
                .input(AEItems.SPEED_CARD, 8)
                .input(AEItems.COLORED_LUMEN_PAINT_BALL.item(AEColor.RED), 6)
                .input(EAESingletons.CONCURRENT_PROCESSOR)
                .power(50000)
                .save(this.output, ExtendedAE.id("assembler/assembler_matrix_speed"));

        // Void Cell
        ShapedRecipeBuilder
                .shaped(this.items, RecipeCategory.MISC, EAESingletons.VOID_CELL)
                .pattern("CEC")
                .pattern("KXK")
                .pattern("III")
                .define('C', AEBlocks.QUARTZ_GLASS)
                .define('E', AEBlocks.CONDENSER)
                .define('K', AEItems.VOID_CARD)
                .define('X', AEItems.CELL_COMPONENT_16K)
                .define('I', Items.AMETHYST_SHARD)
                .unlockedBy(C, has(AEItems.VOID_CARD))
                .save(this.output);

        // Quartz Blend
        ShapelessRecipeBuilder
                .shapeless(this.items, RecipeCategory.MISC, EAESingletons.QUARTZ_BLEND, 6)
                .requires(ConventionTags.ALL_QUARTZ_DUST)
                .requires(Ingredient.of(Items.COAL, Items.CHARCOAL), 2)
                .requires(Ingredient.of(this.items.getOrThrow(BlockItems.SANDS.item())), 6)
                .unlockedBy(C, has(ConventionTags.ALL_QUARTZ_DUST))
                .save(this.output, ExtendedAE.stringId("quartz_blend_normal"));

        ShapelessRecipeBuilder
                .shapeless(this.items, RecipeCategory.MISC, EAESingletons.QUARTZ_BLEND, 6)
                .requires(EAETags.QUARTZ_DUST)
                .requires(Ingredient.of(Items.COAL, Items.CHARCOAL), 2)
                .requires(Ingredient.of(this.items.getOrThrow(BlockItems.SANDS.item())), 6)
                .unlockedBy(C, has(EAETags.QUARTZ_DUST))
                .save(this.output.withConditions(tagCondition(EAETags.QUARTZ_DUST)), ExtendedAE.stringId("quartz_blend_alt"));

        SimpleCookingRecipeBuilder
                .smelting(
                        Ingredient.of(EAESingletons.QUARTZ_BLEND),
                        RecipeCategory.MISC,
                        CookingBookCategory.MISC,
                        new ItemStackTemplate(AEItems.SILICON.get(), 4),
                        0.35F, 200
                )
                .unlockedBy(C, has(EAESingletons.QUARTZ_BLEND))
                .save(this.output,  ExtendedAE.stringId("smelting/quartz_blend"));

        SimpleCookingRecipeBuilder
                .blasting(
                        Ingredient.of(EAESingletons.QUARTZ_BLEND),
                        RecipeCategory.MISC,
                        CookingBookCategory.MISC,
                        new ItemStackTemplate(AEItems.SILICON.get(), 4),
                        0.35F, 100
                )
                .unlockedBy(C, has(EAESingletons.QUARTZ_BLEND))
                .save(this.output,  ExtendedAE.stringId("blasting/quartz_blend"));

        // Config Modifier
        ShapedRecipeBuilder
                .shaped(this.items, RecipeCategory.MISC, EAESingletons.CONFIG_MODIFIER)
                .pattern("BPB")
                .pattern(" E ")
                .define('B', ConventionTags.dye(DyeColor.BLUE))
                .define('P', AEItems.BLANK_PATTERN)
                .define('E', AEItems.ENGINEERING_PROCESSOR)
                .unlockedBy(C, has(EAESingletons.CONFIG_MODIFIER))
                .save(this.output);

        // Vacuum Interface
        CrystalAssemblerRecipeBuilder
                .assemble(EAESingletons.VACUUM_INTERFACE, this.items, this.fluids)
                .input(EAETags.EX_INTERFACE)
                .input(AEItems.CALCULATION_PROCESSOR, 4)
                .input(AEItems.SPATIAL_2_CELL_COMPONENT)
                .power(30000)
                .save(this.output, ExtendedAE.id("assembler/vacuum_interface"));

        // Pulverizer
        CrystalAssemblerRecipeBuilder
                .assemble(EAESingletons.PULVERIZER, this.items, this.fluids)
                .input(EAESingletons.MACHINE_FRAME)
                .input(Tags.Items.GEMS_AMETHYST, 8)
                .input(Tags.Items.INGOTS_IRON, 4)
                .input(AEItems.CALCULATION_PROCESSOR, 2)
                .power(20000)
                .save(this.output, ExtendedAE.id("assembler/pulverizer"));

        if (GlodUtil.checkMod(ModConstants.APPFLUX)) {
            appflux();
        }

        transformation();
        circuit();
        assemblerCircuit();
        fixer();
        crush();
    }

    private void appflux() {
        // Redstone Crystal
        CrystalAssemblerRecipeBuilder
                .assemble(AFSingletons.REDSTONE_CRYSTAL, 8, this.items, this.fluids)
                .input(BlockItems.STORAGE_BLOCKS_REDSTONE.item(), 4)
                .input(ConventionTags.FLUIX_CRYSTAL, 4)
                .input(ConventionTags.GLOWSTONE, 4)
                .fluid(Fluids.WATER, 100)
                .power(5000)
                .save(this.output.withConditions(mod(ModConstants.APPFLUX)), ExtendedAE.id("assembler/redstone_crystal"));
        // Energy Processor
        CircuitCutterRecipeBuilder
                .cut(AFSingletons.ENERGY_PROCESSOR_PRINT, 9, this.items)
                .input(AFSingletons.CHARGED_REDSTONE_BLOCK)
                .power(18000)
                .save(this.output.withConditions(mod(ModConstants.APPFLUX)), ExtendedAE.id("cutter/energy_processor"));
        CrystalAssemblerRecipeBuilder
                .assemble(AFSingletons.ENERGY_PROCESSOR, 4, this.items, this.fluids)
                .input(AFSingletons.ENERGY_PROCESSOR_PRINT, 4)
                .input(AEItems.SILICON_PRINT, 4)
                .input(ConventionTags.REDSTONE, 4)
                .power(4000)
                .save(this.output.withConditions(new ConfigCondition(ConfigCondition.IDs.ASSEMBLER_CIRCUIT), mod(ModConstants.APPFLUX)), ExtendedAE.id("assembler/energy_processor"));

        PulverizerRecipeBuilder
                .crush(ConventionTags.DIAMOND, this.items)
                .output(AFSingletons.DIAMOND_DUST)
                .save(this.output.withConditions(mod(ModConstants.APPFLUX)), ExtendedAE.id("pulverizer/diamond"));
        PulverizerRecipeBuilder
                .crush(Tags.Items.STORAGE_BLOCKS_DIAMOND, this.items)
                .output(AFSingletons.DIAMOND_DUST, 9)
                .save(this.output.withConditions(mod(ModConstants.APPFLUX)), ExtendedAE.id("pulverizer/storage_blocks_diamond"));
        PulverizerRecipeBuilder
                .crush(Tags.Items.GEMS_EMERALD, this.items)
                .output(AFSingletons.EMERALD_DUST)
                .save(this.output.withConditions(mod(ModConstants.APPFLUX)), ExtendedAE.id("pulverizer/emerald"));
        PulverizerRecipeBuilder
                .crush(Tags.Items.STORAGE_BLOCKS_EMERALD, this.items)
                .output(AFSingletons.EMERALD_DUST, 9)
                .save(this.output.withConditions(mod(ModConstants.APPFLUX)), ExtendedAE.id("pulverizer/storage_blocks_emerald"));
    }

    private void transformation() {
        // Fluix
        CrystalAssemblerRecipeBuilder
                .assemble(AEItems.FLUIX_CRYSTAL, 8, this.items, this.fluids)
                .input(AEItems.CERTUS_QUARTZ_CRYSTAL_CHARGED, 4)
                .input(ConventionTags.REDSTONE, 4)
                .input(ConventionTags.NETHER_QUARTZ, 4)
                .fluid(Fluids.WATER, 100)
                .power(1000)
                .save(this.output, ExtendedAE.id("assembler/fluix_transformation"));
        // Entro Infused Ingot
        CrystalAssemblerRecipeBuilder
                .assemble(EAESingletons.ENTRO_INGOT, 4, this.items, this.fluids)
                .input(EAETags.ENTRO_DUST, 4)
                .input(ConventionTags.GOLD_INGOT, 4)
                .input(Items.LAPIS_LAZULI, 4)
                .fluid(Fluids.WATER, 100)
                .power(8000)
                .save(this.output, ExtendedAE.id("assembler/entro_ingot_transformation"));
    }

    private void circuit() {
        // Four AE Processors
        CircuitCutterRecipeBuilder
                .cut(AEItems.ENGINEERING_PROCESSOR_PRINT, 9, this.items)
                .input(BlockItems.STORAGE_BLOCKS_DIAMOND.item())
                .power(18000)
                .save(this.output, ExtendedAE.id("cutter/engineering_processor"));
        CircuitCutterRecipeBuilder
                .cut(AEItems.LOGIC_PROCESSOR_PRINT, 9, this.items)
                .input(BlockItems.STORAGE_BLOCKS_GOLD.item())
                .power(18000)
                .save(this.output, ExtendedAE.id("cutter/logic_processor"));
        CircuitCutterRecipeBuilder
                .cut(AEItems.CALCULATION_PROCESSOR_PRINT, 4, this.items)
                .input(AEBlocks.QUARTZ_BLOCK)
                .power(8000)
                .save(this.output, ExtendedAE.id("cutter/calculation_processor"));
        CircuitCutterRecipeBuilder
                .cut(AEItems.SILICON_PRINT, 9, this.items)
                .input(EAETags.SILICON_BLOCK)
                .power(18000)
                .save(this.output, ExtendedAE.id("cutter/silicon_print"));

        // Entro Processor
        CircuitCutterRecipeBuilder
                .cut(EAESingletons.CONCURRENT_PROCESSOR_PRINT, 4, this.items)
                .input(EAETags.ENTRO_BLOCK)
                .power(8000)
                .save(this.output, ExtendedAE.id("cutter/concurrent_processor"));
    }

    private void assemblerCircuit() {
        var cond = new ConfigCondition(ConfigCondition.IDs.ASSEMBLER_CIRCUIT);
        CrystalAssemblerRecipeBuilder
                .assemble(AEItems.CALCULATION_PROCESSOR, 4, this.items, this.fluids)
                .input(AEItems.CALCULATION_PROCESSOR_PRINT, 4)
                .input(AEItems.SILICON_PRINT, 4)
                .input(ConventionTags.REDSTONE, 4)
                .power(4000)
                .save(this.output.withConditions(cond), ExtendedAE.id("assembler/calculation_processor"));
        CrystalAssemblerRecipeBuilder
                .assemble(AEItems.ENGINEERING_PROCESSOR, 4, this.items, this.fluids)
                .input(AEItems.ENGINEERING_PROCESSOR_PRINT, 4)
                .input(AEItems.SILICON_PRINT, 4)
                .input(ConventionTags.REDSTONE, 4)
                .power(4000)
                .save(this.output.withConditions(cond), ExtendedAE.id("assembler/engineering_processor"));
        CrystalAssemblerRecipeBuilder
                .assemble(AEItems.LOGIC_PROCESSOR, 4, this.items, this.fluids)
                .input(AEItems.LOGIC_PROCESSOR_PRINT, 4)
                .input(AEItems.SILICON_PRINT, 4)
                .input(ConventionTags.REDSTONE, 4)
                .power(4000)
                .save(this.output.withConditions(cond), ExtendedAE.id("assembler/logic_processor"));
        CrystalAssemblerRecipeBuilder
                .assemble(EAESingletons.CONCURRENT_PROCESSOR, 4, this.items, this.fluids)
                .input(EAESingletons.CONCURRENT_PROCESSOR_PRINT, 4)
                .input(AEItems.SILICON_PRINT, 4)
                .input(ConventionTags.REDSTONE, 4)
                .power(4000)
                .save(this.output.withConditions(cond), ExtendedAE.id("assembler/concurrent_processor"));
    }

    private void fixer() {
        CrystalFixerRecipeBuilder
                .fixer(AEBlocks.DAMAGED_BUDDING_QUARTZ.block(), AEBlocks.CHIPPED_BUDDING_QUARTZ.block(), this.items)
                .fuel(AEItems.CERTUS_QUARTZ_CRYSTAL_CHARGED)
                .chance(0.8)
                .save(this.output, ExtendedAE.id("fixer/certus_damage"));
        CrystalFixerRecipeBuilder
                .fixer(AEBlocks.CHIPPED_BUDDING_QUARTZ.block(), AEBlocks.FLAWED_BUDDING_QUARTZ.block(), this.items)
                .fuel(AEItems.CERTUS_QUARTZ_CRYSTAL_CHARGED)
                .chance(0.8)
                .save(this.output, ExtendedAE.id("fixer/certus_chipped"));
        CrystalFixerRecipeBuilder
                .fixer(AEBlocks.FLAWED_BUDDING_QUARTZ.block(), AEBlocks.FLAWLESS_BUDDING_QUARTZ.block(), this.items)
                .fuel(AEItems.CERTUS_QUARTZ_CRYSTAL_CHARGED)
                .chance(0.05)
                .save(this.output, ExtendedAE.id("fixer/certus_flawed"));
        CrystalFixerRecipeBuilder
                .fixer(EAESingletons.HARDLY_ENTROIZED_FLUIX_BUDDING.get(), EAESingletons.HALF_ENTROIZED_FLUIX_BUDDING.get(), this.items)
                .fuel(ConventionTags.ENDER_PEARL_DUST)
                .chance(0.7)
                .save(this.output, ExtendedAE.id("fixer/entro_hardly"));
        CrystalFixerRecipeBuilder
                .fixer(EAESingletons.HALF_ENTROIZED_FLUIX_BUDDING.get(), EAESingletons.MOSTLY_ENTROIZED_FLUIX_BUDDING.get(), this.items)
                .fuel(ConventionTags.ENDER_PEARL_DUST)
                .chance(0.7)
                .save(this.output, ExtendedAE.id("fixer/entro_half"));
        CrystalFixerRecipeBuilder
                .fixer(EAESingletons.MOSTLY_ENTROIZED_FLUIX_BUDDING.get(), EAESingletons.FULLY_ENTROIZED_FLUIX_BUDDING.get(), this.items)
                .fuel(ConventionTags.ENDER_PEARL_DUST)
                .chance(0.7)
                .save(this.output, ExtendedAE.id("fixer/entro_mostly"));
        CrystalFixerRecipeBuilder
                .fixer(Blocks.AMETHYST_BLOCK, Blocks.BUDDING_AMETHYST, this.items)
                .fuel(Tags.Items.GEMS_AMETHYST)
                .chance(0.05)
                .save(this.output, ExtendedAE.id("fixer/budding_amethyst"));
    }

    private void crush() {
        PulverizerRecipeBuilder
                .crush(ConventionTags.CERTUS_QUARTZ, this.items)
                .output(AEItems.CERTUS_QUARTZ_DUST)
                .power(500)
                .save(this.output, ExtendedAE.id("pulverizer/certus_quartz"));
        PulverizerRecipeBuilder
                .crush(EAETags.castToItemTag(ConventionTags.CERTUS_QUARTZ_STORAGE_BLOCK_BLOCK), this.items)
                .output(AEItems.CERTUS_QUARTZ_DUST, 4)
                .power(2000)
                .save(this.output, ExtendedAE.id("pulverizer/certus_quartz_storage_block"));
        PulverizerRecipeBuilder
                .crush(Ingredient.of(AEBlocks.CUT_QUARTZ_BLOCK, AEBlocks.SMOOTH_QUARTZ_BLOCK, AEBlocks.QUARTZ_BRICKS, AEBlocks.QUARTZ_PILLAR, AEBlocks.CHISELED_QUARTZ_BLOCK))
                .output(AEItems.CERTUS_QUARTZ_DUST, 4)
                .power(2000)
                .save(this.output, ExtendedAE.id("pulverizer/certus_quartz_block_alt"));
        PulverizerRecipeBuilder
                .crush(Ingredient.of(AEBlocks.QUARTZ_WALL, AEBlocks.CUT_QUARTZ_WALL, AEBlocks.SMOOTH_QUARTZ_WALL, AEBlocks.QUARTZ_BRICK_WALL, AEBlocks.QUARTZ_PILLAR_WALL, AEBlocks.CHISELED_QUARTZ_WALL))
                .output(AEItems.CERTUS_QUARTZ_DUST, 4)
                .power(2000)
                .save(this.output, ExtendedAE.id("pulverizer/certus_quartz_walls"));
        PulverizerRecipeBuilder
                .crush(Ingredient.of(AEBlocks.QUARTZ_STAIRS, AEBlocks.CUT_QUARTZ_STAIRS, AEBlocks.SMOOTH_QUARTZ_STAIRS, AEBlocks.QUARTZ_BRICK_STAIRS, AEBlocks.QUARTZ_PILLAR_STAIRS, AEBlocks.CHISELED_QUARTZ_STAIRS))
                .output(AEItems.CERTUS_QUARTZ_DUST, 3)
                .power(1500)
                .save(this.output, ExtendedAE.id("pulverizer/certus_quartz_stairs"));
        PulverizerRecipeBuilder
                .crush(Ingredient.of(AEBlocks.QUARTZ_SLAB, AEBlocks.CUT_QUARTZ_SLAB, AEBlocks.SMOOTH_QUARTZ_SLAB, AEBlocks.QUARTZ_BRICK_SLAB, AEBlocks.QUARTZ_PILLAR_SLAB, AEBlocks.CHISELED_QUARTZ_SLAB))
                .output(AEItems.CERTUS_QUARTZ_DUST, 2)
                .power(1000)
                .save(this.output, ExtendedAE.id("pulverizer/certus_quartz_slabs"));
        PulverizerRecipeBuilder
                .crush(Ingredient.of(AEBlocks.SMALL_QUARTZ_BUD, AEBlocks.MEDIUM_QUARTZ_BUD, AEBlocks.LARGE_QUARTZ_BUD))
                .output(AEItems.CERTUS_QUARTZ_DUST)
                .power(500)
                .save(this.output, ExtendedAE.id("pulverizer/certus_quartz_ungrown_bud"));
        PulverizerRecipeBuilder
                .crush(AEBlocks.QUARTZ_CLUSTER)
                .output(AEItems.CERTUS_QUARTZ_CRYSTAL, 4)
                .output(AEItems.CERTUS_QUARTZ_CRYSTAL, 2, 0.5)
                .output(AEItems.CERTUS_QUARTZ_DUST, 2, 0.2)
                .power(3000)
                .save(this.output, ExtendedAE.id("pulverizer/certus_quartz_cluster"));
        PulverizerRecipeBuilder
                .crush(Ingredient.of(AEBlocks.CHIPPED_BUDDING_QUARTZ, AEBlocks.FLAWED_BUDDING_QUARTZ, AEBlocks.FLAWLESS_BUDDING_QUARTZ, AEBlocks.DAMAGED_BUDDING_QUARTZ))
                .output(AEItems.CERTUS_QUARTZ_CRYSTAL, 4)
                .power(2000)
                .save(this.output, ExtendedAE.id("pulverizer/recycle_budding_quartz"));
        PulverizerRecipeBuilder
                .crush(Ingredient.of(Blocks.QUARTZ_BLOCK, Blocks.CHISELED_QUARTZ_BLOCK, Blocks.QUARTZ_BRICKS, Blocks.QUARTZ_PILLAR, Blocks.SMOOTH_QUARTZ))
                .output(Items.QUARTZ, 4)
                .power(2000)
                .save(this.output, ExtendedAE.id("pulverizer/quartz_blocks"));
        PulverizerRecipeBuilder
                .crush(Ingredient.of(Blocks.QUARTZ_STAIRS, Blocks.SMOOTH_QUARTZ_STAIRS))
                .output(Items.QUARTZ, 3)
                .power(1500)
                .save(this.output, ExtendedAE.id("pulverizer/quartz_stairs"));
        PulverizerRecipeBuilder
                .crush(Ingredient.of(Blocks.QUARTZ_SLAB, Blocks.SMOOTH_QUARTZ_SLAB))
                .output(Items.QUARTZ, 2)
                .power(1000)
                .save(this.output, ExtendedAE.id("pulverizer/quartz_slab"));
        PulverizerRecipeBuilder
                .crush(ConventionTags.FLUIX_CRYSTAL, this.items)
                .output(AEItems.FLUIX_DUST)
                .power(500)
                .save(this.output, ExtendedAE.id("pulverizer/fluix_crystal"));
        PulverizerRecipeBuilder
                .crush(AEBlocks.FLUIX_BLOCK)
                .output(AEItems.FLUIX_DUST, 4)
                .power(2000)
                .save(this.output, ExtendedAE.id("pulverizer/fluix_block"));
        PulverizerRecipeBuilder
                .crush(AEBlocks.FLUIX_WALL)
                .output(AEItems.FLUIX_DUST, 4)
                .power(2000)
                .save(this.output, ExtendedAE.id("pulverizer/fluix_wall"));
        PulverizerRecipeBuilder
                .crush(AEBlocks.FLUIX_STAIRS)
                .output(AEItems.FLUIX_DUST, 3)
                .power(1500)
                .save(this.output, ExtendedAE.id("pulverizer/fluix_stairs"));
        PulverizerRecipeBuilder
                .crush(AEBlocks.FLUIX_SLAB)
                .output(AEItems.FLUIX_DUST, 2)
                .power(1000)
                .save(this.output, ExtendedAE.id("pulverizer/fluix_slab"));
        PulverizerRecipeBuilder
                .crush(EAETags.ENTRO_CRYSTAL, this.items)
                .output(EAESingletons.ENTRO_DUST)
                .power(500)
                .save(this.output, ExtendedAE.id("pulverizer/entro_crystal"));
        PulverizerRecipeBuilder
                .crush(Ingredient.of(EAESingletons.ENTRO_BUD_SMALL, EAESingletons.ENTRO_BUD_MEDIUM, EAESingletons.ENTRO_BUD_LARGE))
                .output(EAESingletons.ENTRO_SHARD)
                .power(500)
                .save(this.output, ExtendedAE.id("pulverizer/entro_ungrown_bud"));
        PulverizerRecipeBuilder
                .crush(EAESingletons.ENTRO_CLUSTER)
                .output(EAESingletons.ENTRO_CRYSTAL)
                .output(EAESingletons.ENTRO_SHARD, 1, 0.8)
                .output(EAESingletons.ENTRO_DUST, 1, 0.3)
                .power(1000)
                .save(this.output, ExtendedAE.id("pulverizer/entro_cluster"));
        PulverizerRecipeBuilder
                .crush(Blocks.AMETHYST_BLOCK)
                .output(Items.AMETHYST_SHARD, 4)
                .power(2000)
                .save(this.output, ExtendedAE.id("pulverizer/amethyst_block"));
        PulverizerRecipeBuilder
                .crush(Ingredient.of(Blocks.SMALL_AMETHYST_BUD, Blocks.MEDIUM_AMETHYST_BUD, Blocks.LARGE_AMETHYST_BUD))
                .output(Items.AMETHYST_SHARD, 2)
                .power(1000)
                .save(this.output, ExtendedAE.id("pulverizer/amethyst_ungrown_bud"));
        PulverizerRecipeBuilder
                .crush(Blocks.AMETHYST_CLUSTER)
                .output(Items.AMETHYST_SHARD, 4)
                .output(Items.AMETHYST_SHARD, 2, 0.5)
                .output(Items.AMETHYST_SHARD, 1, 0.3)
                .power(2000)
                .save(this.output, ExtendedAE.id("pulverizer/amethyst_cluster"));
        PulverizerRecipeBuilder
                .crush(EAETags.ENTRO_BLOCK, this.items)
                .output(EAESingletons.ENTRO_DUST, 4)
                .power(2000)
                .save(this.output, ExtendedAE.id("pulverizer/entro_block"));
        PulverizerRecipeBuilder
                .crush(Ingredient.of(AEBlocks.SMOOTH_SKY_STONE_BLOCK, AEBlocks.SKY_STONE_SMALL_BRICK, AEBlocks.SKY_STONE_BLOCK, AEBlocks.SKY_STONE_BRICK))
                .output(AEItems.SKY_DUST)
                .save(this.output, ExtendedAE.id("pulverizer/sky_stone_block"));
        PulverizerRecipeBuilder
                .crush(Ingredient.of(AEBlocks.SMOOTH_SKY_STONE_WALL, AEBlocks.SKY_STONE_SMALL_BRICK_WALL, AEBlocks.SKY_STONE_WALL, AEBlocks.SKY_STONE_BRICK_WALL))
                .output(AEItems.SKY_DUST)
                .save(this.output, ExtendedAE.id("pulverizer/sky_stone_wall"));
        PulverizerRecipeBuilder
                .crush(Ingredient.of(AEBlocks.SMOOTH_SKY_STONE_STAIRS, AEBlocks.SKY_STONE_SMALL_BRICK_STAIRS, AEBlocks.SKY_STONE_STAIRS, AEBlocks.SKY_STONE_BRICK_STAIRS))
                .output(AEItems.SKY_DUST, 1, 0.75)
                .save(this.output, ExtendedAE.id("pulverizer/sky_stone_stairs"));
        PulverizerRecipeBuilder
                .crush(Ingredient.of(AEBlocks.SMOOTH_SKY_STONE_SLAB, AEBlocks.SKY_STONE_SMALL_BRICK_SLAB, AEBlocks.SKY_STONE_SLAB, AEBlocks.SKY_STONE_BRICK_SLAB))
                .output(AEItems.SKY_DUST, 1, 0.5)
                .save(this.output, ExtendedAE.id("pulverizer/sky_stone_slab"));
        PulverizerRecipeBuilder
                .crush(ConventionTags.ENDER_PEARL, this.items)
                .output(AEItems.ENDER_DUST)
                .save(this.output, ExtendedAE.id("pulverizer/ender_pearl"));
        PulverizerRecipeBuilder
                .crush(Items.ENDER_EYE)
                .output(AEItems.ENDER_DUST)
                .output(Items.BLAZE_POWDER)
                .save(this.output, ExtendedAE.id("pulverizer/ender_eye"));
        PulverizerRecipeBuilder
                .crush(Blocks.GLOWSTONE)
                .output(Items.GLOWSTONE_DUST, 4)
                .save(this.output, ExtendedAE.id("pulverizer/glowstone"));
        PulverizerRecipeBuilder
                .crush(Blocks.CLAY)
                .output(Items.CLAY_BALL, 4)
                .save(this.output, ExtendedAE.id("pulverizer/clay"));
        PulverizerRecipeBuilder
                .crush(Blocks.GILDED_BLACKSTONE)
                .output(Blocks.BLACKSTONE)
                .output(Items.GOLD_NUGGET, 1, 0.1)
                .power(2000)
                .save(this.output, ExtendedAE.id("pulverizer/gilded_blackstone"));
        PulverizerRecipeBuilder
                .crush(Tags.Items.ORES_NETHERITE_SCRAP, this.items)
                .output(Items.NETHERITE_SCRAP, 2)
                .output(Items.NETHERITE_SCRAP, 1, 0.2)
                .output(Items.NETHERITE_SCRAP, 1, 0.05)
                .power(10000)
                .save(this.output, ExtendedAE.id("pulverizer/ores_netherite_scrap"));
        PulverizerRecipeBuilder
                .crush(Tags.Items.BONES, this.items)
                .output(Items.BONE_MEAL, 3)
                .output(Items.BONE_MEAL, 1, 0.9)
                .output(Items.BONE_MEAL, 1, 0.5)
                .power(1500)
                .save(this.output, ExtendedAE.id("pulverizer/bones"));
        PulverizerRecipeBuilder
                .crush(Tags.Items.RODS_BLAZE, this.items)
                .output(Items.BLAZE_POWDER, 2)
                .output(Items.BLAZE_POWDER, 1, 0.8)
                .output(Items.BLAZE_POWDER, 1, 0.3)
                .power(3000)
                .save(this.output, ExtendedAE.id("pulverizer/rods_blaze"));
        PulverizerRecipeBuilder
                .crush(Tags.Items.RODS_BREEZE, this.items)
                .output(Items.WIND_CHARGE, 4)
                .output(Items.WIND_CHARGE, 2, 0.8)
                .output(Items.WIND_CHARGE, 1, 0.3)
                .power(3000)
                .save(this.output, ExtendedAE.id("pulverizer/rods_breeze"));
        PulverizerRecipeBuilder
                .crush(Items.PRISMARINE_SHARD)
                .output(Items.PRISMARINE_CRYSTALS)
                .output(Items.PRISMARINE_CRYSTALS, 1, 0.5)
                .power(2000)
                .save(this.output, ExtendedAE.id("pulverizer/prismarine_shard"));
        PulverizerRecipeBuilder
                .crush(Tags.Items.COBBLESTONES, this.items)
                .output(Items.GRAVEL)
                .output(Items.SAND, 1, 0.1)
                .save(this.output, ExtendedAE.id("pulverizer/cobblestones"));
        PulverizerRecipeBuilder
                .crush(Tags.Items.GRAVELS, this.items)
                .output(Items.FLINT)
                .output(Items.FLINT, 1, 0.1)
                .power(1500)
                .save(this.output, ExtendedAE.id("pulverizer/gravels"));
        PulverizerRecipeBuilder
                .crush(EAESingletons.FISHBIG)
                .output(Items.PUFFERFISH, 8)
                .power(500)
                .save(this.output, ExtendedAE.id("pulverizer/fishbig_destroy"));
        PulverizerRecipeBuilder
                .crush(EAESingletons.MDDYUE)
                .output(Items.EGG, 8)
                .power(500)
                .save(this.output, ExtendedAE.id("pulverizer/mddyue_destroy"));
    }

    private ICondition mod(String modid) {
        return new ModLoadedCondition(modid);
    }

    private ICondition tagCondition(TagKey<@NotNull Item> tag) {
        return new NotCondition(new TagEmptyCondition<>(tag));
    }
}
