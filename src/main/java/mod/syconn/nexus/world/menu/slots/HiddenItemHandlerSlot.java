package mod.syconn.nexus.world.menu.slots;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class HiddenItemHandlerSlot extends SlotItemHandler {

    public HiddenItemHandlerSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    public boolean mayPickup(Player playerIn) {
        return false;
    }
}
