package mod.syconn.nexus.client.screen;

import com.mojang.blaze3d.vertex.Tesselator;
import mod.syconn.nexus.Nexus;
import mod.syconn.nexus.world.menu.InterfaceMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.gui.widget.ScrollPanel;

public class InterfaceScreen extends AbstractContainerScreen<InterfaceMenu> {

    private static final ResourceLocation SCROLLER_SPRITE = new ResourceLocation("container/creative_inventory/scroller");
    private static final ResourceLocation BACKGROUND = new ResourceLocation(Nexus.MODID, "textures/gui/interface.png");

    public InterfaceScreen(InterfaceMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 194;
        imageHeight = 203;
    }

    protected void init() {
        super.init();
    }

    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.blit(BACKGROUND, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        pGuiGraphics.blitSprite(SCROLLER_SPRITE, leftPos + 175, topPos + 18, 12, 15);
    }

    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        pGuiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
    }
}