package mod.syconn.nexus.blockentities;

import mod.syconn.nexus.Registration;
import mod.syconn.nexus.world.savedata.PipeNetworks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

public class NexusBE extends BasePipeBE {

    private Map<Block, Integer> blockTypes = new HashMap<>();

    public NexusBE(BlockPos pos, BlockState state) {
        super(Registration.NEXUS_BE.get(), pos, state);
    }

    public Map<Block, Integer> getBlocks() {
        return blockTypes;
    }

    public void createBlockList() {
        if (!level.isClientSide()) {
            blockTypes.clear();
            PipeNetworks.get((ServerLevel) level).getAllPipesByUUID(getUUID(), level).forEach(pos -> {
                Block block = level.getBlockState(pos).getBlock();
                if (blockTypes.containsKey(block)) {
                    blockTypes.put(block, blockTypes.get(block) + 1);
                } else {
                    blockTypes.put(block, 1);
                }
            });
            markDirty();
        }
    }

    public void tickServer() {}

    protected void saveClientData(CompoundTag tag) {
        super.saveClientData(tag);
        ListTag listTag = new ListTag();
        for (Map.Entry<Block, Integer> entry : blockTypes.entrySet()) {
            CompoundTag data = new CompoundTag();
            data.putString("block", BuiltInRegistries.BLOCK.getKey(entry.getKey()).toString());
            data.putInt("amount", entry.getValue());
            listTag.add(data);
        }
        tag.put("list", listTag);
    }

    protected void loadClientData(CompoundTag tag) {
        super.loadClientData(tag);
        blockTypes = new HashMap<>();
        if (tag.contains("list")) {
            tag.getList("list", Tag.TAG_COMPOUND).forEach(nbt -> {
                CompoundTag data = (CompoundTag) nbt;
                blockTypes.put(BuiltInRegistries.BLOCK.get(new ResourceLocation(data.getString("block"))), data.getInt("amount"));
            });
        }
    }
}
