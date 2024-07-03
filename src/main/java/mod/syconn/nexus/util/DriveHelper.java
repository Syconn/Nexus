package mod.syconn.nexus.util;

import mod.syconn.nexus.Registration;
import mod.syconn.nexus.util.data.DriveSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Map;

public class DriveHelper {

    public static ItemStack createDrive(int storage) {
        ItemStack stack = new ItemStack(Registration.STORAGE_DRIVE.get());
        stack.getOrCreateTag().put("data", new DriveSlot(storage).save());
        return stack;
    }

    public static ItemStack getStorageDrive(DriveSlot slot) {
        ItemStack stack = createDrive(slot.getMaxQuantity());
        stack.getOrCreateTag().put("data", slot.save());
        return stack;
    }

    public static DriveSlot getDriveSlot(ItemStack stack) {
        return new DriveSlot(stack.getOrCreateTag().getCompound("data"));
    }

    public static Map<Item, List<ItemStack>> combineMaps(Map<Item, List<ItemStack>> map, Map<Item, List<ItemStack>> map2) {
        for (Map.Entry<Item, List<ItemStack>> entry : map2.entrySet()) {
            if (map.containsKey(entry.getKey())) {
                List<ItemStack> stackList = map.get(entry.getKey());
                for (int j = 0; j < entry.getValue().size(); j++) {
                    boolean added = false;
                    for (int i = 0; i < stackList.size(); i++) {
                        if (ItemStack.isSameItemSameTags(stackList.get(i), entry.getValue().get(j))) {
                            stackList.set(i, stackList.get(i).copyWithCount(stackList.get(i).getCount() + entry.getValue().get(j).getCount()));
                            added = true;
                            break;
                        }
                    }
                    if (!added) stackList.add(entry.getValue().get(j));
                }
            } else {
                map.put(entry.getKey(), entry.getValue());
            }
        }
        return map;
    }
}
