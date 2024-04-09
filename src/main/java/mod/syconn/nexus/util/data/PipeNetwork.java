package mod.syconn.nexus.util.data;

import mod.syconn.nexus.util.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PipeNetwork {

    private final List<BlockPos> pipes;
    private List<StoragePoint> storagePoints;
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

    public void setStoragePoint(List<StoragePoint> points) {
        storagePoints = points;
    }

    public void addPositions(BlockPos... posses) {
        for (BlockPos pos : posses) if (!pipes.contains(pos)) pipes.add(pos);
    }

    public void addStoragePoint(StoragePoint point) {
        for (StoragePoint point2 : storagePoints) {
            if (point.matches(point2)) return;
        }
        storagePoints.add(point);
    }

    public void addStoragePoints(List<StoragePoint> point) {
        storagePoints.addAll(point);
    }

    public boolean removePosition(BlockPos pos) {
        pipes.remove(pos);
        return pipes.isEmpty();
    }

    public boolean removePosition(List<BlockPos> posList) {
        for (BlockPos pos : posList) pipes.remove(pos);
        return pipes.isEmpty();
    }

    public void removeStoragePoint(BlockPos pos) {
        for (int i = 0; i < storagePoints.size(); i++) {
            if (storagePoints.get(i).getPos().equals(pos)) {
                storagePoints.remove(storagePoints.get(i));
                return;
            }
        }
    }

    public List<BlockPos> getPipes() {
        return pipes;
    }

    public List<StoragePoint> getStoragePoints() {
        return storagePoints;
    }

    public void updateAllPoints(Level level) {
        storagePoints.forEach(p -> p.update(level));
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.put("pipes", NBTHelper.writePosses(pipes));
        tag.putUUID("uuid", uuid);
        ListTag listTag = new ListTag();
        for (StoragePoint point : storagePoints) {
            CompoundTag tag2 = new CompoundTag();
            tag2.put("point", point.save());
            listTag.add(tag2);
        }
        tag.put("points", listTag);
        return tag;
    }

    public static PipeNetwork load(CompoundTag tag) {
        PipeNetwork network = new PipeNetwork(tag.getUUID("uuid"), NBTHelper.readPosses(tag.getCompound("pipes")));
        List<StoragePoint> points = new ArrayList<>();
        tag.getList("points", Tag.TAG_COMPOUND).forEach(tag2 -> {
            CompoundTag nbt = (CompoundTag) tag2;
            points.add(new StoragePoint(nbt.getCompound("point")));
        });
        network.setStoragePoint(points);
        return network;
    }
}
