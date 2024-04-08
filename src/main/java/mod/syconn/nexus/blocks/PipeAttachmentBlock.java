package mod.syconn.nexus.blocks;

import mod.syconn.nexus.Registration;
import mod.syconn.nexus.blockentities.BasePipeBE;
import mod.syconn.nexus.util.ConnectionType;
import mod.syconn.nexus.world.savedata.PipeNetworks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.ticks.ScheduledTick;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

import static mod.syconn.nexus.util.ConnectionType.*;

public abstract class PipeAttachmentBlock extends Block implements SimpleWaterloggedBlock, EntityBlock {

    public static final EnumProperty<ConnectionType> NORTH = EnumProperty.create("north", ConnectionType.class);
    public static final EnumProperty<ConnectionType> SOUTH = EnumProperty.create("south", ConnectionType.class);
    public static final EnumProperty<ConnectionType> WEST = EnumProperty.create("west", ConnectionType.class);
    public static final EnumProperty<ConnectionType> EAST = EnumProperty.create("east", ConnectionType.class);
    public static final EnumProperty<ConnectionType> UP = EnumProperty.create("up", ConnectionType.class);
    public static final EnumProperty<ConnectionType> DOWN = EnumProperty.create("down", ConnectionType.class);
//    public static final ModelProperty<BlockState> FACADEID = new ModelProperty<>(); //TODO REMOVE

    private static VoxelShape[] shapeCache = null;
    private static final VoxelShape SHAPE_CABLE_NORTH = Shapes.box(.3, .3, 0, .7, .7, .3);
    private static final VoxelShape SHAPE_CABLE_SOUTH = Shapes.box(.3, .3, .7, .7, .7, 1);
    private static final VoxelShape SHAPE_CABLE_WEST = Shapes.box(0, .3, .3, .3, .7, .7);
    private static final VoxelShape SHAPE_CABLE_EAST = Shapes.box(.7, .3, .3, 1, .7, .7);
    private static final VoxelShape SHAPE_CABLE_UP = Shapes.box(.3, .5, .3, .7, 1, .7);
    private static final VoxelShape SHAPE_CABLE_DOWN = Shapes.box(.3, 0, .3, .7, .7, .7);

    private static final VoxelShape SHAPE_BLOCK_NORTH = Shapes.box(.2, .2, 0, .8, .8, .1);
    private static final VoxelShape SHAPE_BLOCK_SOUTH = Shapes.box(.2, .2, .9, .8, .8, 1);
    private static final VoxelShape SHAPE_BLOCK_WEST = Shapes.box(0, .2, .2, .1, .8, .8);
    private static final VoxelShape SHAPE_BLOCK_EAST = Shapes.box(.9, .2, .2, 1, .8, .8);
    private static final VoxelShape SHAPE_BLOCK_UP = Shapes.box(.2, .9, .2, .8, 1, .8);
    private static final VoxelShape SHAPE_BLOCK_DOWN = Shapes.box(.2, 0, .2, .8, .1, .8);

    public PipeAttachmentBlock(Properties properties) {
        super(properties);
        makeShapes();
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, false));
    }

    private int calculateShapeIndex(ConnectionType north, ConnectionType south, ConnectionType west, ConnectionType east, ConnectionType up, ConnectionType down) {
        int l = ConnectionType.values().length;
        return ((((south.ordinal() * l + north.ordinal()) * l + west.ordinal()) * l + east.ordinal()) * l + up.ordinal()) * l + down.ordinal();
    }

    private void makeShapes() {
        if (shapeCache == null) {
            int length = ConnectionType.values().length;
            shapeCache = new VoxelShape[length * length * length * length * length * length];

            for (ConnectionType up : ConnectionType.values()) {
                for (ConnectionType down : ConnectionType.values()) {
                    for (ConnectionType north : ConnectionType.values()) {
                        for (ConnectionType south : ConnectionType.values()) {
                            for (ConnectionType east : ConnectionType.values()) {
                                for (ConnectionType west : ConnectionType.values()) {
                                    int idx = calculateShapeIndex(north, south, west, east, up, down);
                                    shapeCache[idx] = makeShape(north, south, west, east, up, down);
                                }
                            }
                        }
                    }
                }
            }

        }
    }

    private VoxelShape makeShape(ConnectionType north, ConnectionType south, ConnectionType west, ConnectionType east, ConnectionType up, ConnectionType down) {
        VoxelShape shape = Shapes.box(.3, .3, .3, .7, .7, .7);
        shape = combineShape(shape, north, SHAPE_CABLE_NORTH, SHAPE_BLOCK_NORTH);
        shape = combineShape(shape, south, SHAPE_CABLE_SOUTH, SHAPE_BLOCK_SOUTH);
        shape = combineShape(shape, west, SHAPE_CABLE_WEST, SHAPE_BLOCK_WEST);
        shape = combineShape(shape, east, SHAPE_CABLE_EAST, SHAPE_BLOCK_EAST);
        shape = combineShape(shape, up, SHAPE_CABLE_UP, SHAPE_BLOCK_UP);
        shape = combineShape(shape, down, SHAPE_CABLE_DOWN, SHAPE_BLOCK_DOWN);
        return shape;
    }

    private VoxelShape combineShape(VoxelShape shape, ConnectionType ConnectionType, VoxelShape cableShape, VoxelShape blockShape) {
        if (ConnectionType == CABLE) {
            return Shapes.join(shape, cableShape, BooleanOp.OR);
        } else if (ConnectionType == INPUT || ConnectionType == OUTPUT) {
            return Shapes.join(shape, Shapes.join(blockShape, cableShape, BooleanOp.OR), BooleanOp.OR);
        } else {
            return shape;
        }
    }

    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        ConnectionType north = getConnectorType(state, world, pos, Direction.NORTH);
        ConnectionType south = getConnectorType(state, world, pos, Direction.SOUTH);
        ConnectionType west = getConnectorType(state, world, pos, Direction.WEST);
        ConnectionType east = getConnectorType(state, world, pos, Direction.EAST);
        ConnectionType up = getConnectorType(state, world, pos, Direction.UP);
        ConnectionType down = getConnectorType(state, world, pos, Direction.DOWN);
        int index = calculateShapeIndex(north, south, west, east, up, down);
        return shapeCache[index];
    }

    public BlockState updateShape(BlockState state, @Nonnull Direction direction, @Nonnull BlockState neighbourState, @Nonnull LevelAccessor world, @Nonnull BlockPos current, @Nonnull BlockPos offset) {
        if (state.getValue(BlockStateProperties.WATERLOGGED)) {
            world.getFluidTicks().schedule(new ScheduledTick<>(Fluids.WATER, current, Fluids.WATER.getTickDelay(world), 0L));   // @todo 1.18 what is this last parameter exactly?
        }
        return calculateState(world, current, state);
    }

    public void setPlacedBy(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable LivingEntity placer, @Nonnull ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof BasePipeBE cable) {
            cable.markDirty();
        }
        BlockState blockState = calculateState(level, pos, state);
        if (state != blockState) {
            level.setBlockAndUpdate(pos, blockState);
        }
    }

    protected ConnectionType getConnectorType(BlockState state, BlockGetter world, BlockPos connectorPos, Direction facing) {
        BlockPos pos = connectorPos.relative(facing);
        BlockState state2 = world.getBlockState(pos);
        Block block = state2.getBlock();
        if (block instanceof PipeAttachmentBlock) {
            return CABLE;
        } else if (isConnectable(world, connectorPos, facing)) {
            return OUTPUT; // TODO MAKE INPUT/OUTPUT
        } else {
            return ConnectionType.NONE;
        }
    }

    public static boolean isConnectable(BlockGetter world, BlockPos connectorPos, Direction facing) {
        BlockPos pos = connectorPos.relative(facing);
        BlockState state = world.getBlockState(pos);
        BlockEntity te = world.getBlockEntity(pos);
        if (state.is(Registration.PIPE_CONNECTIVE)) return true;
        if (state.is(Registration.DIRECTIONAL_PIPE_CONNECTIVE) && state.getValue(InterfaceBlock.FACING) == facing) return true;
        if (state.isAir()) return false;
//        if (te == null) return false;
//        return te.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, pos, null) != null;
        return false;
    }

    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.WATERLOGGED, NORTH, SOUTH, EAST, WEST, UP, DOWN);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        return calculateState(world, pos, defaultBlockState()).setValue(BlockStateProperties.WATERLOGGED, world.getFluidState(pos).getType() == Fluids.WATER);
    }

    private BlockState calculateState(LevelAccessor world, BlockPos pos, BlockState state) {
        ConnectionType north = getConnectorType(state, world, pos, Direction.NORTH);
        ConnectionType south = getConnectorType(state, world, pos, Direction.SOUTH);
        ConnectionType west = getConnectorType(state, world, pos, Direction.WEST);
        ConnectionType east = getConnectorType(state, world, pos, Direction.EAST);
        ConnectionType up = getConnectorType(state, world, pos, Direction.UP);
        ConnectionType down = getConnectorType(state, world, pos, Direction.DOWN);
        return state.setValue(NORTH, north).setValue(SOUTH, south).setValue(WEST, west).setValue(EAST, east).setValue(UP, up).setValue(DOWN, down);
    }

    public FluidState getFluidState(BlockState state) {
        return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (!pLevel.isClientSide() && !(pNewState.getBlock() instanceof PipeAttachmentBlock)) PipeNetworks.get((ServerLevel) pLevel).removePipe(pLevel, pPos);
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pMovedByPiston) {
        if (!pLevel.isClientSide() && pLevel.getBlockEntity(pPos) instanceof BasePipeBE be) be.setUUID(PipeNetworks.get((ServerLevel) pLevel).addPipe(pLevel, pPos));
    }

    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pHand == InteractionHand.MAIN_HAND && !pLevel.isClientSide() && pPlayer.getItemInHand(pHand).isEmpty() && pLevel.getBlockEntity(pPos) instanceof BasePipeBE be) {
            if (!pLevel.getBlockState(pPos.below()).is(Blocks.STONE) && !pLevel.getBlockState(pPos.below()).is(Blocks.GREEN_CONCRETE)) {
                for (BlockPos pos : PipeNetworks.get((ServerLevel) pLevel).getAllPipesByUUID(be.getUUID(), pLevel)) {
                    if (!PipeNetworks.get((ServerLevel) pLevel).isStoragePoint(be.getUUID(), pos)) pLevel.setBlock(pos.below(), Blocks.STONE.defaultBlockState(), 2);
                    else pLevel.setBlock(pos.below(), Blocks.GREEN_CONCRETE.defaultBlockState(), 2);
                }
            }
            else {
                for (BlockPos pos : PipeNetworks.get((ServerLevel) pLevel).getAllPipesByUUID(be.getUUID(), pLevel)) {
                    pLevel.setBlock(pos.below(), Blocks.AIR.defaultBlockState(), 2);
                }
            }
            PipeNetworks.get((ServerLevel) pLevel).getAllPipesByUUID(be.getUUID(), pLevel);
        }
        return InteractionResult.PASS;
    }

    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) {
            return null;
        } else return (lvl, pos, st, be) -> {
            if (be instanceof BasePipeBE cable) {
                cable.tickServer();
            }
        };
    }
}
