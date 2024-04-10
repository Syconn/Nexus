package mod.syconn.nexus.client.screen;

import mod.syconn.nexus.Nexus;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class InterfaceScreen extends AbstractContainerScreen<> {

    private static final ResourceLocation SCROLLER_SPRITE = new ResourceLocation("container/creative_inventory/scroller");
    private static final ResourceLocation SCROLLER_DISABLED_SPRITE = new ResourceLocation("container/creative_inventory/scroller_disabled");
    private static final ResourceLocation BACKGROUND = new ResourceLocation(Nexus.MODID, "texture/gui/interface.png");

    public InterfaceScreen(AbstractContainerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {

    }
}
