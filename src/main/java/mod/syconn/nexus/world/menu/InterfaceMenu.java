package mod.syconn.nexus.world.menu;

import mod.syconn.nexus.Registration;
import mod.syconn.nexus.blockentities.InterfaceBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.SlotItemHandler;

public class InterfaceMenu extends AbstractContainerMenu {

    private final BlockPos pos;
    private IItemHandlerModifiable items;

    public InterfaceMenu(int windowId, Player player, BlockPos pos) {
        super(Registration.INTERFACE_MENU.get(), windowId);
        this.pos = pos;

        if (player.level().getBlockEntity(pos) instanceof InterfaceBE be) {
            items = be.getItems();
            int index = 0;
            for (int y = 0; y < 5; y++) {
                for (int x = 0; x < 9; x++) {
                    addSlot(new SlotItemHandler(items, index, 9 + 18 * x, 18 + 18 * y));
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
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < 45) {
                itemstack1 = itemstack.getCount() > itemstack.getMaxStackSize() ? itemstack1.copyWithCount(itemstack.getMaxStackSize()) : itemstack1.copy();
                itemstack = itemstack1.copy();
                if (!this.moveItemStackTo(itemstack1, 45, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
//                items.extractItem(index, 63, false);
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

    protected boolean moveItemStackTo(ItemStack pStack, int pStartIndex, int pEndIndex, boolean pReverseDirection) {
        boolean flag = false;
        int i = pStartIndex;
        if (pReverseDirection) {
            i = pEndIndex - 1;
        }
        if (pStack.isStackable()) {
            while(!pStack.isEmpty() && (pReverseDirection ? i >= pStartIndex : i < pEndIndex)) {
                Slot slot = this.slots.get(i);
                ItemStack itemstack = slot.getItem();
                if (!itemstack.isEmpty() && ItemStack.isSameItemSameTags(pStack, itemstack)) {
                    int j = itemstack.getCount() + pStack.getCount();
                    int maxSize = Math.min(slot.getMaxStackSize(), pStack.getMaxStackSize());
                    if (j <= maxSize) {
                        pStack.setCount(0);
                        if (i < 45) items.setStackInSlot(i, itemstack.copyWithCount(j));
                        else itemstack.setCount(j);
                        slot.setChanged();
                        flag = true;
                    } else if (itemstack.getCount() < maxSize) {
                        pStack.shrink(maxSize - itemstack.getCount());
                        if (i < 45) items.setStackInSlot(i, itemstack.copyWithCount(maxSize));
                        else itemstack.setCount(maxSize);
                        slot.setChanged();
                        flag = true;
                    }
                }
                if (pReverseDirection) {
                    --i;
                } else {
                    ++i;
                }
            }
        }
        if (!pStack.isEmpty()) {
            if (pReverseDirection) {
                i = pEndIndex - 1;
            } else {
                i = pStartIndex;
            }
            while(pReverseDirection ? i >= pStartIndex : i < pEndIndex) {
                Slot slot1 = this.slots.get(i);
                ItemStack itemstack1 = slot1.getItem();
                if (itemstack1.isEmpty() && slot1.mayPlace(pStack)) {
                    if (pStack.getCount() > slot1.getMaxStackSize()) {
                        slot1.setByPlayer(pStack.split(slot1.getMaxStackSize()));
                    } else {
                        slot1.setByPlayer(pStack.split(pStack.getCount()));
                    }

                    slot1.setChanged();
                    flag = true;
                    break;
                }

                if (pReverseDirection) {
                    --i;
                } else {
                    ++i;
                }
            }
        }
        return flag;
    }

    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(player.level(), pos), player, Registration.INTERFACE.get());
    }
}
