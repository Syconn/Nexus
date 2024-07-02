package mod.syconn.nexus.items;

import mod.syconn.nexus.client.tooltip.DriveTooltip;
import mod.syconn.nexus.util.DriveHelper;
import mod.syconn.nexus.util.data.DriveSlot;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class StorageDrive extends Item {

    public StorageDrive(Properties properties) {
        super(properties);
    }

    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if (pIsAdvanced.isAdvanced()) {
            DriveSlot slot = DriveHelper.getDriveSlot(pStack);
            pTooltipComponents.add(Component.literal(slot.getQuantity() + "/" + slot.getMaxQuantity() + " blocks").withStyle(ChatFormatting.WHITE).withStyle(ChatFormatting.BOLD));
        }
    }

    public Optional<TooltipComponent> getTooltipImage(ItemStack pStack) {
        NonNullList<ItemStack> nonnulllist = NonNullList.create();
        DriveSlot drive = DriveHelper.getDriveSlot(pStack);
        drive.getStacks().forEach(type -> nonnulllist.add(type.getStack()));
        return Optional.of(new DriveTooltip(nonnulllist));
    }
}
