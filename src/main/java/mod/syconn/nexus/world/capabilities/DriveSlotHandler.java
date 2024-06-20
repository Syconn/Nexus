package mod.syconn.nexus.world.capabilities;

import mod.syconn.nexus.blockentities.DriveBE;
import mod.syconn.nexus.items.StorageDrive;
import mod.syconn.nexus.util.DriveHelper;
import mod.syconn.nexus.util.data.DriveSlot;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;

public class DriveSlotHandler implements IDriveHandler, INBTSerializable<CompoundTag> {

    private final int slots;
    private final DriveBE drive;
    private DriveSlot[] driveSlots;

    public DriveSlotHandler(DriveBE be, int slots) {
        this.slots = slots;
        this.driveSlots = new DriveSlot[slots];
        this.drive = be;
    }

    public List<ItemStack> getStacks() {
        List<DriveSlot.ItemTypes> types = new ArrayList<>();
        for (DriveSlot driveSlot : driveSlots) if (driveSlot != null) types.addAll(driveSlot.getStacks());
        return DriveSlot.ItemTypes.convertToStacks(types);
    }

    public DriveSlot[] getDriveSlots() {
        return driveSlots;
    }

    public boolean addDrive(ItemStack stack) {
        if (stack.getItem() instanceof StorageDrive) {
            for (int i = 0; i < driveSlots.length; i++) {
                if (driveSlots[i] == null) {
                    driveSlots[i] = DriveHelper.getDriveSlot(stack);
                    return true;
                }
            }
        }
        return false;
    }

    public ItemStack removeDrive() {
        for (int i = 0; i < driveSlots.length; i++) {
            if (driveSlots[i] != null) {
                ItemStack stack = DriveHelper.getStorageDrive(driveSlots[i]);
                driveSlots[i] = null;
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    public ItemStack addStack(ItemStack stack) {
        ItemStack returnStack = stack.copy();
        for (int i = 0; i < driveSlots.length; i++) {
            if (driveSlots[i] != null) {
                returnStack.copyWithCount(returnStack.getCount() - driveSlots[i].addStack(stack).getCount());
                updateScreen();
                if (returnStack.isEmpty()) return ItemStack.EMPTY;
            }
        }
        return returnStack;
    }

    public ItemStack removeStack(ItemStack stack) {
        ItemStack copy = stack.copy();
        int removed = 0;
        for (int i = 0; i < driveSlots.length; i++) {
            if (driveSlots[i] != null) {
                removed += driveSlots[i].removeStack(stack).getCount();
                if (removed >= copy.getCount()) return copy;
            }
        }
        return copy.copyWithCount(removed);
    }

    public void updateScreen() {
        drive.updateScreen();
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag drives = new ListTag();
        for (int i = 0; i < driveSlots.length; i++) {
            if (driveSlots[i] != null) {
                CompoundTag entry = new CompoundTag();
                entry.putInt("slot", i);
                entry.put("drive", driveSlots[i].save());
                drives.add(entry);
            }
        }
        tag.put("drives", drives);
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        driveSlots = new DriveSlot[slots];
        tag.getList("drives", Tag.TAG_COMPOUND).forEach(nbt -> {
            CompoundTag entry = (CompoundTag) nbt;
            driveSlots[entry.getInt("slot")] = new DriveSlot(entry.getCompound("drive"));
        });
    }
}
