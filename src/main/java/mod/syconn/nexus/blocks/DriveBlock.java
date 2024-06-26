package mod.syconn.nexus.blocks;

import mod.syconn.nexus.blockentities.DriveBE;
import mod.syconn.nexus.items.StorageDrive;
import mod.syconn.nexus.util.DriveHelper;
import mod.syconn.nexus.util.data.DriveSlot;
import mod.syconn.nexus.world.savedata.PipeNetworks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DriveBlock extends PipeAttachmentBlock {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public DriveBlock() {
        super(Blocks.DIAMOND_BLOCK.properties());
        registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        ItemStack stack = pPlayer.getItemInHand(pHand);
        if (!pLevel.isClientSide() && pHand == InteractionHand.MAIN_HAND && pLevel.getBlockEntity(pPos) instanceof DriveBE be) {
            if (stack.getItem() instanceof StorageDrive) {
                boolean flag = be.addDrive(stack);
                if (flag) pPlayer.setItemInHand(pHand, ItemStack.EMPTY);
                return flag ? InteractionResult.SUCCESS : InteractionResult.FAIL;
            }
            else if (stack.isEmpty()) {
                ItemStack flag = be.removeDrive().copy();
                pPlayer.setItemInHand(pHand, flag);
                return !flag.isEmpty() ? InteractionResult.SUCCESS : InteractionResult.FAIL;
            }
        }
        return InteractionResult.PASS;
    }

    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pMovedByPiston) {
        if (!pLevel.isClientSide()) {
            PipeNetworks network = PipeNetworks.get((ServerLevel) pLevel);
            UUID uuid = network.addPipe(pLevel, pPos);
            network.addStoragePoint(pLevel, pPos, pPos, uuid);
        }
    }

    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (pLevel.getBlockEntity(pPos) instanceof DriveBE be) {
            if (!pState.is(pNewState.getBlock())) for (DriveSlot driveSlot : be.getDrive().getDriveSlots()) if (driveSlot != null) Containers.dropItemStack(pLevel, pPos.getX(), pPos.getY(), pPos.getZ(), DriveHelper.getStorageDrive(driveSlot));
            pLevel.updateNeighbourForOutputSignal(pPos, pState.getBlock());
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new DriveBE(pPos, pState);
    }

    public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Shapes.block();
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(FACING);
    }

    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return super.getStateForPlacement(pContext).setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }
}
