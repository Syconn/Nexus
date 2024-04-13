package mod.syconn.nexus.world.menu;

import mod.syconn.nexus.Registration;
import mod.syconn.nexus.blockentities.InterfaceBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;

public class InterfaceMenu extends AbstractContainerMenu {

    private final BlockPos pos;
    private final int SLOT_COUNT = 7;

    public InterfaceMenu(int windowId, Player player, BlockPos pos) {
        super(Registration.INTERFACE_MENU.get(), windowId);
        this.pos = pos;
        if (player.level().getBlockEntity(pos) instanceof InterfaceBE be) {
            int index = 0;
            for (int y = 0; y < 5; y++) {
                for (int x = 0; x < 9; x++) {
                    addSlot(new SlotItemHandler(be.getItems(), index, 9 + 18 * x, 18 + 18 * y));
                    index++;
                }
            }
        }
        layoutPlayerInventorySlots(player.getInventory(), 9, 122);
    }

    public BlockPos getPos() {
        return pos;
    }

    private void layoutPlayerInventorySlots(Container playerInventory, int leftCol, int topRow) {
        addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);
        topRow += 58;
        addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);
    }

    private int addSlotRange(Container playerInventory, int index, int x, int y, int amount, int dx) {
        for (int i = 0 ; i < amount ; i++) {
            addSlot(new Slot(playerInventory, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    private int addSlotBox(Container playerInventory, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0 ; j < verAmount ; j++) {
            index = addSlotRange(playerInventory, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }

    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem().copyWithCount(64);
            itemstack = itemstack1.copy();
            if (index < 45) {
                if (!this.moveItemStackTo(itemstack1, 45, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, 45, false)) {
                return ItemStack.EMPTY;
            }
            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(player.level(), pos), player, Registration.INTERFACE.get());
    }
}
