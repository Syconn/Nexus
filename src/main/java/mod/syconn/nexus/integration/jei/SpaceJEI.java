package mod.syconn.nexus.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import mod.syconn.nexus.Nexus;
import mod.syconn.nexus.world.crafting.StorageDriveRecipe;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class SpaceJEI implements IModPlugin {

    public ResourceLocation getPluginUid()
    {
        return new ResourceLocation(Nexus.MODID, "crafting");
    }

    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        registration.getCraftingCategory().addExtension(StorageDriveRecipe.class, new DriveRecipeWrapper());
    }
}