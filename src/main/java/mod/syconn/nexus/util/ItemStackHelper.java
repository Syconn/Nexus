package mod.syconn.nexus.util;

import mod.syconn.nexus.Registration;
import mod.syconn.nexus.util.data.DriveSlot;
import mod.syconn.nexus.util.data.PipeNetwork;
import mod.syconn.nexus.util.data.StoragePoint;
import mod.syconn.nexus.world.capabilities.IDriveHandler;
import mod.syconn.nexus.world.savedata.PipeNetworks;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static net.neoforged.neoforge.items.ItemHandlerHelper.canItemStacksStack;

public class ItemStackHelper {

    public static ItemStack canAddItemStack(ItemStack pStack, ServerLevel level, UUID uuid, boolean simulated) {
        PipeNetwork network = PipeNetworks.get(level).getPipeNetwork(uuid);
        ItemStack remainder = pStack.copy();
        int addTotal = pStack.getCount();
        for (StoragePoint point : network.getStoragePoints()) {
            if (level.getCapability(Capabilities.ItemHandler.BLOCK, point.getInventoryPos(), null) != null) {
                IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, point.getInventoryPos(), null);
                remainder = ItemHandlerHelper.insertItemStacked(handler, remainder, simulated);
                addTotal = remainder.getCount();
            } else if (level.getCapability(Registration.DRIVE_HANDLER_BLOCK, point.getInventoryPos(), null) != null) {
                IDriveHandler handler = level.getCapability(Registration.DRIVE_HANDLER_BLOCK, point.getInventoryPos(), null);
                if (!simulated) remainder = handler.addStack(remainder);
                else {
                    for (DriveSlot driveSlot : handler.getDriveSlots()) {
                        if (driveSlot != null) {
                            addTotal -= driveSlot.canAddUpTo(pStack.copyWithCount(addTotal));
                            if (addTotal <= 0) return ItemStack.EMPTY;
                        }
                    }
                }
            }
        }
        if (simulated) return remainder.copyWithCount(addTotal);
        return remainder;
    }

    public static boolean canAddItemStack(ItemStack pStack, ServerLevel level, UUID uuid) {
        PipeNetwork network = PipeNetworks.get(level).getPipeNetwork(uuid);
        int addTotal = pStack.getCount();
        for (StoragePoint point : network.getStoragePoints()) {
            if (level.getCapability(Capabilities.ItemHandler.BLOCK, point.getInventoryPos(), null) != null) {
                IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, point.getInventoryPos(), null);
                ItemStack insertStack = pStack.copy();
                for (int i = 0; i < handler.getSlots(); i++) {
                    insertStack = handler.insertItem(i, insertStack, true);
                    if (insertStack.getCount() < pStack.getCount()) return true;
                }
            } else if (level.getCapability(Registration.DRIVE_HANDLER_BLOCK, point.getInventoryPos(), null) != null) {
                IDriveHandler handler = level.getCapability(Registration.DRIVE_HANDLER_BLOCK, point.getInventoryPos(), null);
                for (DriveSlot driveSlot : handler.getDriveSlots()) {
                    if (driveSlot != null) {
                        addTotal -= driveSlot.canAddUpTo(pStack.copyWithCount(addTotal));
                        if (addTotal <= 0) return true;
                    }
                }
            }
        }
        return addTotal < pStack.getCount();
    }

    public static List<ItemStack> combineItemsInStorage(IItemHandler handler, Item ignore) {
        List<ItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (!stack.is(ignore)) {
                boolean added = false;
                for (int u = 0; u < stacks.size(); u++) {
                    if (canItemStacksStack(stacks.get(u), stack)) {
                        stacks.set(u, stacks.get(u).copyWithCount(stacks.get(u).getCount() + stack.getCount()));
                        added = true;
                    }
                }
                if (!added) stacks.add(stack);
            }
        }
        return stacks;
    }

    public static ItemStack removeStack(IItemHandler handler, ItemStack pStack, boolean simulate) {
        ItemStack returnStack = pStack.copy();
        for (int i = 0; i < handler.getSlots(); i++) {
            if (canItemStacksStack(returnStack, handler.getStackInSlot(i))) returnStack.shrink(handler.extractItem(i, pStack.getCount(), simulate).getCount());
            if (returnStack.isEmpty()) return ItemStack.EMPTY;
        }
        return pStack.copyWithCount(pStack.getCount() - returnStack.getCount());
    }
}
