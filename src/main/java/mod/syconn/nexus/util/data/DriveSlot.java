package mod.syconn.nexus.util.data;

import mod.syconn.nexus.Nexus;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class DriveSlot {

    private final List<ItemTypes> stored_items;
    private final int max_quantity;
    private int quantity;

    public DriveSlot(int max_quantity) {
        this.max_quantity = max_quantity;
        this.stored_items = new ArrayList<>();
    }

    public DriveSlot(CompoundTag tag) {
        max_quantity = tag.getInt("max");
        quantity = tag.getInt("quantity");
        stored_items = new ArrayList<>();
        if (tag.contains("list"))
            tag.getList("list", Tag.TAG_COMPOUND).forEach(nbt -> stored_items.add(new ItemTypes(((CompoundTag) nbt).getCompound("type"))));
    }

    public ItemStack addStack(ItemStack stack) {
        if (quantity < max_quantity) {
            int toAdd = canAddUpTo(stack);
            for (ItemTypes type : stored_items) {
                if (type.sameType(stack)) {
                    type.addStack(stack.copyWithCount(toAdd));
                    quantity += toAdd;
                    return stack.copyWithCount(toAdd);
                }
            }
            if (toAdd > 0) {
                stored_items.add(new ItemTypes(stack, toAdd));
                quantity += toAdd;
                return stack.copyWithCount(toAdd);
            }
        }
        return stack;
    }

    public ItemStack removeStack(ItemStack stack, boolean simulate) {
        List<ItemTypes> remove = new ArrayList<>();
        int amount = 0;
        for (ItemTypes type : stored_items) {
            if (type.sameType(stack)) {
                amount = type.extractStack(stack.getCount(), simulate);
                if (!simulate) quantity -= amount;
                if (type.amount <= 0) remove.add(type);
                break;
            }
        }
        stored_items.removeAll(remove);
        return stack.copyWithCount(amount);
    }

    public int canAddUpTo(ItemStack stack) {
        return Math.min(stack.getCount(), max_quantity - quantity);
    }

    public List<ItemTypes> getStacks() {
        return stored_items;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getMaxQuantity() {
        return max_quantity;
    }

    public int getColor() {
        return (quantity >= max_quantity) ? 2 : (quantity >= max_quantity / 2) ? 1 : 0;
    }

    public ResourceLocation getTexture() {
        String loc = quantity >= max_quantity ? "red" : quantity >= max_quantity / 2 ? "yellow" : "green";
        return new ResourceLocation(Nexus.MODID, "textures/entity/drive_" + loc + ".png");
    }

    public boolean isFull() {
        return quantity >= max_quantity;
    }

    public boolean isEmpty() {
        return quantity <= 0;
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("max", max_quantity);
        tag.putInt("quantity", quantity);
        ListTag list = new ListTag();
        stored_items.forEach(itemTypes -> {
            CompoundTag entry = new CompoundTag();
            entry.put("type", itemTypes.save());
            list.add(entry);
        });
        tag.put("list", list);
        return tag;
    }

    public static class ItemTypes {

        private final ItemStack id;
        private int amount;

        public ItemTypes(ItemStack id, int amount) {
            this.id = id.copyWithCount(1);
            this.amount = amount;
        }

        public ItemTypes(CompoundTag tag) {
            this.id = ItemStack.of(tag);
            this.amount = tag.getInt("amount");
        }

        public boolean sameType(ItemStack check) {
            return ItemStack.isSameItemSameTags(check, id);
        }

        private void addStack(ItemStack stack) {
            if (sameType(stack)) amount += stack.getCount();
        }

        private int extractStack(int size, boolean simulate) {
            int returnAmount = Math.min(amount, size);
            if (!simulate) amount = Math.max(amount - returnAmount, 0);
            return returnAmount;
        }

        private ItemTypes addType(ItemTypes itemType) {
            if (sameType(itemType.id)) amount += itemType.amount;
            return this;
        }

        private ItemTypes copy() {
            return new ItemTypes(id, amount);
        }

        public ItemStack getStack() {
            return id.copyWithCount(amount);
        }

        public static List<ItemStack> convertToStacks(List<ItemTypes> types) {
            List<ItemTypes> typeList = new ArrayList<>();
            List<ItemStack> stackList = new ArrayList<>();
            for (ItemTypes type1 : types) {
                boolean contained = false;
                for (int i = 0; i < typeList.size(); i++) {
                    if (type1.sameType(typeList.get(i).id)) {
                        contained = true;
                        typeList.set(i, typeList.get(i).addType(type1));
                    }
                }
                if (!contained) typeList.add(type1.copy());
            }
            for (ItemTypes type : typeList) stackList.add(type.getStack().copy());
            return stackList;
        }

        public CompoundTag save() {
            CompoundTag tag = new CompoundTag();
            tag.putInt("amount", amount);
            id.save(tag);
            return tag;
        }
    }
}
