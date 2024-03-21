package mod.syconn.nexus.util.data;

import mod.syconn.nexus.Registration;
import mod.syconn.nexus.blockentities.ItemPipeBE;
import mod.syconn.nexus.util.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PipeNetwork {

    private List<BlockPos> pipes;
    private UUID uuid;

    public PipeNetwork(UUID uuid, List<BlockPos> pipes) {
        this.uuid = uuid;
        this.pipes = pipes;
    }

    public PipeNetwork(UUID uuid, BlockPos pipe) {
        this.uuid = uuid;
        this.pipes = new ArrayList<>();
        this.pipes.add(pipe);
    }

    public void addPositions(BlockPos... posses) {
        for (BlockPos pos : posses) if (!pipes.contains(pos)) pipes.add(pos);
    }

    public boolean removePosition(BlockPos pos) {
        pipes.remove(pos);
        return pipes.isEmpty();
    }

    public boolean removePosition(List<BlockPos> posList) {
        for (BlockPos pos : posList) pipes.remove(pos);
        return pipes.isEmpty();
    }

    public List<BlockPos> getPipes() {
        return pipes;
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.put("pipes", NBTHelper.writePosses(pipes));
        tag.putUUID("uuid", uuid);
        return tag;
    }

    public static PipeNetwork load(CompoundTag tag) {
        return new PipeNetwork(tag.getUUID("uuid"), NBTHelper.readPosses(tag.getCompound("pipes")));
    }
}
