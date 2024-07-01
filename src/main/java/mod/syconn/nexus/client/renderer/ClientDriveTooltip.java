package mod.syconn.nexus.client.renderer;

import mod.syconn.nexus.client.tooltip.DriveTooltip;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.text.DecimalFormat;

@OnlyIn(Dist.CLIENT)
public class ClientDriveTooltip implements ClientTooltipComponent {

    private static final ResourceLocation BACKGROUND_SPRITE = new ResourceLocation("container/bundle/background");
    private final NonNullList<ItemStack> items;

    public ClientDriveTooltip(DriveTooltip pDriveTooltip) {
        this.items = pDriveTooltip.getItems();
    }

    public int getHeight() {
        return this.backgroundHeight() + 4;
    }

    public int getWidth(Font pFont) {
        return this.backgroundWidth();
    }

    private int backgroundWidth() {
        return this.gridSizeX() * 18 + 2;
    }

    private int backgroundHeight() {
        return this.gridSizeY() * 20 + 2;
    }

    public void renderImage(Font pFont, int pX, int pY, GuiGraphics pGuiGraphics) {
        int i = this.gridSizeX();
        int j = this.gridSizeY();
        pGuiGraphics.blitSprite(BACKGROUND_SPRITE, pX, pY, this.backgroundWidth(), this.backgroundHeight());
        int k = 0;

        for(int l = 0; l < j; ++l) {
            for(int i1 = 0; i1 < i; ++i1) {
                int j1 = pX + i1 * 18 + 1;
                int k1 = pY + l * 20 + 1;
                k++;
                if (this.items.size() > k) this.renderSlot(j1, k1, k, pGuiGraphics, pFont);
            }
        }
    }

    private void renderSlot(int pX, int pY, int pItemIndex, GuiGraphics pGuiGraphics, Font pFont) {
        ItemStack itemstack = this.items.get(pItemIndex);
        this.blit(pGuiGraphics, pX, pY);
        pGuiGraphics.renderItem(itemstack, pX + 1, pY + 1, pItemIndex);
        pGuiGraphics.renderItemDecorations(pFont, itemstack, pX + 1, pY + 1, getCount(itemstack.getCount()));
        if (pItemIndex == 0) {
            AbstractContainerScreen.renderSlotHighlight(pGuiGraphics, pX + 1, pY + 1, 0);
        }
    }

    private String getCount(int value) {
        if (value > 1) {
            String[] arr = {"", "k", "m", "b", "t"};
            int index = 0;
            while ((value / 1000) >= 1) {
                value = value / 1000;
                index++;
            }
            if (index > 4) index = 0;
            DecimalFormat decimalFormat = new DecimalFormat("#");
            return String.format("%s%s", decimalFormat.format(value), arr[index]);
        } else return null;
    }

    private void blit(GuiGraphics pGuiGraphics, int pX, int pY) {
        pGuiGraphics.blitSprite(new ResourceLocation("container/bundle/slot"), pX, pY, 0, 18, 20);
    }

    private int gridSizeX() {
        return (int)Math.ceil(((double)this.items.size()) / (double)this.gridSizeY());
    }

    private int gridSizeY() {
        return (int) Math.sqrt(this.items.size());
    }
}
