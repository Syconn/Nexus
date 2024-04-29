package mod.syconn.nexus.blocks;

import mod.syconn.nexus.blockentities.NexusBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NexusBlock extends PipeAttachmentBlock {

    public NexusBlock() {
        super(Blocks.IRON_BLOCK.properties());
    }

    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new NexusBE(pPos, pState);
    }

    public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Shapes.block();
    }
}
