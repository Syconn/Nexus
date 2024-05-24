package mod.syconn.nexus.blocks;

import mod.syconn.nexus.Nexus;
import mod.syconn.nexus.Registration;
import mod.syconn.nexus.blockentities.BasePipeBE;
import mod.syconn.nexus.blockentities.ExternalStorageBE;
import mod.syconn.nexus.util.ConnectionType;
import mod.syconn.nexus.util.CustomRender;
import mod.syconn.nexus.util.PipePatterns;
import mod.syconn.nexus.world.savedata.PipeNetworks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static mod.syconn.nexus.util.ConnectionType.*;

public class ExternalStorage extends PipeAttachmentBlock implements CustomRender {

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

    protected ConnectionType getConnectorType(BlockState state, BlockGetter world, BlockPos thisPos, Direction facing) {
//        if (state.getValue(FACING).getOpposite() == facing) return CABLE;
//        else if (world.getBlockEntity(thisPos.relative(facing)) != null &&
//                world.getBlockEntity(thisPos.relative(facing)).getLevel().getCapability(Capabilities.ItemHandler.BLOCK, thisPos.relative(facing), null) != null) return NONE;
        return super.getConnectorType(state, world, thisPos, facing);
    }

    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(FACING);
    }

    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pMovedByPiston) {
        if (!pLevel.isClientSide()) {
            PipeNetworks network = PipeNetworks.get((ServerLevel) pLevel);
            UUID uuid = network.addPipe(pLevel, pPos);
            network.addStoragePoint(pLevel, pPos, pPos.relative(pState.getValue(FACING).getOpposite()), uuid);
        }
    }

    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) { //TODO MAY BE WHY EXTRA STORAGE POINTS
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

    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        super.onNeighborChange(state, level, pos, neighbor);
        if (!level.isClientSide() && ((ServerLevel) level).getCapability(Capabilities.ItemHandler.BLOCK, neighbor, null) != null && level.getBlockEntity(pos) instanceof BasePipeBE be) {
            PipeNetworks.get((ServerLevel) level).updateAllPoints((Level) level, be.getUUID(), true);
        }
    }

    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    public Block getBlock() {
        return Registration.EXTERNAL_STORAGE_DUMMY.get();
    }

    @Nullable
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ExternalStorageBE(pPos, pState);
    }
}
