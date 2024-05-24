package mod.syconn.nexus.world.savedata;

import mod.syconn.nexus.blockentities.BasePipeBE;
import mod.syconn.nexus.blocks.NexusBlock;
import mod.syconn.nexus.util.data.PipeNetwork;
import mod.syconn.nexus.util.data.StoragePoint;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.*;

public class PipeNetworks extends SavedData {

    private Map<UUID, PipeNetwork> pipe_network = new HashMap<>();

    public PipeNetwork getPipeNetwork(UUID uuid) {
        return pipe_network.get(uuid);
    }

    public UUID addPipe(Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof BasePipeBE temp && temp.getUUID() == null) {
            UUID uuid = UUID.randomUUID();
            pipe_network.put(uuid, new PipeNetwork(uuid, pos));
            temp.setUUID(uuid);
            for (Direction d : Direction.values()) if (level.getBlockEntity(pos.relative(d)) instanceof BasePipeBE be && be.canConnect(pos, d)
                    && temp.canConnect(pos.relative(d), d.getOpposite()) && be.getUUID() != null && !be.getUUID().equals(uuid)) uuid = conjoin(level, uuid, be.getUUID());
            setDirty();
            return uuid;
        }
        return ((BasePipeBE) level.getBlockEntity(pos)).getUUID();
    }

    private UUID newLine(Level level, UUID oldUUID, List<BlockPos> positions, List<StoragePoint> points) {
        pipe_network.get(oldUUID).updateAllPoints(level, true);
        setDirty();
        if (pipe_network.containsKey(oldUUID) && pipe_network.get(oldUUID).removePosition(positions)) pipe_network.remove(oldUUID);
        UUID uuid = UUID.randomUUID();
        pipe_network.put(uuid, new PipeNetwork(uuid, positions));
        pipe_network.get(uuid).setStoragePoint(points);
        for (BlockPos pos : positions) if (level.getBlockEntity(pos) instanceof BasePipeBE be) be.setUUID(uuid);
        return uuid;
    }

    private UUID conjoin(Level level, UUID... uuids) {
        UUID uuid = uuids[0];
        pipe_network.get(uuid).updateAllPoints(level, true);
        setDirty();
        for (int i = 1; i < uuids.length; i++) {
            if (pipe_network.containsKey(uuids[i])) {
                pipe_network.get(uuid).addPositions(pipe_network.get(uuids[i]).getPipes().toArray(BlockPos[]::new));
                for (BlockPos pos : pipe_network.get(uuids[i]).getPipes().toArray(BlockPos[]::new)) if (level.getBlockEntity(pos) instanceof BasePipeBE be) be.setUUID(uuid);
                pipe_network.get(uuids[i]).updateAllPoints(level, true);
                pipe_network.get(uuid).addStoragePoints(pipe_network.get(uuids[i]).getStoragePoints());
                pipe_network.remove(uuids[i]);
                setDirty();
            }
        }
        setDirty();
        return uuid;
    }

    public boolean removePipe(Level level, BlockPos pos) {
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

    public void addStoragePoint(Level level, BlockPos pos, BlockPos inventoryPos, UUID uuid) {
        if (pipe_network.containsKey(uuid)) {
            pipe_network.get(uuid).addStoragePoint(new StoragePoint(pos, inventoryPos, level));
            setDirty();
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

    public List<BlockPos> getNexusBlocks(Level level, UUID uuid) {
        if (pipe_network.containsKey(uuid)) return pipe_network.get(uuid).getNexusPoints(level);
        return List.of();
    }

    public Map<Item, Map<BlockPos, List<ItemStack>>> getItemsOnNetwork(Level level, UUID uuid, boolean update) {
        updateAllPoints(level, uuid, update);
        Map<Item, Map<BlockPos, List<ItemStack>>> map = new HashMap<>();
        if (pipe_network.containsKey(uuid)) {
            for(StoragePoint point : pipe_network.get(uuid).getStorageLocations()) {
                for (ItemStack stack : point.getItems()) {
                    if (map.containsKey(stack.getItem())) {
                        if (map.get(stack.getItem()).containsKey(point.getInventoryPos())) {
                            List<ItemStack> stacks = new ArrayList<>(map.get(stack.getItem()).get(point.getInventoryPos()));
                            stacks.add(stack);
                            map.get(stack.getItem()).put(point.getInventoryPos(), stacks);
                        } else {
                            Map<BlockPos, List<ItemStack>> m = map.get(stack.getItem());
                            m.put(point.getInventoryPos(), List.of(stack));
                            map.put(stack.getItem(), m);
                        }
                    } else map.put(stack.getItem(), new HashMap<>(Map.of(point.getInventoryPos(), List.of(stack))));
                }
            }
        }
        return map;
    }

    public void updateAllPoints(Level level, UUID uuid, boolean update) {
        if (pipe_network.containsKey(uuid)) pipe_network.get(uuid).updateAllPoints(level, update);
    }

    private static PipeNetworks create() {
        return new PipeNetworks();
    }

    public static PipeNetworks get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(new SavedData.Factory<>(PipeNetworks::create, PipeNetworks::load), "pipe_network");
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
}