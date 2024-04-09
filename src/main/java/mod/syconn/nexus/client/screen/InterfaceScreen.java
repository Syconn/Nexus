package mod.syconn.nexus.client.screen;

import mod.syconn.nexus.Nexus;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class InterfaceScreen extends Screen {

    private static final ResourceLocation SCROLLER_SPRITE = new ResourceLocation("container/creative_inventory/scroller");
    private static final ResourceLocation SCROLLER_DISABLED_SPRITE = new ResourceLocation("container/creative_inventory/scroller_disabled");
    private static final ResourceLocation BACKGROUND = new ResourceLocation(Nexus.MODID, "texture/gui/interface.png");

    public InterfaceScreen(Component pTitle) {
        super(pTitle);
    }
}
