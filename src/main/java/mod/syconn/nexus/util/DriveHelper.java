package mod.syconn.nexus.util;

import mod.syconn.nexus.Registration;
import mod.syconn.nexus.util.data.DriveSlot;
import net.minecraft.world.item.ItemStack;

public class DriveHelper {

    public static ItemStack createStorageDrive(int storage) {
        ItemStack stack = new ItemStack(Registration.STORAGE_DRIVE.get());
        stack.getOrCreateTag().put("data", new DriveSlot(storage).save());
        return stack;
    }

    public static ItemStack getStorageDrive(DriveSlot slot) {
        ItemStack stack = createStorageDrive(slot.getMaxQuantity());
        stack.getOrCreateTag().put("data", slot.save());
        return stack;
    }

    public static DriveSlot getDriveSlot(ItemStack stack) {
        return new DriveSlot(stack.getOrCreateTag().getCompound("data"));
    }
}
