package mod.syconn.nexus.util;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static net.neoforged.neoforge.items.ItemHandlerHelper.canItemStacksStack;

public class ItemStackHelper {

    public static List<ItemStack> combineItemsInStorage(IItemHandler handler, @Nullable Item ignore) {
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

    @NotNull
    public static ItemStack removeStack(IItemHandlerModifiable dest, @NotNull ItemStack stack, boolean simulate) {
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
