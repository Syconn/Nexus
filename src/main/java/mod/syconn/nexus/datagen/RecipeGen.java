package mod.syconn.nexus.datagen;

import mod.syconn.nexus.Registration;
import mod.syconn.nexus.world.crafting.StorageDriveRecipe;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class RecipeGen extends RecipeProvider {

    public RecipeGen(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    protected void buildRecipes(RecipeOutput pRecipeOutput) {
        SpecialRecipeBuilder.special(StorageDriveRecipe::new).save(pRecipeOutput, "storage_drive");
        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, Registration.ITEM_PIPE)
                .pattern(" g ")
                .pattern("ghg")
                .pattern(" g ")
                .define('g', Tags.Items.GLASS)
                .define('h', Items.HOPPER)
                .unlockedBy("hopper", InventoryChangeTrigger.TriggerInstance.hasItems(Items.HOPPER))
                .save(pRecipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Registration.INTERFACE)
                .pattern("dbd")
                .pattern("dgd")
                .pattern("dbd")
                .define('d', Items.REDSTONE)
                .define('b', Items.REDSTONE_BLOCK)
                .define('g', Items.REDSTONE_LAMP)
                .unlockedBy("lamp", InventoryChangeTrigger.TriggerInstance.hasItems(Items.REDSTONE_LAMP))
                .save(pRecipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, Registration.CRAFTING_INTERFACE)
                .requires(Registration.INTERFACE)
                .requires(Items.CRAFTING_TABLE)
                .unlockedBy("interface", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.INTERFACE))
                .save(pRecipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, Registration.EXTERNAL_STORAGE)
                .pattern("br ")
                .pattern("hpr")
                .pattern("br ")
                .define('b', Items.BLACK_CONCRETE)
                .define('r', Items.REDSTONE)
                .define('p', Registration.ITEM_PIPE)
                .define('h', Items.HOPPER)
                .unlockedBy("pipe", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.ITEM_PIPE))
                .save(pRecipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Registration.NEXUS)
                .pattern("byb")
                .pattern("pcl")
                .pattern("brb")
                .define('b', Items.REDSTONE_BLOCK)
                .define('y', Items.YELLOW_CONCRETE)
                .define('p', Items.PURPLE_CONCRETE)
                .define('c', Items.END_CRYSTAL)
                .define('l', Items.LIGHT_BLUE_CONCRETE)
                .define('r', Items.RED_CONCRETE)
                .unlockedBy("end_crystal", InventoryChangeTrigger.TriggerInstance.hasItems(Items.END_CRYSTAL))
                .save(pRecipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Registration.DRIVE)
                .pattern("sis")
                .pattern("ici")
                .pattern("sis")
                .define('s', Items.SMOOTH_STONE)
                .define('i', Items.IRON_BLOCK)
                .define('c', Items.END_CRYSTAL)
                .unlockedBy("end_crystal", InventoryChangeTrigger.TriggerInstance.hasItems(Items.END_CRYSTAL))
                .save(pRecipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Registration.NETHERITE_UPGRADE.get())
                .pattern(" n ")
                .pattern("nrn")
                .pattern(" n ")
                .define('n', Items.NETHERITE_INGOT)
                .define('r', Registration.EMERALD_UPGRADE.get())
                .unlockedBy("has_ingot", inventoryTrigger(ItemPredicate.Builder.item().of(Items.NETHERITE_INGOT).build()))
                .save(pRecipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Registration.EMERALD_UPGRADE.get())
                .pattern(" n ")
                .pattern("nrn")
                .pattern(" n ")
                .define('n', Items.EMERALD)
                .define('r', Registration.DIAMOND_UPGRADE.get())
                .unlockedBy("has_ingot", inventoryTrigger(ItemPredicate.Builder.item().of(Items.EMERALD).build()))
                .save(pRecipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Registration.DIAMOND_UPGRADE.get())
                .pattern(" n ")
                .pattern("nrn")
                .pattern(" n ")
                .define('n', Items.DIAMOND)
                .define('r', Registration.GOLD_UPGRADE.get())
                .unlockedBy("has_ingot", inventoryTrigger(ItemPredicate.Builder.item().of(Items.DIAMOND).build()))
                .save(pRecipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Registration.GOLD_UPGRADE.get())
                .pattern(" n ")
                .pattern("nrn")
                .pattern(" n ")
                .define('n', Items.GOLD_INGOT)
                .define('r', Registration.IRON_UPGRADE.get())
                .unlockedBy("has_ingot", inventoryTrigger(ItemPredicate.Builder.item().of(Items.GOLD_INGOT).build()))
                .save(pRecipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Registration.IRON_UPGRADE.get())
                .pattern(" n ")
                .pattern("nrn")
                .pattern(" n ")
                .define('n', Items.IRON_INGOT)
                .define('r', Items.REDSTONE)
                .unlockedBy("has_ingot", inventoryTrigger(ItemPredicate.Builder.item().of(Items.IRON_INGOT).build()))
                .save(pRecipeOutput);
    }
}
