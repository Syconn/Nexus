package mod.syconn.nexus.util.data;

import mod.syconn.nexus.Registration;
import mod.syconn.nexus.util.ItemStackHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class StoragePoint {

    private final BlockPos inventoryPos;
    private final BlockPos pos;
    private List<ItemStack> stacks = new ArrayList<>();
    private List<FluidStack> liquids = new ArrayList<>();

    public StoragePoint(BlockPos pos, BlockPos inventoryPos, Level level) {
        this.pos = pos;
        this.inventoryPos = inventoryPos;
        update(level);
    }

    public StoragePoint(CompoundTag tag) {
        pos = NbtUtils.readBlockPos(tag.getCompound("pos"));
        inventoryPos = NbtUtils.readBlockPos(tag.getCompound("inventorypos"));
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

    public void update(Level level) {
        stacks.clear();
        liquids.clear();
        if (level.getBlockEntity(inventoryPos) == null) return;
        if (level.getCapability(Capabilities.ItemHandler.BLOCK, inventoryPos, null) != null) {
            stacks = ItemStackHelper.combineItemsInStorage(level.getCapability(Capabilities.ItemHandler.BLOCK, inventoryPos, null), Blocks.AIR.asItem());
        }
        if (level.getCapability(Registration.DRIVE_HANDLER_BLOCK, inventoryPos, null) != null) {
            stacks = level.getCapability(Registration.DRIVE_HANDLER_BLOCK, inventoryPos, null).getStacks();
        }
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.put("pos", NbtUtils.writeBlockPos(pos));
        tag.put("inventorypos", NbtUtils.writeBlockPos(inventoryPos));
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

    public boolean equals(Object obj) {
        return obj instanceof StoragePoint point && inventoryPos.equals(point.inventoryPos) && pos.equals(point.pos);
    }

    public BlockPos getInventoryPos() {
        return inventoryPos;
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
