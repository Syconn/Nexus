package mod.syconn.nexus.client.screen;

import com.mojang.datafixers.util.Pair;
import mod.syconn.nexus.Nexus;
import mod.syconn.nexus.blockentities.InterfaceBE;
import mod.syconn.nexus.network.Channel;
import mod.syconn.nexus.network.packets.RefreshInterface;
import mod.syconn.nexus.network.packets.ScrollInterface;
import mod.syconn.nexus.world.menu.InterfaceMenu;
import mod.syconn.nexus.world.menu.slots.HiddenItemHandlerSlot;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.text.DecimalFormat;

public class InterfaceScreen extends AbstractContainerScreen<InterfaceMenu> {

    private static final ResourceLocation SCROLLER_SPRITE = new ResourceLocation("container/creative_inventory/scroller");
    private static final ResourceLocation SCROLLER_DISABLED_SPRITE = new ResourceLocation("container/stonecutter/scroller_disabled");
    private static final ResourceLocation BACKGROUND = new ResourceLocation(Nexus.MODID, "textures/gui/interface.png");
    private float scrollOffs;
    private boolean scrolling;

    public InterfaceScreen(InterfaceMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 195;
        imageHeight = 204;
    }

    protected void init() {
        super.init();
    }

    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.blit(BACKGROUND, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
        if (Minecraft.getInstance().level.getBlockEntity(menu.getPos()) instanceof InterfaceBE be && !scrolling) {
            scrollOffs = (float) (be.getLine() / (Math.ceil(be.getInvSize() / 9.0f) - 5));
            if (Math.ceil(be.getInvSize() / 9.0f) > 5) pGuiGraphics.blitSprite(SCROLLER_SPRITE, leftPos + 175, topPos + 18 + (int) (95 * scrollOffs), 12, 15);
            else pGuiGraphics.blitSprite(SCROLLER_DISABLED_SPRITE, leftPos + 175, topPos + 18, 12, 15);
        }
    }

    protected void renderTooltip(GuiGraphics pGuiGraphics, int pX, int pY) {
        if (this.menu.getCarried().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
            ItemStack itemstack = this.hoveredSlot.getItem().copy();
            itemstack.setHoverName(itemstack.getHoverName().copy().append(" x" + itemstack.getCount()).withStyle(itemstack.getHoverName().getStyle()));
            pGuiGraphics.renderTooltip(this.font, this.getTooltipFromContainerItem(itemstack), itemstack.getTooltipImage(), itemstack, pX, pY);
        }
    }

    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        pGuiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
    }

    protected void renderSlot(GuiGraphics pGuiGraphics, Slot pSlot) {
        if (!(pSlot instanceof HiddenItemHandlerSlot)) {
            int i = pSlot.x;
            int j = pSlot.y;
            ItemStack itemstack = pSlot.getItem();
            String amount = "";
            double value = itemstack.getCount();
            boolean flag = false;
            boolean flag1 = pSlot == this.clickedSlot && !this.draggingItem.isEmpty() && !this.isSplittingStack;
            ItemStack itemstack1 = this.menu.getCarried();
            if (pSlot == this.clickedSlot && !this.draggingItem.isEmpty() && this.isSplittingStack && !itemstack.isEmpty()) itemstack = itemstack.copyWithCount(itemstack.getCount() / 2);
            else if (this.isQuickCrafting && this.quickCraftSlots.contains(pSlot) && !itemstack1.isEmpty()) {
                if (this.quickCraftSlots.size() == 1) return;
                if (AbstractContainerMenu.canItemQuickReplace(pSlot, itemstack1, true) && this.menu.canDragTo(pSlot)) {
                    flag = true;
                    int k = Math.min(itemstack1.getMaxStackSize(), pSlot.getMaxStackSize(itemstack1));
                    int l = pSlot.getItem().isEmpty() ? 0 : pSlot.getItem().getCount();
                    int i1 = AbstractContainerMenu.getQuickCraftPlaceCount(this.quickCraftSlots, this.quickCraftingType, itemstack1) + l;
                    if (i1 > k) {
                        i1 = k;
                        amount = ChatFormatting.YELLOW.toString() + k;
                    }
                    itemstack = itemstack1.copyWithCount(i1);
                } else {
                    this.quickCraftSlots.remove(pSlot);
                    this.recalculateQuickCraftRemaining();
                }
            }
            if (amount.isEmpty()) {
                if (value > 1) {
                    String[] arr = {"", "k", "m", "b", "t"};
                    int index = 0;
                    while ((value / 1000) >= 1) {
                        value = value / 1000;
                        index++;
                    }
                    if (index > 4) index = 0;
                    DecimalFormat decimalFormat = new DecimalFormat("#");
                    amount = String.format("%s%s", decimalFormat.format(value), arr[index]);
                } else amount = null;
            }
            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().translate(0.0F, 0.0F, 100.0F);
            if (itemstack.isEmpty() && pSlot.isActive()) {
                Pair<ResourceLocation, ResourceLocation> pair = pSlot.getNoItemIcon();
                if (pair != null) {
                    TextureAtlasSprite textureatlassprite = this.minecraft.getTextureAtlas(pair.getFirst()).apply(pair.getSecond());
                    pGuiGraphics.blit(i, j, 0, 16, 16, textureatlassprite);
                    flag1 = true;
                }
            }
            if (!flag1) {
                if (flag) pGuiGraphics.fill(i, j, i + 16, j + 16, -2130706433);
                int j1 = pSlot.x + pSlot.y * this.imageWidth;
                if (pSlot.isFake()) pGuiGraphics.renderFakeItem(itemstack, i, j, j1);
                else pGuiGraphics.renderItem(itemstack, i, j, j1);
                pGuiGraphics.renderItemDecorations(this.font, itemstack, i, j, amount);
            }
            pGuiGraphics.pose().popPose();
        }
    }

    protected boolean insideScrollbar(double pMouseX, double pMouseY) {
        int i = this.leftPos;
        int j = this.topPos;
        int k = i + 175;
        int l = j + 18;
        int i1 = k + 12;
        int j1 = l + 110;
        return pMouseX >= (double)k && pMouseY >= (double)l && pMouseX < (double)i1 && pMouseY < (double)j1;
    }

    public boolean canScroll() {
        return Minecraft.getInstance().level.getBlockEntity(menu.getPos()) instanceof InterfaceBE be && be.getInvSize() > 45;
    }

    public boolean mouseScrolled(double pMouseX, double pMouseY, double pScrollX, double pScrollY) {
        if (!this.canScroll()) {
            return false;
        } else {
            this.scrollOffs = subtractInputFromScroll(this.scrollOffs, pScrollY);
            this.scrollTo(this.scrollOffs);
            return true;
        }
    }

    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (pButton == 0 && this.insideScrollbar(pMouseX, pMouseY)) {
            this.scrolling = this.canScroll();
            return true;
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (this.scrolling) {
            int i = this.topPos + 18;
            int j = i + 112;
            this.scrollOffs = ((float)pMouseY - (float)i - 7.5F) / ((float)(j - i) - 15.0F);
            this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0F, 1.0F);
            this.scrollTo(this.scrollOffs);
            return true;
        } else return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        if (pButton == 0) this.scrolling = false;
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    private void scrollTo(float scrollOffs) {
        Channel.sendToServer(new ScrollInterface(menu.getPos(), scrollOffs));
    }

    private int calculateRowCount() {
        if (Minecraft.getInstance().level.getBlockEntity(menu.getPos()) instanceof InterfaceBE be) return (int) Math.ceil(be.getInvSize() / 9.0) - 5;
        return 0;
    }

    private float subtractInputFromScroll(float pScrollOffs, double pInput) {
        return Mth.clamp(pScrollOffs - (float)(pInput / (double)this.calculateRowCount()), 0.0F, 1.0F);
    }

    public void onClose() {
        super.onClose();
        Channel.sendToServer(new RefreshInterface(menu.getPos()));
    }
}