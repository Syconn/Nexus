package mod.syconn.nexus.util.data;

import mod.syconn.nexus.util.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PipeNetwork {

    private final List<BlockPos> pipes;
    private final List<StoragePoint> storagePoints;
    private final UUID uuid;


    public PipeNetwork(UUID uuid, List<BlockPos> pipes) {
        this.uuid = uuid;
        this.pipes = pipes;
        this.storagePoints = new ArrayList<>();
    }

    public PipeNetwork(UUID uuid, BlockPos pipe) {
        this.uuid = uuid;
        this.pipes = new ArrayList<>();
        this.pipes.add(pipe);
        this.storagePoints = new ArrayList<>();
    }

    public void addPositions(BlockPos... posses) {
        for (BlockPos pos : posses) if (!pipes.contains(pos)) pipes.add(pos);
    }

    public void addStoragePoint(StoragePoint point) {
        storagePoints.add(point);
    }

    public boolean removePosition(BlockPos pos) {
        pipes.remove(pos);
        return pipes.isEmpty();
    }

    public boolean removePosition(List<BlockPos> posList) {
        for (BlockPos pos : posList) pipes.remove(pos);
        return pipes.isEmpty();
    }

    public void removeStoragePoint(StoragePoint point) {
        storagePoints.remove(point);
    }

    public List<BlockPos> getPipes() {
        return pipes;
    }

    public List<StoragePoint> getStoragePoints() {
        return storagePoints;
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.put("pipes", NBTHelper.writePosses(pipes));
        tag.putUUID("uuid", uuid);
        // TODO SAVE POINTS
        return tag;
    }

    public static PipeNetwork load(CompoundTag tag) {
        return new PipeNetwork(tag.getUUID("uuid"), NBTHelper.readPosses(tag.getCompound("pipes")));
    }
}
