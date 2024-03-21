package mod.syconn.nexus.blockentities;

import mod.syconn.nexus.Registration;
import mod.syconn.nexus.world.savedata.PipeNetworks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public class ItemPipeBE extends SavableBlockEntity {

    private UUID uuid = null;

    public ItemPipeBE(BlockPos pos, BlockState state) {
        super(Registration.ITEM_PIPE_BE.get(), pos, state);
    }

    public void tickServer() {
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
        markDirty();
    }

    public UUID getUUID() {
        return uuid;
    }

    protected void saveClientData(CompoundTag tag) {
        if(uuid != null) tag.putUUID("uuid", uuid);
    }

    protected void loadClientData(CompoundTag tag) {
        if (tag.contains("uuid")) uuid = tag.getUUID("uuid");
    }
}