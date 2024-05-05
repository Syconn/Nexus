package mod.syconn.nexus.datagen;

import mod.syconn.nexus.Registration;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class RecipeGen extends RecipeProvider {

    public RecipeGen(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    protected void buildRecipes(RecipeOutput pRecipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, Registration.ITEM_PIPE)
                .pattern(" g ")
                .pattern("ghg")
                .pattern(" g ")
                .define('g', Tags.Items.GLASS)
                .define('h', Items.HOPPER)
                .unlockedBy("hopper", InventoryChangeTrigger.TriggerInstance.hasItems(Items.HOPPER))
                .save(pRecipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, Registration.INTERFACE)
                .pattern("dbd")
                .pattern("dgd")
                .pattern("dbd")
                .define('d', Items.REDSTONE)
                .define('b', Items.REDSTONE_BLOCK)
                .define('g', Items.REDSTONE_LAMP)
                .unlockedBy("lamp", InventoryChangeTrigger.TriggerInstance.hasItems(Items.REDSTONE_LAMP))
                .save(pRecipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.TRANSPORTATION, Registration.CRAFTING_INTERFACE)
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
    }
}
