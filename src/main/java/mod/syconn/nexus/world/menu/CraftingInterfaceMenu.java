package mod.syconn.nexus.world.menu;

import mod.syconn.nexus.Registration;
import mod.syconn.nexus.blockentities.AbstractInterfaceBE;
import mod.syconn.nexus.blockentities.CraftingInterfaceBE;
import mod.syconn.nexus.util.ItemStackHelper;
import mod.syconn.nexus.util.data.PipeNetwork;
import mod.syconn.nexus.util.data.StoragePoint;
import mod.syconn.nexus.world.capabilities.IDriveHandler;
import mod.syconn.nexus.world.menu.slots.HiddenItemHandlerSlot;
import mod.syconn.nexus.world.savedata.PipeNetworks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CrafterBlock;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.SlotItemHandler;

public class CraftingInterfaceMenu extends AbstractContainerMenu {

    protected final BlockPos pos;
    protected final Level level;
    private final Player player;
    private final CraftingContainer craftSlots = new TransientCraftingContainer(this, 3, 3);
    private final ResultContainer resultSlots = new ResultContainer();
    protected IItemHandlerModifiable items;
    private AbstractInterfaceBE be;

    public CraftingInterfaceMenu(int windowId, Player player, BlockPos pos) {
        super(Registration.CRAFTING_INTERFACE_MENU.get(), windowId);
        this.pos = pos;
        this.level = player.level();
        this.player = player;
        if (player.level().getBlockEntity(pos) instanceof CraftingInterfaceBE be) {
            this.be = be;
            this.items = be.getItems();
            int index = 0;
            for (int y = 0; y < 5; y++) {
                for (int x = 0; x < 9; x++) {
                    addSlot(new SlotItemHandler(items, index, 9 + 18 * x, 18 + 18 * y));
                    index++;
                }
            }
            addSlot(new HiddenItemHandlerSlot(items, 45, 9 + 18 * 6, 18 + 18));
        }
        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 3; ++j) {
                this.addSlot(new Slot(this.craftSlots, j + i * 3, 196 + j * 18, 54 + i * 18));
            }
        }
        addSlot(new ResultSlot(player, this.craftSlots, this.resultSlots, 0, 214, 154));
        layoutPlayerInventorySlots(player.getInventory(), 9, 122);
    }

    public void slotsChanged(Container pContainer) {
        super.slotsChanged(pContainer);
        if (!level.isClientSide) {
            ServerPlayer serverplayer = (ServerPlayer)player;
            ItemStack itemstack = CrafterBlock.getPotentialResults(level, this.craftSlots).map(p_307367_ -> p_307367_.assemble(this.craftSlots, level.registryAccess()).copy()).orElse(ItemStack.EMPTY).copy();
            resultSlots.setItem(0, itemstack);
            setRemoteSlot(0, itemstack);
            serverplayer.connection.send(new ClientboundContainerSetSlotPacket(containerId, incrementStateId(), 0, itemstack));
        }
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

    public void clicked(int pSlotId, int pButton, ClickType pClickType, Player pPlayer) {
        if (pSlotId > 0 && pSlotId < 46 && pClickType == ClickType.PICKUP && this.slots.get(pSlotId).getItem().is(getCarried().getItem())) return;
        super.clicked(pSlotId, pButton, pClickType, pPlayer);
    }

    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < 46 || index == 55) {
                itemstack1 = itemstack1.copyWithCount(Math.min(itemstack.getMaxStackSize(), itemstack.getCount()));
                itemstack = itemstack1.copy();
                if (!this.moveItemStackTo(itemstack1, 46, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
                if (index < 46) items.extractItem(index, itemstack.getCount(), false);
                else {
                    slot.onTake(player, itemstack);
                    slotsChanged(craftSlots);
                }
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
                if (!itemstack.isEmpty() && ItemStack.isSameItemSameTags(pStack, itemstack) && i < 46 && level != null && !level.isClientSide() && level.getBlockEntity(pos) instanceof CraftingInterfaceBE be) {
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
        return stillValid(ContainerLevelAccess.create(player.level(), pos), player, Registration.CRAFTING_INTERFACE.get());
    }

    public void removed(Player pPlayer) {
        super.removed(pPlayer);
        clearContainer(pPlayer, craftSlots);
    }

    protected void clearContainer(Player pPlayer, Container pContainer) {
        if (!pPlayer.isAlive() || pPlayer instanceof ServerPlayer && ((ServerPlayer) pPlayer).hasDisconnected()) {
            for (int j = 0; j < pContainer.getContainerSize(); ++j) {
                pPlayer.drop(pContainer.removeItemNoUpdate(j), false);
            }
        } else if (level != null && !level.isClientSide()) {
            for (int i = 0; i < pContainer.getContainerSize(); ++i) {
                PipeNetwork network = PipeNetworks.get((ServerLevel) level).getPipeNetwork(be.getUUID());
                ItemStack stack = pContainer.removeItemNoUpdate(i);
                for (StoragePoint point : network.getStoragePoints()) {
                    if (level.getCapability(Capabilities.ItemHandler.BLOCK, point.getInventoryPos(), null) != null) {
                        IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, point.getInventoryPos(), null);
                        stack = ItemHandlerHelper.insertItemStacked(handler, stack, false);
                    } else if (level.getCapability(Registration.DRIVE_HANDLER_BLOCK, point.getInventoryPos(), null) != null) {
                        IDriveHandler handler = level.getCapability(Registration.DRIVE_HANDLER_BLOCK, point.getInventoryPos(), null);
                        stack = handler.addStack(stack);
                    }
                    if (stack.isEmpty()) break;
                }
                Inventory inventory = pPlayer.getInventory();
                if (inventory.player instanceof ServerPlayer) {
                    inventory.placeItemBackInInventory(stack);
                }
            }
        }
    }
}
