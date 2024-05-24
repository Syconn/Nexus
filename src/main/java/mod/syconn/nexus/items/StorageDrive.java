package mod.syconn.nexus.items;

import mod.syconn.nexus.util.DriveHelper;
import mod.syconn.nexus.util.data.DriveSlot;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StorageDrive extends Item {

    public StorageDrive(Properties properties) {
        super(properties);
    }

    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if (pIsAdvanced.isAdvanced()) {
            DriveSlot slot = DriveHelper.getDriveSlot(pStack);
            pTooltipComponents.add(Component.literal( slot.getQuantity() + "/" + slot.getMaxQuantity() + " blocks").withStyle(ChatFormatting.WHITE).withStyle(ChatFormatting.BOLD));
        }
    }
}
