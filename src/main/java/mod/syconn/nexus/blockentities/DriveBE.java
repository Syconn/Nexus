package mod.syconn.nexus.blockentities;

import mod.syconn.nexus.Registration;
import mod.syconn.nexus.blocks.DriveBlock;
import mod.syconn.nexus.items.StorageDrive;
import mod.syconn.nexus.util.DriveHelper;
import mod.syconn.nexus.util.data.DriveSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class DriveBE extends BasePipeBE {

    private DriveSlot[] driveSlots = new DriveSlot[10];

    public DriveBE(BlockPos pos, BlockState state) {
        super(Registration.DRIVE_BE.get(), pos, state);
    }

    public void tickServer() {

    }

    public boolean addDrive(ItemStack stack) {
        if (stack.getItem() instanceof StorageDrive) {
            for (int i = 0; i < driveSlots.length; i++) {
                if (driveSlots[i] == null) {
                    driveSlots[i] = DriveHelper.getDriveSlot(stack);
                    markDirty();
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
                markDirty();
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    public DriveSlot[] getDriveSlots() {
        return driveSlots;
    }

    public boolean canConnect(BlockPos pos, Direction conDir) {
        return !getBlockState().getValue(DriveBlock.FACING).getOpposite().equals(conDir);
    }

    protected void loadClientData(CompoundTag tag) {
        super.loadClientData(tag);
        driveSlots = new DriveSlot[10];
        tag.getList("drives", Tag.TAG_COMPOUND).forEach(nbt -> {
            CompoundTag entry = (CompoundTag) nbt;
            driveSlots[entry.getInt("slot")] = new DriveSlot(entry.getCompound("drive"));
        });
    }

    protected void saveClientData(CompoundTag tag) {
        super.saveClientData(tag);
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
    }
}
