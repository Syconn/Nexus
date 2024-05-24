package mod.syconn.nexus.util.data;

import com.google.common.collect.Lists;
import mod.syconn.nexus.Nexus;
import mod.syconn.nexus.util.NBTHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DriveSlot {

    private final Map<Item, List<ItemStack>> stored_items;
    private final int max_quantity;
    private int storage_quantity;

    public DriveSlot(int max_quantity) {
        this.max_quantity = max_quantity;
        this.stored_items = new HashMap<>();
    }

    public DriveSlot(CompoundTag tag) {
        max_quantity = tag.getInt("max");
        storage_quantity = tag.getInt("quantity");
        stored_items = new HashMap<>();
        tag.getList("list", Tag.TAG_COMPOUND).forEach(nbt -> {
            CompoundTag entry = (CompoundTag) nbt;
            stored_items.put(BuiltInRegistries.ITEM.get(new ResourceLocation(entry.getString("item"))), NBTHelper.loadStacks(entry.getCompound("stacks")));
        });
    }

    public ItemStack addStack(ItemStack stack) {
        if (storage_quantity < max_quantity) {
            if (stored_items.containsKey(stack.getItem())) {
                List<ItemStack> stackList = stored_items.get(stack.getItem());
                for (int i = 0; i < stackList.size(); i++) {
                    if (ItemStack.isSameItemSameTags(stackList.get(i), stack)) {
                        stackList.set(i, stack.copyWithCount(stackList.get(i).getCount() + (Math.min(stack.getCount(), max_quantity - storage_quantity))));
                        stored_items.put(stack.getItem(), stackList);
                        storage_quantity += Math.min(stack.getCount(), max_quantity - storage_quantity);
                        return stack.copyWithCount(Math.min(stack.getCount(), max_quantity - storage_quantity));
                    }
                }
                stackList.add(stack.copyWithCount(Math.min(stack.getCount(), max_quantity - storage_quantity)));
                stored_items.put(stack.getItem(), stackList);
            } else {
                stored_items.put(stack.getItem(), Lists.newArrayList(stack.copyWithCount(Math.min(stack.getCount(), max_quantity - storage_quantity))));
            }
            storage_quantity += Math.min(stack.getCount(), max_quantity - storage_quantity);
            return stack.copyWithCount(Math.min(stack.getCount(), max_quantity - storage_quantity));
        }
        return stack;
    }

    public ItemStack removeItem(ItemStack stack) {
        if (stored_items.containsKey(stack.getItem())) {
            List<ItemStack> stackList = stored_items.get(stack.getItem());
            for (int i = 0; i < stackList.size(); i++) {
                if (ItemStack.isSameItemSameTags(stackList.get(i), stack) && stackList.get(i).getCount() >= stack.getCount()) {
                    stackList.set(i, stack.copyWithCount(stackList.get(i).getCount() - stack.getCount()));
                    storage_quantity -= stackList.get(i).getCount() - stack.getCount();
                    return stack;
                } else return ItemStack.EMPTY;
            }
        }
        return ItemStack.EMPTY;
    }

    public int getQuantity() {
        return storage_quantity;
    }

    public int getMaxQuantity() {
        return max_quantity;
    }

    public int getColor() {
        return (storage_quantity >= max_quantity) ? 2 : (storage_quantity >= max_quantity / 2) ? 1 : 0;
    }

    public Map<Item, List<ItemStack>> getStoredItems() {
        return stored_items;
    }

    public ResourceLocation getTexture() {
        String loc = storage_quantity >= max_quantity ? "red" : storage_quantity >= max_quantity / 2 ? "yellow" : "green";
        return new ResourceLocation(Nexus.MODID, "textures/entity/drive_" + loc + ".png");
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("max", max_quantity);
        tag.putInt("quantity", storage_quantity);
        ListTag list = new ListTag();
        stored_items.forEach((item, stackedList) -> {
            CompoundTag entry = new CompoundTag();
            entry.putString("item", BuiltInRegistries.ITEM.getKey(item).toString());
            entry.put("stacks", NBTHelper.saveStacks(stackedList));
            list.add(entry);
        });
        tag.put("list", list);
        return tag;
    }
}
