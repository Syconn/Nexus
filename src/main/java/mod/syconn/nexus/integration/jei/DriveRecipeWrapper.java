package mod.syconn.nexus.integration.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import mod.syconn.nexus.util.DriveHelper;
import mod.syconn.nexus.world.crafting.StorageDriveRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

import static mod.syconn.nexus.Registration.*;

public class DriveRecipeWrapper implements ICraftingCategoryExtension<StorageDriveRecipe> {

    public void setRecipe(RecipeHolder<StorageDriveRecipe> recipeHolder, IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
        List<ItemStack> edge1 = List.of(ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY);
        List<ItemStack> edge2 = List.of(new ItemStack(Items.IRON_INGOT), ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY);
        List<ItemStack> edge3 = List.of(new ItemStack(Items.REDSTONE), ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY);
        List<ItemStack> center = List.of(new ItemStack(Items.GOLD_INGOT), DriveHelper.createDrive(320), DriveHelper.createDrive(320*IRON_UPGRADE.get().getUpgradeSpeed()),
                DriveHelper.createDrive(320*GOLD_UPGRADE.get().getUpgradeSpeed()), DriveHelper.createDrive(320*DIAMOND_UPGRADE.get().getUpgradeSpeed()));
        List<ItemStack> middle1 = List.of(ItemStack.EMPTY, new ItemStack(IRON_UPGRADE.get()), new ItemStack(GOLD_UPGRADE.get()), new ItemStack(DIAMOND_UPGRADE.get()), new ItemStack(EMERALD_UPGRADE.get()));
        List<ItemStack> middle2 = List.of(new ItemStack(Items.IRON_INGOT), new ItemStack(IRON_UPGRADE.get()), new ItemStack(GOLD_UPGRADE.get()), new ItemStack(DIAMOND_UPGRADE.get()), new ItemStack(EMERALD_UPGRADE.get()));
        List<ItemStack> middle3 = List.of(new ItemStack(Items.GOLD_INGOT), new ItemStack(IRON_UPGRADE.get()), new ItemStack(GOLD_UPGRADE.get()), new ItemStack(DIAMOND_UPGRADE.get()), new ItemStack(EMERALD_UPGRADE.get()));
        craftingGridHelper.createAndSetInputs(builder, List.of(edge2, middle2, edge2, middle3, center, middle2, edge3, middle1, edge1), getWidth(), getHeight());
        craftingGridHelper.createAndSetOutputs(builder, List.of(DriveHelper.createDrive(320), DriveHelper.createDrive(320*IRON_UPGRADE.get().getUpgradeSpeed()),
                DriveHelper.createDrive(320*GOLD_UPGRADE.get().getUpgradeSpeed()), DriveHelper.createDrive(320*DIAMOND_UPGRADE.get().getUpgradeSpeed()), DriveHelper.createDrive(320*EMERALD_UPGRADE.get().getUpgradeSpeed())));
    }

    public int getHeight() {
        return 3;
    }

    public int getWidth() {
        return 3;
    }
}
