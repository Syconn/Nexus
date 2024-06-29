package mod.syconn.nexus.blocks;

import mod.syconn.nexus.blockentities.NexusBE;
import mod.syconn.nexus.client.screen.NetworkManagerScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class NexusBlock extends PipeAttachmentBlock {

    public NexusBlock() {
        super(Blocks.IRON_BLOCK.properties());
    }

    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pHand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
        if (!pLevel.isClientSide()) return InteractionResult.SUCCESS;
        if (pLevel.getBlockEntity(pPos) instanceof NexusBE be) Minecraft.getInstance().setScreen(new NetworkManagerScreen(be.getBlocks()));
        return InteractionResult.FAIL;
    }

    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new NexusBE(pPos, pState);
    }

    public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Shapes.block();
    }
}
