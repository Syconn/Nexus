package mod.syconn.nexus.blocks;

import mod.syconn.nexus.Nexus;
import mod.syconn.nexus.Registration;
import mod.syconn.nexus.blockentities.InterfaceBE;
import mod.syconn.nexus.world.menu.InterfaceMenu;
import mod.syconn.nexus.world.savedata.PipeNetworks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InterfaceBlock extends PipeAttachmentBlock implements EntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    public InterfaceBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.RAW_IRON).requiresCorrectToolForDrops().strength(2.5F, 3.0F));
        registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(ACTIVE, false));
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
        if (!pLevel.isClientSide) {
            BlockEntity be = pLevel.getBlockEntity(pPos);
            if (be instanceof InterfaceBE ie && pState.getValue(ACTIVE)) {
                ((InterfaceBE) be).updateScreen();
                MenuProvider containerProvider = new MenuProvider() {
                    public Component getDisplayName() {
                        return Component.literal("Nexus Screen");
                    }
                    public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) { return new InterfaceMenu(windowId, playerEntity, pPos); }
                };
                pPlayer.openMenu(containerProvider, buf -> buf.writeBlockPos(pPos));
            }
        }
        return InteractionResult.SUCCESS;
    }

    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(FACING);
        pBuilder.add(ACTIVE);
    }

    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        BlockPos blockpos = pPos.relative(pState.getValue(FACING).getOpposite());
        return pLevel.getBlockState(blockpos).is(Registration.PIPES);
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        if (super.getStateForPlacement(pContext).setValue(FACING, pContext.getClickedFace()).canSurvive(pContext.getLevel(), pContext.getClickedPos()))
            return super.getStateForPlacement(pContext).setValue(FACING, pContext.getClickedFace()).setValue(ACTIVE, false);
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

    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new InterfaceBE(pPos, pState);
    }
}