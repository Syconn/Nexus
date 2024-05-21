package mod.syconn.nexus.util;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class NBTHelper {

    public static CompoundTag savePositions(List<BlockPos> posses){
        CompoundTag tag = new CompoundTag();
        tag.putInt("size", posses.size());
        for (int i = 0; i < posses.size(); i++) {
            tag.put(String.valueOf(i), NbtUtils.writeBlockPos(posses.get(i)));
        }
        return tag;
    }

    public static List<BlockPos> loadPositions(CompoundTag nbt){
        List<BlockPos> posses = new ArrayList<>();
        for (int i = 0; i < nbt.getInt("size"); i++) {
            posses.add(NbtUtils.readBlockPos(nbt.getCompound(String.valueOf(i))));
        }
        return posses;
    }

    public static CompoundTag saveStacks(List<ItemStack> stacks) {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        stacks.forEach(stack -> {
            CompoundTag entry = new CompoundTag();
            stack.save(entry);
            list.add(entry);
        });
        tag.put("list", list);
        return tag;
    }

    public static List<ItemStack> loadStacks(CompoundTag tag) {
        List<ItemStack> stacks = new ArrayList<>();
        tag.getList("list", Tag.TAG_COMPOUND).forEach(nbt -> {
            CompoundTag entry = (CompoundTag) nbt;
            stacks.add(ItemStack.of(entry));
        });
        return stacks;
    }
}
