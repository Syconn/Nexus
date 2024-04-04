package mod.syconn.nexus.util.data;

import mod.syconn.nexus.util.ItemStackHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.List;

public class StoragePoint {

    private final BlockPos pos;
    private List<ItemStack> stacks;
    private List<FluidStack> liquids;

    public StoragePoint(BlockPos pos, List<ItemStack> stacks, List<FluidStack> liquids) {
        this.pos = pos;
        this.stacks = stacks;
        this.liquids = liquids;
    }

    public StoragePoint(CompoundTag tag) {
        pos = NbtUtils.readBlockPos(tag.getCompound("pos"));
        List<ItemStack> itemStacks = new ArrayList<>();
        tag.getList("items", Tag.TAG_COMPOUND).forEach(tag1 -> {
            CompoundTag nbt = (CompoundTag) tag1;
            itemStacks.add(ItemStack.of(nbt));
        });
        stacks = itemStacks;
        List<FluidStack> fluidStacks = new ArrayList<>();
        tag.getList("fluids", Tag.TAG_COMPOUND).forEach(tag1 -> {
            CompoundTag nbt = (CompoundTag) tag1;
            fluidStacks.add(FluidStack.loadFluidStackFromNBT(nbt));
        });
        liquids = fluidStacks;
    }

    public ItemStack addItemStack(Level level, ItemStack stack) {
        if (level.getBlockEntity(pos) != null && level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null) != null) {
            IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
            return ItemHandlerHelper.insertItem(handler, stack, false);
        }

        return stack;
    }

    public ItemStack requestItemStack(Level level, ItemStack stack) {
        if (level.getBlockEntity(pos) != null && level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null) != null) {
            IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
            return ItemStackHelper.removeStack(handler, stack, false);
        }

        return ItemStack.EMPTY;
    }

    public void update(Level level) {
        stacks.clear();
        liquids.clear();
        if (level.getBlockEntity(pos) == null) return; // TODO REMOVE
        if (level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null) != null) {
            stacks = ItemStackHelper.combineItemsInStorage(level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null));
        }
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.put("pos", NbtUtils.writeBlockPos(pos));
        ListTag items = new ListTag();
        for (ItemStack stack : stacks) {
            CompoundTag tag2 = new CompoundTag();
            stack.save(tag2);
            items.add(tag2);
        }
        tag.put("items", items);
        ListTag fluids = new ListTag();
        for (FluidStack stack : liquids) {
            CompoundTag tag2 = new CompoundTag();
            stack.writeToNBT(tag2);
            fluids.add(tag2);
        }
        tag.put("fluids", fluids);
        return tag;
    }

    public static StoragePoint load(CompoundTag tag) {
        return new StoragePoint(tag);
    }

    public BlockPos getPos() {
        return pos;
    }

    public List<ItemStack> getItems() {
        return stacks;
    }

    public List<FluidStack> getLiquids() {
        return liquids;
    }
}
