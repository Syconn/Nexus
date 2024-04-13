package mod.syconn.nexus.blockentities;

import mod.syconn.nexus.Registration;
import mod.syconn.nexus.world.savedata.PipeNetworks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class InterfaceBE extends BasePipeBE {

    private static final String ITEMS_TAG = "Inventory";

    private final ItemStackHandler items = createItemHandler();
    private final Lazy<IItemHandler> itemHandler = Lazy.of(() -> items);
    private boolean updateScreen = true;

    public InterfaceBE(BlockPos pos, BlockState state) {
        super(Registration.INTERFACE_BE.get(), pos, state);
    }

    public void tickServer() {
        if (updateScreen) { // TODO HANDLE VIEWS HERE
            updateScreen = false;
            PipeNetworks network = PipeNetworks.get((ServerLevel) level);
            Map<BlockPos, List<ItemStack>> map = network.getItemsOnNetwork(level, getUUID());
            int slot = 0;
            for (Map.Entry<BlockPos, List<ItemStack>> m : map.entrySet()) { for (ItemStack stack : m.getValue()) {
                    items.setStackInSlot(slot, stack);
                    slot++;
                }
            }
            setChanged();
        }
    }

    private ItemStackHandler createItemHandler() {
        return new ItemStackHandler(45) {
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
                System.out.println(level != null);
                return super.extractItem(slot, amount, simulate);
            }

            public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                return super.insertItem(slot, stack, simulate);
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
    }

    protected void loadClientData(CompoundTag tag) {
        super.loadClientData(tag);
        if (tag.contains(ITEMS_TAG)) {
            items.deserializeNBT(tag.getCompound(ITEMS_TAG));
        }
    }
}
