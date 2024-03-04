package mod.syconn.nexus.blockentities;

import mod.syconn.nexus.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ItemPipeBE extends BlockEntity {

    protected ItemPipeBE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public ItemPipeBE(BlockPos pos, BlockState state) {
        super(Registration.ITEM_PIPE_BE.get(), pos, state);
    }

    public void markDirty() {
//        traverse(worldPosition, cable -> cable.outputs = null);
    }

    public void tickServer() {

    }

//    // This is a generic function that will traverse all cables connected to this cable
//    // and call the given consumer for each cable.
//    private void traverse(BlockPos pos, Consumer<CableBlockEntity> consumer) {
//        Set<BlockPos> traversed = new HashSet<>();
//        traversed.add(pos);
//        consumer.accept(this);
//        traverse(pos, traversed, consumer);
//    }
}
