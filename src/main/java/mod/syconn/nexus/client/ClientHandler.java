package mod.syconn.nexus.client;

import mod.syconn.nexus.Nexus;
import mod.syconn.nexus.Registration;
import mod.syconn.nexus.client.loader.PipeModelLoader;
import mod.syconn.nexus.client.screen.CraftingInterfaceScreen;
import mod.syconn.nexus.client.screen.InterfaceScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;

@Mod.EventBusSubscriber(modid = Nexus.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientHandler {

    @SubscribeEvent
    public static void init(FMLClientSetupEvent event) {
        event.enqueueWork(() -> MenuScreens.register(Registration.INTERFACE_MENU.get(), InterfaceScreen::new));
        event.enqueueWork(() -> MenuScreens.register(Registration.CRAFTING_INTERFACE_MENU.get(), CraftingInterfaceScreen::new));
    }

    @SubscribeEvent
    public static void modelInit(ModelEvent.RegisterGeometryLoaders event) {
        PipeModelLoader.register(event);
    }
}
