package mod.syconn.nexus.blockentities;

import mod.syconn.nexus.Registration;
import mod.syconn.nexus.util.ItemStackHelper;
import mod.syconn.nexus.util.NBTHelper;
import mod.syconn.nexus.util.data.PipeNetwork;
import mod.syconn.nexus.util.data.StoragePoint;
import mod.syconn.nexus.world.capabilities.UncappedItemHandler;
import mod.syconn.nexus.world.savedata.PipeNetworks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InterfaceBE extends BasePipeBE {

    private static final String ITEMS_TAG = "Inventory";
    private final UncappedItemHandler items = createItemHandler();
    private final Lazy<IItemHandler> itemHandler = Lazy.of(() -> items);
    private final Map<Integer, List<BlockPos>> registry = new HashMap<>();
    private int line = 0;
    private boolean updateScreen = false;

    public InterfaceBE(BlockPos pos, BlockState state) {
        super(Registration.INTERFACE_BE.get(), pos, state);
    }

    public void tickServer() { // TODO EASY WAY MAYBE IS SET CLIENT SIDE VIEW OF ITEMS TO SIZE
        if (updateScreen && !level.isClientSide()) { // TODO TEST 2 Connections to one storage block
            for (int i = 0; i < items.getSlots(); i++) items.setStackInSlot(i, ItemStack.EMPTY);
            PipeNetworks network = PipeNetworks.get((ServerLevel) level);
            Map<Item, Map<BlockPos, List<ItemStack>>> map = network.getItemsOnNetwork(level, getUUID(), false);
            int slot = line * 9;
            for (Map.Entry<Item, Map<BlockPos, List<ItemStack>>> m : map.entrySet()) {
                if (slot > items.getSlots()) break;
                int stackSize = 0;
                List<BlockPos> locations = new ArrayList<>();
                for (Map.Entry<BlockPos, List<ItemStack>> m2 : m.getValue().entrySet()) {
                    locations.add(m2.getKey());
                    for (ItemStack stack : m2.getValue()) stackSize += stack.getCount();
                }
                items.setStackInSlot(slot, m.getValue().get(locations.get(0)).get(0).copyWithCount(stackSize));
                registry.put(slot, locations);
                slot++;
            }
            updateScreen = false;
            markDirty();
        }
    }

    private UncappedItemHandler createItemHandler() {
        return new UncappedItemHandler(45) {
            protected void onContentsChanged(int slot) {
                markDirty();
            }

            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                ItemStack stack = super.extractItem(slot, amount, simulate);
                if (registry.containsKey(slot)) {
                    IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, registry.get(slot).get(0), null);
                    ItemStackHelper.removeStack((IItemHandlerModifiable) handler, stack, simulate);
                    onContentsChanged(slot);
                }
                return stack;
            }

            public void setStackInSlot(int slot, @NotNull ItemStack stack) {
                ItemStack oldStack = getStackInSlot(slot);
                super.setStackInSlot(slot, stack);
                ItemStack addStack = stack.copyWithCount(stack.getCount() - oldStack.getCount());
                if (level != null && !level.isClientSide() && !updateScreen && !addStack.isEmpty()) {
                    PipeNetwork network = PipeNetworks.get((ServerLevel) level).getPipeNetwork(getUUID());
                    for (StoragePoint point : network.getStoragePoints()) {
                        IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, point.getInventoryPos(), null);
                        ItemStack stack2 = ItemHandlerHelper.insertItemStacked(handler, addStack, false);
                        onContentsChanged(slot);
                        if (stack2.isEmpty()) break;
                    }
                }
            }

            public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                if (level == null || level.isClientSide() || updateScreen || stack.isEmpty()) return super.insertItem(slot, stack, simulate);
                return ItemStackHelper.canAddItemStack(stack, (ServerLevel) level, getUUID());
            }

            public boolean isItemValid(int slot, @NotNull ItemStack stack) { // TODO OVERFLOW STILL BROKE
                if (level == null || level.isClientSide() || updateScreen || stack.isEmpty()) return super.isItemValid(slot, stack);
                return !ItemStackHelper.canAddItemStack(stack, (ServerLevel) level, getUUID()).equals(stack);
            }
        };
    }

    public void updateScreen() {
        this.updateScreen = true;
    }

    public ItemStackHandler getItems() {
        return items;
    }

    public IItemHandler getItemHandler() {
        return itemHandler.get();
    }

    protected void saveClientData(CompoundTag tag) {
        super.saveClientData(tag);
        tag.put(ITEMS_TAG, items.serializeNBT());
        ListTag registryTag = new ListTag();
        registry.forEach(((integer, pos) -> {
            CompoundTag nbt = new CompoundTag();
            nbt.putInt("int", integer);
            nbt.put("positions", NBTHelper.writePosses(pos));
            registryTag.add(nbt);
        }));
        tag.put("registry", registryTag);
        tag.putBoolean("update", updateScreen);
        tag.putInt("line", line);
    }

    protected void loadClientData(CompoundTag tag) {
        super.loadClientData(tag);
        if (tag.contains(ITEMS_TAG)) {
            items.deserializeNBT(tag.getCompound(ITEMS_TAG));
        }
        tag.getList("registry", Tag.TAG_COMPOUND).forEach(tag2 -> {
            CompoundTag nbt = (CompoundTag) tag2;
            registry.put(nbt.getInt("int"), NBTHelper.readPosses(nbt.getCompound("positions")));
        });
        updateScreen = tag.getBoolean("update");
        line = tag.getInt("line");
    }
}
