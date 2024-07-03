package mod.syconn.nexus.world.crafting;

import mod.syconn.nexus.Registration;
import mod.syconn.nexus.items.StorageDrive;
import mod.syconn.nexus.items.UpgradeItem;
import mod.syconn.nexus.util.DriveHelper;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

import static mod.syconn.nexus.Registration.*;

public class StorageDriveRecipe extends CustomRecipe {

    private final Map<Integer, Integer> toCraft = new HashMap<>() {{ put(IRON_UPGRADE.get().getUpgradeSpeed(), 320); put(GOLD_UPGRADE.get().getUpgradeSpeed(), IRON_UPGRADE.get().getUpgradeSpeed() * 320);
        put(DIAMOND_UPGRADE.get().getUpgradeSpeed(), GOLD_UPGRADE.get().getUpgradeSpeed() * 320); put(EMERALD_UPGRADE.get().getUpgradeSpeed(), DIAMOND_UPGRADE.get().getUpgradeSpeed() * 320); }};

    public StorageDriveRecipe(CraftingBookCategory pCategory) {
        super(pCategory);
    }

    public boolean matches(CraftingContainer inventory, Level pLevel) {
        if (inventory.getWidth() == 3 && inventory.getHeight() == 3) {
            int iron_ingots = 0;
            int gold_ingots = 0;
            int redstone = 0;
            for (ItemStack stack : inventory.getItems()) {
                if (stack.is(Items.IRON_INGOT)) iron_ingots++;
                else if (stack.is(Items.GOLD_INGOT)) gold_ingots++;
                else if (stack.is(Items.REDSTONE)) redstone++;
            }
            if (iron_ingots == 4 && gold_ingots == 2 && redstone == 1) return true;
            if (!(inventory.getItem(1).getItem() instanceof UpgradeItem)) return false;
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                if (i % 2 == 0 && i != 4 && !inventory.getItem(i).isEmpty()) return false;
                if (i % 2 != 0 && i != 1 && !inventory.getItem(i).is(inventory.getItem(1).getItem())) return false;
            }
            return inventory.getItem(4).getItem() instanceof StorageDrive && toCraft.get(((UpgradeItem) inventory.getItem(1).getItem()).getUpgradeSpeed()).equals(DriveHelper.getDriveSlot(inventory.getItem(4)).getMaxQuantity());
        }
        return false;
    }

    public ItemStack assemble(CraftingContainer inventory, RegistryAccess pRegistryAccess) {
        if (inventory.getWidth() == 3 && inventory.getHeight() == 3) {
            if (inventory.getItem(4).getItem() instanceof StorageDrive && inventory.getItem(1).getItem() instanceof UpgradeItem)
                return DriveHelper.createDrive(((UpgradeItem) inventory.getItem(1).getItem()).getUpgradeSpeed() * 320);
            else return DriveHelper.createDrive(320);
        }
        return ItemStack.EMPTY;
    }

    public boolean canCraftInDimensions(int width, int height) {
        return width > 2 && height > 2;
    }

    public RecipeSerializer<?> getSerializer() {
        return Registration.STORAGE_DRIVE_SERIALIZER.get();
    }
}