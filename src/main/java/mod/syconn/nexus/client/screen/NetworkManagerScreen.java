package mod.syconn.nexus.client.screen;

import mod.syconn.nexus.Nexus;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NetworkManagerScreen extends Screen {

    private static final ResourceLocation SCROLLER_SPRITE = new ResourceLocation("container/stonecutter/scroller");
    private static final ResourceLocation BG_LOCATION = new ResourceLocation(Nexus.MODID, "textures/gui/network_manager.png");
    private final List<BlockType> types = new ArrayList<>();
    private int leftPos, topPos;

    public NetworkManagerScreen(Map<Block, Integer> map) {
        super(Component.literal("Network Manager"));

        for (Map.Entry<Block, Integer> entry : map.entrySet()) {
            types.add(new BlockType(entry.getKey(), entry.getValue()));
        }
    }

    protected void init() {
        this.leftPos = (this.width - 176) / 2;
        this.topPos = (this.height - 147) / 2;

        for (int i = 0; i < Math.min(5, types.size()); i++) {
            addRenderableWidget(new BlockTypeButton(leftPos + 9, topPos + 18 + i * 22, 141, 22, types.get(i), this::clicked));
        }
    }

    public void renderBackground(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderBackground(graphics, pMouseX, pMouseY, pPartialTick);
        graphics.blit(BG_LOCATION, leftPos, topPos, 0, 0, 176, 147);
    }

    public void render(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(graphics, pMouseX, pMouseY, pPartialTick);
//        pGuiGraphics.blitSprite(SCROLLER_SPRITE, leftPos + 175, topPos + 18 + (int) (95 * scrollOffs), 12, 15);
    }

    private void clicked(Button button) {}

    record BlockType(Block block, int amount) { }

    static class BlockTypeButton extends ExtendedButton {

        private final BlockType type;

        public BlockTypeButton(int xPos, int yPos, int width, int height, BlockType type, OnPress handler) {
            super(xPos, yPos, width, height, type.block.getName().append(Component.literal(" x" + type.amount)), handler);
            this.type = type;
        }

        public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
            guiGraphics.renderItem(new ItemStack(type.block), getX() + 3, getY() + 2);
        }
    }
}