package mod.syconn.nexus.blocks;

import mod.syconn.nexus.blockentities.BasePipeBE;
import mod.syconn.nexus.blockentities.ExternalStorageBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jetbrains.annotations.Nullable;

public class ExternalStorage extends PipeAttachmentBlock {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public ExternalStorage() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.RAW_IRON).requiresCorrectToolForDrops().strength(2.5F, 3.0F));
        registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(BlockStateProperties.WATERLOGGED, false));
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        VoxelShape shape = super.getShape(pState, pLevel, pPos, pContext);
        return switch (pState.getValue(FACING)) {
            case DOWN -> Shapes.join(shape, Block.box(2, 14, 2, 14, 16, 14), BooleanOp.OR);
            case UP -> Shapes.join(shape, Block.box(2, 0, 2, 14, 2, 14), BooleanOp.OR);
            case NORTH -> Shapes.join(shape, Block.box(2, 2, 14, 14, 14, 16), BooleanOp.OR);
            case SOUTH -> Shapes.join(shape, Block.box(2, 2, 0, 14, 14, 2), BooleanOp.OR);
            case WEST -> Shapes.join(shape, Block.box(14, 2, 2, 16, 14, 14), BooleanOp.OR);
            case EAST -> Shapes.join(shape, Block.box(0, 2, 2, 2, 14, 14), BooleanOp.OR);
        };
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(FACING);
    }

    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        BlockPos blockpos = pPos.relative(pState.getValue(FACING).getOpposite());
        return pLevel.getBlockEntity(blockpos) != null && pLevel.getBlockEntity(blockpos).getLevel().getCapability(Capabilities.ItemHandler.BLOCK, blockpos, pState.getValue(FACING).getOpposite()) != null;
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        if (super.getStateForPlacement(pContext).setValue(FACING, pContext.getClickedFace()).canSurvive(pContext.getLevel(), pContext.getClickedPos()))
            return super.getStateForPlacement(pContext).setValue(FACING, pContext.getClickedFace());
        return null;
    }

    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
        return !pState.canSurvive(pLevel, pPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos);
    }

    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Nullable
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ExternalStorageBE(pPos, pState);
    }
}
