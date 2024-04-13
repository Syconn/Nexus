package mod.syconn.nexus.blockentities;

import mod.syconn.nexus.Registration;
import mod.syconn.nexus.util.ItemStackHelper;
import mod.syconn.nexus.util.data.PipeNetwork;
import mod.syconn.nexus.util.data.StoragePoint;
import mod.syconn.nexus.world.capabilities.UncappedItemHandler;
import mod.syconn.nexus.world.savedata.PipeNetworks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InterfaceBE extends BasePipeBE {

    private static final String ITEMS_TAG = "Inventory";
    private final UncappedItemHandler items = createItemHandler();
    private final Lazy<IItemHandler> itemHandler = Lazy.of(() -> items);
    private final Map<Integer, BlockPos> registry = new HashMap<>();
    private boolean updateScreen = false;

    public InterfaceBE(BlockPos pos, BlockState state) {
        super(Registration.INTERFACE_BE.get(), pos, state);
    }

    public void tickServer() {
        if (updateScreen && !level.isClientSide()) {
            for (int i = 0; i < items.getSlots(); i++) items.setStackInSlot(i, ItemStack.EMPTY);
            PipeNetworks network = PipeNetworks.get((ServerLevel) level);
            Map<BlockPos, List<ItemStack>> map = network.getItemsOnNetwork(level, getUUID(), false);
            int slot = 0;
            for (Map.Entry<BlockPos, List<ItemStack>> m : map.entrySet()) {
                for (ItemStack stack : m.getValue()) {
                    items.setStackInSlot(slot, stack);
                    registry.put(slot, m.getKey());
                    slot++;
                }
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

            public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {

                System.out.println("extract");
                ItemStack stack = super.extractItem(slot, amount, simulate);
                if (registry.containsKey(slot)) {
                    IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, registry.get(slot), null);
                    ItemStackHelper.removeStack((IItemHandlerModifiable) handler, stack, simulate);
                    onContentsChanged(slot);
                }
                return stack;
            }

            public void setStackInSlot(int slot, @NotNull ItemStack stack) {
                super.setStackInSlot(slot, stack);
                if (level != null && !level.isClientSide() && !updateScreen && !stack.isEmpty()) {
                    PipeNetwork network = PipeNetworks.get((ServerLevel) level).getPipeNetwork(getUUID());
                    ItemStack result = stack.copy();
                    for (StoragePoint point : network.getStoragePoints()) {
                        IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, point.getInventoryPos(), null);
                        result = ItemHandlerHelper.insertItemStacked(handler, result, false);
                        onContentsChanged(slot);
                        updateScreen();
                    }
                }
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
            nbt.put("pos", NbtUtils.writeBlockPos(pos));
            registryTag.add(nbt);
        }));
        tag.put("registry", registryTag);
        tag.putBoolean("update", updateScreen);
    }

    protected void loadClientData(CompoundTag tag) {
        super.loadClientData(tag);
        if (tag.contains(ITEMS_TAG)) {
            items.deserializeNBT(tag.getCompound(ITEMS_TAG));
        }
        tag.getList("registry", Tag.TAG_COMPOUND).forEach(tag2 -> {
            CompoundTag nbt = (CompoundTag) tag2;
            registry.put(nbt.getInt("int"), NbtUtils.readBlockPos(nbt.getCompound("pos")));
        });
        updateScreen = tag.getBoolean("update");
    }
}
