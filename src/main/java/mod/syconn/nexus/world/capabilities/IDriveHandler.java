package mod.syconn.nexus.world.capabilities;

import mod.syconn.nexus.util.data.DriveSlot;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface IDriveHandler {

    List<ItemStack> getStacks();

    DriveSlot[] getDriveSlots();

    ItemStack addStack(ItemStack stack);

    ItemStack removeStack(ItemStack stack);

    boolean addDrive(ItemStack stack);

    ItemStack removeDrive();

    void updateScreen();
}
