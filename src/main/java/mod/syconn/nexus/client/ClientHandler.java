package mod.syconn.nexus.client;

import mod.syconn.nexus.Nexus;
import mod.syconn.nexus.Registration;
import mod.syconn.nexus.client.loader.PipeModelLoader;
import mod.syconn.nexus.client.model.DriveModel;
import mod.syconn.nexus.client.renderer.DriveBER;
import mod.syconn.nexus.client.screen.CraftingInterfaceScreen;
import mod.syconn.nexus.client.screen.InterfaceScreen;
import mod.syconn.nexus.util.DriveHelper;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber(modid = Nexus.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientHandler {

    @SubscribeEvent
    public static void init(FMLClientSetupEvent event) {
        event.enqueueWork(() -> MenuScreens.register(Registration.INTERFACE_MENU.get(), InterfaceScreen::new));
        event.enqueueWork(() -> MenuScreens.register(Registration.CRAFTING_INTERFACE_MENU.get(), CraftingInterfaceScreen::new));

        ItemProperties.register(Registration.STORAGE_DRIVE.get(), new ResourceLocation(Nexus.MODID, "color"), new ItemPropertyFunction() {
            public float call(ItemStack stack, @Nullable ClientLevel p_174677_, @Nullable LivingEntity p_174678_, int p_174679_) { return DriveHelper.getDriveSlot(stack).getColor(); }
        });
    }

    @SubscribeEvent
    public static void modelInit(ModelEvent.RegisterGeometryLoaders event) {
        PipeModelLoader.register(event);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(Registration.DRIVE_BE.get(), DriveBER::new);
    }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(DriveModel.LAYER_LOCATION, DriveModel::createBodyLayer);
    }
}
