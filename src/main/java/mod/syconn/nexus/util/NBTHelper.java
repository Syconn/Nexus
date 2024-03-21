package mod.syconn.nexus.util;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;

import java.util.ArrayList;
import java.util.List;

public class NBTHelper {

    public static CompoundTag writePosses(List<BlockPos> posses){
        CompoundTag tag = new CompoundTag();
        tag.putInt("size", posses.size());
        for (int i = 0; i < posses.size(); i++) {
            tag.put(String.valueOf(i), NbtUtils.writeBlockPos(posses.get(i)));
        }
        return tag;
    }

    public static List<BlockPos> readPosses(CompoundTag nbt){
        List<BlockPos> posses = new ArrayList<>();
        for (int i = 0; i < nbt.getInt("size"); i++) {
            posses.add(NbtUtils.readBlockPos(nbt.getCompound(String.valueOf(i))));
        }
        return posses;
    }
}
