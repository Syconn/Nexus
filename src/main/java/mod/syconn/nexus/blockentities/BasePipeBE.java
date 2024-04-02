package mod.syconn.nexus.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public abstract class BasePipeBE extends SyncedBE {

    private UUID uuid = null;

    public BasePipeBE(BlockEntityType<?> pType, BlockPos pos, BlockState state) {
        super(pType, pos, state);
    }

    public abstract void tickServer();

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
