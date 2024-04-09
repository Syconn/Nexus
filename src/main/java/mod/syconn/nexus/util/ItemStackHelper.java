package mod.syconn.nexus.util;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ItemStackHelper {

    public static List<ItemStack> combineItemsInStorage(IItemHandler handler, @Nullable Item ignore) {
        List<ItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (!stack.is(ignore)) {
                boolean added = false;
                for (int u = 0; u < stacks.size(); u++) {
                    if (ItemHandlerHelper.canItemStacksStack(stacks.get(u), stack)) {
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
    public static ItemStack removeStack(IItemHandler dest, @NotNull ItemStack stack, boolean simulate) {
        ItemStack result = stack.copyWithCount(0);
        if (dest == null || stack.isEmpty())
            return stack;

        for (int i = 0; i < dest.getSlots(); i++) {
            ItemStack stack2 = dest.extractItem(i, stack.getCount(), simulate);
            stack = stack.copyWithCount(stack.getCount() - stack2.getCount());
            result.setCount(result.getCount() + stack2.getCount());
            if (stack.isEmpty()) {
                return result;
            }
        }

        return result;
    }
}
