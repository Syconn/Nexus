package mod.syconn.nexus.blocks;

import mod.syconn.nexus.blockentities.CraftingInterfaceBE;
import mod.syconn.nexus.world.menu.CraftingInterfaceMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class CraftingInterface extends InterfaceBlock {

    public CraftingInterface() {}

    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide) {
            BlockEntity be = pLevel.getBlockEntity(pPos);
            if (be instanceof CraftingInterfaceBE && pState.getValue(ACTIVE)) {
                ((CraftingInterfaceBE) be).updateScreen();
                MenuProvider containerProvider = new MenuProvider() {
                    public Component getDisplayName() {
                        return Component.literal("Crafting Interface Screen");
                    }
                    public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) { return new CraftingInterfaceMenu(windowId, playerEntity, pPos); }
                };
                pPlayer.openMenu(containerProvider, buf -> buf.writeBlockPos(pPos));
            }
        }
        return InteractionResult.SUCCESS;
    }

    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new CraftingInterfaceBE(pPos, pState);
    }
}
