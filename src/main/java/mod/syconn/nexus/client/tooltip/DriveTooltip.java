package mod.syconn.nexus.client.tooltip;

import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public class DriveTooltip implements TooltipComponent {

    private final NonNullList<ItemStack> items;

    public DriveTooltip(NonNullList<ItemStack> pItems) {
        this.items = pItems;
    }

    public NonNullList<ItemStack> getItems() {
        return this.items;
    }
}
