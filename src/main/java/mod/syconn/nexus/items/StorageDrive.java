package mod.syconn.nexus.items;

import mod.syconn.nexus.util.DriveHelper;
import mod.syconn.nexus.util.data.DriveSlot;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StorageDrive extends Item { // TODO SHOW ITEMS IN SHIFT MENU LIKE BUNDLE ITEM

    public StorageDrive(Properties properties) {
        super(properties);
    }

    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if (pIsAdvanced.isAdvanced()) {
            DriveSlot slot = DriveHelper.getDriveSlot(pStack);
            pTooltipComponents.add(Component.literal( slot.getQuantity() + "/" + slot.getMaxQuantity() + " blocks").withStyle(ChatFormatting.WHITE).withStyle(ChatFormatting.BOLD));
            if (Screen.hasShiftDown()) {
                if (!slot.getStacks().isEmpty()) pTooltipComponents.add(Component.empty());
                for (int i = 0; i < Math.min(slot.getStacks().size(), 5); i++) {
                    pTooltipComponents.add(Component.literal(slot.getStacks().get(i).getStack().toString()).withStyle(ChatFormatting.ITALIC));
                }
            } else if (slot.getQuantity() > 0) pTooltipComponents.add(Component.literal("Press SHIFT to view items").withStyle(ChatFormatting.YELLOW));
        }
    }
}
