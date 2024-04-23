package mod.syconn.nexus.util;

import mod.syconn.nexus.util.data.PipeNetwork;
import mod.syconn.nexus.util.data.StoragePoint;
import mod.syconn.nexus.world.savedata.PipeNetworks;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static net.neoforged.neoforge.items.ItemHandlerHelper.canItemStacksStack;

public class ItemStackHelper {

    public static ItemStack canAddItemStack(ItemStack pStack, ServerLevel level, UUID uuid) {
        PipeNetwork network = PipeNetworks.get(level).getPipeNetwork(uuid);
        ItemStack remainder = pStack.copy();
        for (StoragePoint point : network.getStoragePoints()) {
            IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, point.getInventoryPos(), null);
            remainder = ItemHandlerHelper.insertItemStacked(handler, remainder, true);
        }
        return remainder;
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

    public static ItemStack removeStack(IItemHandlerModifiable dest, ItemStack stack, boolean simulate) {
        if (dest == null || stack.isEmpty())
            return ItemStack.EMPTY;
        for (int i = 0; i < dest.getSlots(); i++) {
            ItemStack stack2 = dest.getStackInSlot(i).copy();
            if (canItemStacksStack(stack.copy(), stack2)) {
                int extract = Math.min(stack.getCount(), stack2.getMaxStackSize());
                if (stack2.getCount() <= extract) {
                    if (!simulate) {
                        dest.setStackInSlot(i, ItemStack.EMPTY);
                        return stack2;
                    }
                    else return stack2.copy();
                } else {
                    dest.setStackInSlot(i, ItemHandlerHelper.copyStackWithSize(stack2, stack2.getCount() - extract));
                    return ItemHandlerHelper.copyStackWithSize(stack2, extract);
                }
            }
        }
        return ItemStack.EMPTY;
    }
}
