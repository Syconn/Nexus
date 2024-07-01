package mod.syconn.nexus.blockentities;

import mod.syconn.nexus.Registration;
import mod.syconn.nexus.blocks.InterfaceBlock;
import mod.syconn.nexus.network.Channel;
import mod.syconn.nexus.network.packets.AddStack;
import mod.syconn.nexus.util.ItemStackHelper;
import mod.syconn.nexus.util.NBTHelper;
import mod.syconn.nexus.util.data.PipeNetwork;
import mod.syconn.nexus.util.data.StoragePoint;
import mod.syconn.nexus.world.capabilities.IDriveHandler;
import mod.syconn.nexus.world.capabilities.UncappedItemHandler;
import mod.syconn.nexus.world.savedata.PipeNetworks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractInterfaceBE extends BasePipeBE {

    private static final String ITEMS_TAG = "Inventory";
    private final UncappedItemHandler items = createItemHandler();
    private final Lazy<IItemHandler> itemHandler = Lazy.of(() -> items);
    protected final Map<Integer, List<BlockPos>> registry = new HashMap<>();
    private int invSize;
    protected int line = 0;
    protected boolean updateScreen = false;
    private int tick = 0;

    public AbstractInterfaceBE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void tickServer() {
        if (updateScreen && !level.isClientSide()) {
            for (int i = 0; i < items.getSlots(); i++) items.setStackInSlot(i, ItemStack.EMPTY);
            PipeNetworks network = PipeNetworks.get((ServerLevel) level);
            Map<Item, Map<BlockPos, List<ItemStack>>> map = network.getItemsOnNetwork(level, getUUID(), false);
            invSize = map.entrySet().size();
            int startIndex = line * 9;
            int slotDelay = 0;
            int slot = 0;
            for (Map.Entry<Item, Map<BlockPos, List<ItemStack>>> m : map.entrySet()) {
                if (slot >= 45) break;
                else if (slotDelay >= startIndex) {
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
                slotDelay++;
            }
            updateScreen = false;
            markDirty();
        }

        if (!level.isClientSide()) {
            if (line > 0 && Math.ceil(invSize / 9.0) <= 5) {
                line = 0;
                updateScreen();
            }

            tick++;
            if (tick >= 100) {
                level.setBlock(worldPosition, getBlockState().setValue(InterfaceBlock.ACTIVE, !PipeNetworks.get((ServerLevel) level).getNexusBlocks(level, getUUID()).isEmpty()), 2);
                tick = 0;
            }
            markDirty();
        }
    }

    protected UncappedItemHandler createItemHandler() {
        return new UncappedItemHandler(46) {
            protected void onContentsChanged(int slot) {
                markDirty();
            }

            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                ItemStack stack = super.extractItem(slot, amount, simulate);
                if (registry.containsKey(slot)) {
                    amount = Math.min(amount, stack.getMaxStackSize());
                    ItemStack returnStack = stack.copyWithCount(amount);
                    for (int i = 0; i < registry.get(slot).size(); i++) {
                        if (level.getCapability(Capabilities.ItemHandler.BLOCK, registry.get(slot).get(i), null) != null) {
                            IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, registry.get(slot).get(i), null);
                            returnStack = ItemStackHelper.removeStack(handler, returnStack, simulate);
                        } else if (level.getCapability(Registration.DRIVE_HANDLER_BLOCK, registry.get(slot).get(i), null) != null) {
                            IDriveHandler handler = level.getCapability(Registration.DRIVE_HANDLER_BLOCK, registry.get(slot).get(i), null);
                            returnStack = handler.removeStack(returnStack, simulate);
                        }
                        onContentsChanged(slot);
                        if (returnStack.isEmpty()) return stack.copy();
                    }
                    return stack.copyWithCount(amount - returnStack.getCount());
                }
                return stack.copy();
            }

            public void setStackInSlot(int slot, @NotNull ItemStack stack) {
                ItemStack oldStack = getStackInSlot(slot);
                super.setStackInSlot(slot, stack);
                ItemStack addStack = stack.copyWithCount(stack.getCount() - oldStack.getCount());
                if (level != null && !level.isClientSide() && !updateScreen && !addStack.isEmpty()) {
                    PipeNetwork network = PipeNetworks.get((ServerLevel) level).getPipeNetwork(getUUID());
                    for (StoragePoint point : network.getStoragePoints()) {
                        if (level.getCapability(Capabilities.ItemHandler.BLOCK, point.getInventoryPos(), null) != null) {
                            IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, point.getInventoryPos(), null);
                            addStack = ItemHandlerHelper.insertItemStacked(handler, addStack, false);
                            onContentsChanged(slot);
                            if (addStack.isEmpty()) break;
                        } else if (level.getCapability(Registration.DRIVE_HANDLER_BLOCK, point.getInventoryPos(), null) != null) {
                            IDriveHandler handler = level.getCapability(Registration.DRIVE_HANDLER_BLOCK, point.getInventoryPos(), null);
                            addStack = handler.addStack(addStack);
                            onContentsChanged(slot);
                            if (addStack.isEmpty()) break;
                        }
                    }
                    Channel.sendToServer(new AddStack(addStack));
                }
            }

            public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                if (level == null || level.isClientSide() || updateScreen || stack.isEmpty()) return super.insertItem(slot, stack, simulate);
                return ItemStackHelper.canAddItemStack(stack, (ServerLevel) level, getUUID(), simulate);
            }

            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                if (level == null || level.isClientSide() || updateScreen || stack.isEmpty()) return super.isItemValid(slot, stack);
                return ItemStackHelper.canAddItemStack(stack, (ServerLevel) level, getUUID());
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

    public int getInvSize() {
        return invSize;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
        updateScreen();
        markDirty();
    }

    protected void saveClientData(CompoundTag tag) {
        super.saveClientData(tag);
        tag.put(ITEMS_TAG, items.serializeNBT());
        ListTag registryTag = new ListTag();
        registry.forEach(((integer, pos) -> {
            CompoundTag nbt = new CompoundTag();
            nbt.putInt("int", integer);
            nbt.put("positions", NBTHelper.savePositions(pos));
            registryTag.add(nbt);
        }));
        tag.put("registry", registryTag);
        tag.putBoolean("update", updateScreen);
        tag.putInt("line", line);
        tag.putInt("invSize", invSize);
    }

    protected void loadClientData(CompoundTag tag) {
        super.loadClientData(tag);
        if (tag.contains(ITEMS_TAG)) items.deserializeNBT(tag.getCompound(ITEMS_TAG));
        tag.getList("registry", Tag.TAG_COMPOUND).forEach(tag2 -> {
            CompoundTag nbt = (CompoundTag) tag2;
            registry.put(nbt.getInt("int"), NBTHelper.loadPositions(nbt.getCompound("positions")));
        });
        updateScreen = tag.getBoolean("update");
        line = tag.getInt("line");
        invSize = tag.getInt("invSize");
    }
}