package mod.syconn.nexus.client.screen;

import mod.syconn.nexus.Nexus;
import mod.syconn.nexus.blockentities.InterfaceBE;
import mod.syconn.nexus.blocks.ExternalStorage;
import mod.syconn.nexus.network.Channel;
import mod.syconn.nexus.network.packets.ScrollInterface;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NetworkManagerScreen extends Screen {

    private static final ResourceLocation SCROLLER_SPRITE = new ResourceLocation("container/stonecutter/scroller");
    private static final ResourceLocation SCROLLER_DISABLED_SPRITE = new ResourceLocation("container/stonecutter/scroller_disabled");
    private static final ResourceLocation BG_LOCATION = new ResourceLocation(Nexus.MODID, "textures/gui/network_manager.png");
    private final List<BlockType> types = new ArrayList<>();
    private final BlockTypeButton[] buttons = new BlockTypeButton[5];
    private int leftPos, topPos;
    private float scrollOffs;
    private boolean scrolling;
    private int row;

    public NetworkManagerScreen(Map<Block, Integer> map) {
        super(Component.literal("Network Manager"));
        for (Map.Entry<Block, Integer> entry : map.entrySet()) {
            types.add(new BlockType(entry.getKey(), entry.getValue()));
        }
    }

    protected void init() {
        this.leftPos = (this.width - 176) / 2;
        this.topPos = (this.height - 147) / 2;

        for (int i = row; i < Math.min(5 + row, types.size() + row); i++) {
            addRenderableWidget(buttons[i - row] = new BlockTypeButton(leftPos + 9, topPos + 18 + i * 22, 141, 22, types.get(i), this::clicked));
        }
    }

    public void renderBackground(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderBackground(graphics, pMouseX, pMouseY, pPartialTick);
        graphics.blit(BG_LOCATION, leftPos, topPos, 0, 0, 176, 147);
    }

    public void render(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(graphics, pMouseX, pMouseY, pPartialTick);
        if (canScroll()) graphics.blitSprite(SCROLLER_SPRITE, leftPos + 156, topPos + 18 + (int) (95 * scrollOffs), 12, 15);
        else graphics.blitSprite(SCROLLER_DISABLED_SPRITE, leftPos + 156, topPos + 18, 12, 15);
    }

    protected boolean insideScrollbar(double pMouseX, double pMouseY) {
        int i = this.leftPos;
        int j = this.topPos;
        int k = i + 156;
        int l = j + 18;
        int i1 = k + 12;
        int j1 = l + 110;
        return pMouseX >= (double)k && pMouseY >= (double)l && pMouseX < (double)i1 && pMouseY < (double)j1;
    }

    public boolean canScroll() {
        return types.size() > 5;
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
            int j = i + 156;
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
        row = (int) ((types.size() - 5) * scrollOffs);
        for (int i = row; i < Math.min(5 + row, types.size() + row); i++) {
            buttons[i - row].setType(types.get(i));
        }
    }

    private int calculateRowCount() {
        return types.size() - 5;
    }

    private float subtractInputFromScroll(float pScrollOffs, double pInput) {
        return Mth.clamp(pScrollOffs - (float)(pInput / (double)this.calculateRowCount()), 0.0F, 1.0F);
    }

    private void clicked(Button button) {}

    record BlockType(Block block, int amount) { }

    static class BlockTypeButton extends ExtendedButton {

        private BlockType type;

        public BlockTypeButton(int xPos, int yPos, int width, int height, BlockType type, OnPress handler) {
            super(xPos, yPos, width, height, type.block.getName().append(Component.literal(" x" + type.amount)), handler);
            this.type = type;
        }

        public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            Minecraft mc = Minecraft.getInstance();
            guiGraphics.blitSprite(SPRITES.get(this.active, this.isHoveredOrFocused()), this.getX(), this.getY(), this.getWidth(), this.getHeight());
            final FormattedText buttonText = mc.font.ellipsize(this.getMessage(), this.width - 6);
            guiGraphics.drawCenteredString(mc.font, Language.getInstance().getVisualOrder(buttonText), this.getX() + this.width / 2 + 4, this.getY() + (this.height - 8) / 2, getFGColor());
            guiGraphics.renderItem(new ItemStack(type.block), getX() + 3, getY() + 3);
        }

        public void setType(BlockType type) {
            this.type = type;
            setMessage(type.block.getName().append(Component.literal(" x" + type.amount)));
        }
    }
}