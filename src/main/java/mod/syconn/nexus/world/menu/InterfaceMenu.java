package mod.syconn.nexus.world.menu;

import mod.syconn.nexus.Registration;
import mod.syconn.nexus.blockentities.AbstractInterfaceBE;
import mod.syconn.nexus.util.ItemStackHelper;
import mod.syconn.nexus.world.menu.slots.HiddenItemHandlerSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.SlotItemHandler;

public class InterfaceMenu extends AbstractContainerMenu {

    protected final BlockPos pos;
    protected final Level level;
    protected IItemHandlerModifiable items;

    public InterfaceMenu(MenuType<?> pMenuType, int windowId, Player player, BlockPos pos) {
        super(pMenuType, windowId);
        this.pos = pos;
        this.level = player.level();
        if (player.level().getBlockEntity(pos) instanceof AbstractInterfaceBE be) {
            items = be.getItems();
            int index = 0;
            for (int y = 0; y < 5; y++) {
                for (int x = 0; x < 9; x++) {
                    addSlot(new SlotItemHandler(items, index, 9 + 18 * x, 18 + 18 * y));
                    index++;
                }
            }
            addSlot(new HiddenItemHandlerSlot(items, 45, 9 + 18 * 6, 18 + 18));
        }
        layoutPlayerInventorySlots(player.getInventory(), 9, 122);
    }

    public InterfaceMenu(int windowId, Player player, BlockPos pos) {
        this(Registration.INTERFACE_MENU.get(), windowId, player, pos);
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
            if (index < 46) {
                itemstack1 = itemstack.getCount() > itemstack.getMaxStackSize() ? itemstack1.copyWithCount(itemstack.getMaxStackSize()) : itemstack1.copy();
                itemstack = itemstack1.copy();
                if (!this.moveItemStackTo(itemstack1, 46, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
                items.extractItem(index, 63, false);
            } else if (!this.moveItemStackTo(itemstack1, 0, 46, false)) {
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
        boolean handled = false;
        int i = pStartIndex;
        if (pReverseDirection) i = pEndIndex - 1;
        if (pStack.isStackable()) {
            while(!pStack.isEmpty() && (pReverseDirection ? i >= pStartIndex : i < pEndIndex)) {
                Slot slot = this.slots.get(i);
                ItemStack itemstack = slot.getItem();
                if (!itemstack.isEmpty() && ItemStack.isSameItemSameTags(pStack, itemstack) && i < 46 && level != null && !level.isClientSide() && level.getBlockEntity(pos) instanceof AbstractInterfaceBE be) {
                    ItemStack remainder = ItemStackHelper.canAddItemStack(pStack, (ServerLevel) level, be.getUUID(), true);
                    if (!ItemStack.matches(pStack, remainder)) {
                        items.setStackInSlot(i, itemstack.copyWithCount(itemstack.getCount() + (pStack.getCount() - remainder.getCount())));
                        pStack.setCount(remainder.getCount());
                        slot.setChanged();
                        flag = true;
                    }
                    handled = true;
                    break;
                }
                if (pReverseDirection) --i;
                else ++i;
            }
        }
        if (!pStack.isEmpty() && !handled) {
            if (pReverseDirection) i = pEndIndex - 1;
            else i = pStartIndex;
            while(pReverseDirection ? i >= pStartIndex : i < pEndIndex) {
                Slot slot1 = this.slots.get(i);
                ItemStack itemstack1 = slot1.getItem();
                if (itemstack1.isEmpty() && slot1.mayPlace(pStack)) {
                    if (pStack.getCount() > slot1.getMaxStackSize()) slot1.setByPlayer(pStack.split(slot1.getMaxStackSize()));
                    else slot1.setByPlayer(pStack.split(pStack.getCount()));
                    slot1.setChanged();
                    flag = true;
                    break;
                }
                if (pReverseDirection) --i;
                else ++i;
            }
        }
        return flag;
    }

    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(player.level(), pos), player, Registration.INTERFACE.get());
    }
}
