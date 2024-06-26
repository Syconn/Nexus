package mod.syconn.nexus.blockentities;

import mod.syconn.nexus.Registration;
import mod.syconn.nexus.blocks.DriveBlock;
import mod.syconn.nexus.util.data.PipeNetwork;
import mod.syconn.nexus.world.capabilities.DriveSlotHandler;
import mod.syconn.nexus.world.capabilities.IDriveHandler;
import mod.syconn.nexus.world.savedata.PipeNetworks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.Lazy;

public class DriveBE extends BasePipeBE {

    private static final String DRIVES_TAG = "Drives";
    private final DriveSlotHandler drives = new DriveSlotHandler(this, 10);
    private final Lazy<IDriveHandler> driveHandler = Lazy.of(() -> drives);

    public DriveBE(BlockPos pos, BlockState state) {
        super(Registration.DRIVE_BE.get(), pos, state);
    }

    public boolean addDrive(ItemStack stack) {
        boolean flag = drives.addDrive(stack);
        markDirty();
        return flag;
    }

    public ItemStack removeDrive() {
        ItemStack flag = drives.removeDrive();
        markDirty();
        return flag;
    }

    public void updateScreen() {
        if (level instanceof ServerLevel sl) PipeNetworks.get(sl).updateAllPoints(level, getUUID(), true);
    }

    public DriveSlotHandler getDrive() {
        return drives;
    }

    public IDriveHandler getDriveHandler() {
        return driveHandler.get();
    }

    public boolean canConnect(BlockPos pos, Direction conDir) {
        return !getBlockState().getValue(DriveBlock.FACING).getOpposite().equals(conDir);
    }

    public void tickServer() { }

    protected void loadClientData(CompoundTag tag) {
        super.loadClientData(tag);
        if (tag.contains(DRIVES_TAG)) drives.deserializeNBT(tag.getCompound(DRIVES_TAG));
    }

    protected void saveClientData(CompoundTag tag) {
        super.saveClientData(tag);
        tag.put(DRIVES_TAG, drives.serializeNBT());
    }
}
