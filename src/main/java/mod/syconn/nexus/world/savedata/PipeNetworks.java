package mod.syconn.nexus.world.savedata;

import mod.syconn.nexus.blockentities.BasePipeBE;
import mod.syconn.nexus.blockentities.ItemPipeBE;
import mod.syconn.nexus.util.data.PipeNetwork;
import mod.syconn.nexus.util.data.StoragePoint;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.*;

public class PipeNetworks extends SavedData {

    private Map<UUID, PipeNetwork> pipe_network = new HashMap<>();
    public UUID addPipe(Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof BasePipeBE temp && temp.getUUID() == null) {
            UUID uuid = UUID.randomUUID();
            pipe_network.put(uuid, new PipeNetwork(uuid, pos));
            if (level.getBlockEntity(pos) instanceof ItemPipeBE be) be.setUUID(uuid);
            for (Direction d : Direction.values()) {
                if (level.getBlockEntity(pos.relative(d)) instanceof ItemPipeBE be) {
                    if (be.getUUID() != null) {
                        conjoin(level, uuid, be.getUUID());
                    }
                }
            }
            setDirty();
            return uuid;
        }
        return ((BasePipeBE) level.getBlockEntity(pos)).getUUID();
    }

    private UUID newLine(Level level, UUID oldUUID, List<BlockPos> positions, List<StoragePoint> points) {
        if (pipe_network.containsKey(oldUUID) && pipe_network.get(oldUUID).removePosition(positions)) pipe_network.remove(oldUUID);
        UUID uuid = UUID.randomUUID();
        pipe_network.put(uuid, new PipeNetwork(uuid, positions));
        pipe_network.get(uuid).setStoragePoint(points);
        for (BlockPos pos : positions) if (level.getBlockEntity(pos) instanceof ItemPipeBE be) be.setUUID(uuid);
        return uuid;
    }

    private UUID conjoin(Level level, UUID... uuids) {
        UUID uuid = uuids[0];
        for (int i = 1; i < uuids.length; i++) {
            if (pipe_network.containsKey(uuids[i])) {
                pipe_network.get(uuid).addPositions(pipe_network.get(uuids[i]).getPipes().toArray(BlockPos[]::new));
                for (BlockPos pos : pipe_network.get(uuids[i]).getPipes().toArray(BlockPos[]::new)) {
                    if (level.getBlockEntity(pos) instanceof ItemPipeBE be) be.setUUID(uuid);
                }
                if (uuid != uuids[i]) pipe_network.remove(uuids[i]);
                setDirty();
            }
        }
        setDirty();
        return uuid;
    }

    public boolean removePipe(Level level, BlockPos pos) { // TODO get Storage Points to link
        boolean delete = false;
        if (level.getBlockEntity(pos) instanceof BasePipeBE be) {
            UUID uuid = be.getUUID();
            if (pipe_network.containsKey(uuid)) {
                delete = pipe_network.get(uuid).removePosition(pos);
                pipe_network.get(uuid).removeStoragePoint(pos);
                if (delete) pipe_network.remove(uuid);
            }
            validLine(level, uuid);
            setDirty();
        }
        return delete;
    }

    public void addStoragePoint(Level level, BlockPos pos, BlockPos inventoryPos, UUID uuid) {
        pipe_network.get(uuid).addStoragePoint(new StoragePoint(pos, inventoryPos, level));
    }

    private void validLine(Level level, UUID uuid) {
        if (pipe_network.containsKey(uuid)) {
            List<BlockPos> validPosList = new ArrayList<>();
            List<BlockPos> posList = pipe_network.get(uuid).getPipes();
            List<StoragePoint> testPoints = pipe_network.get(uuid).getStoragePoints();
            List<StoragePoint> storagePoints = new ArrayList<>();
            validPosList.add(posList.get(0));
            for (Direction d : Direction.values()) if (posList.contains(posList.get(0).relative(d))) validPosList.add(posList.get(0).relative(d));
            int lastSize = 0;
            while (lastSize < validPosList.size()) {
                lastSize = validPosList.size();
                List<BlockPos> testPos = List.of(validPosList.toArray(BlockPos[]::new));
                for (BlockPos pos : testPos) for (Direction d : Direction.values()) if (posList.contains(pos.relative(d)) && !validPosList.contains(pos.relative(d))) validPosList.add(pos.relative(d));
            }
            for (BlockPos pos : validPosList) for (StoragePoint point : testPoints) if (point.getPos().equals(pos)) storagePoints.add(point);
            newLine(level, uuid, validPosList, storagePoints);
            for (BlockPos pos : posList) {
                if (!validPosList.contains(pos)) {
                    validLine(level, uuid);
                    return;
                }
            }
        }
    }

    public List<BlockPos> getAllPipesByUUID(UUID uuid, Level level) {
        if (pipe_network.containsKey(uuid)) return pipe_network.get(uuid).getPipes();
        return List.of();
    }

    public boolean isStoragePoint(UUID uuid, BlockPos pos) {
        for (StoragePoint point : pipe_network.get(uuid).getStoragePoints()) {
            if (point.getPos().equals(pos)) return true;
        }
        return false;
    }

    public CompoundTag save(CompoundTag pCompoundTag) {
        ListTag network = new ListTag();
        pipe_network.forEach(((uuid, pipeNetwork) -> {
            CompoundTag tag = new CompoundTag();
            tag.putUUID("uuid", uuid);
            tag.put("network", pipeNetwork.save());
            network.add(tag);
        }));
        pCompoundTag.put("network", network);
        return pCompoundTag;
    }

    private static PipeNetworks create() {
        return new PipeNetworks();
    }

    private static PipeNetworks load(CompoundTag tag) {
        PipeNetworks data = create();
        Map<UUID, PipeNetwork> networkMap = new HashMap<>();
        tag.getList("network", Tag.TAG_COMPOUND).forEach(tag2 -> {
            CompoundTag nbt = (CompoundTag) tag2;
            networkMap.put(nbt.getUUID("uuid"), PipeNetwork.load(nbt.getCompound("network")));
        });
        data.pipe_network = networkMap;
        return data;
    }

    public static PipeNetworks get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(new SavedData.Factory<>(PipeNetworks::create, PipeNetworks::load), "pipe_network");
    }
}