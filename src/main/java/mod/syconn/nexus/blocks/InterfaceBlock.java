package mod.syconn.nexus.blocks;

import mod.syconn.nexus.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class InterfaceBlock extends Block {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public InterfaceBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.RAW_IRON).requiresCorrectToolForDrops().strength(2.5F, 3.0F));
        registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return switch (pState.getValue(FACING)) {
            case DOWN -> Block.box(2, 14, 2, 14, 16, 14);
            case UP -> Block.box(2, 0, 2, 14, 2, 14);
            case NORTH -> Block.box(2, 2, 14, 14, 14, 16);
            case SOUTH -> Block.box(2, 2, 0, 14, 14, 2);
            case WEST -> Block.box(14, 2, 2, 16, 14, 14);
            case EAST -> Block.box(0, 2, 2, 2, 14, 14);
        };
    }

    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {

        return InteractionResult.PASS;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        BlockPos blockpos = pPos.relative(pState.getValue(FACING).getOpposite());
        return pLevel.getBlockState(blockpos).is(Registration.PIPES);
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
}