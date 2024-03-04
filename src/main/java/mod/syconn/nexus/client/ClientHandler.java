package mod.syconn.nexus.client;

import mod.syconn.nexus.Nexus;
import mod.syconn.nexus.client.model.PipeModelLoader;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;

@Mod.EventBusSubscriber(modid = Nexus.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientHandler {

    @SubscribeEvent
    public static void init(FMLClientSetupEvent event) {

    }

    @SubscribeEvent
    public static void modelInit(ModelEvent.RegisterGeometryLoaders event) {
        PipeModelLoader.register(event);
    }
}
