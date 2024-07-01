package mod.syconn.nexus.client.loader;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import mod.syconn.nexus.Nexus;
import mod.syconn.nexus.client.model.PipeBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;

import java.util.function.Function;

public class PipeModelLoader implements IGeometryLoader<PipeModelLoader.CableModelGeometry> {

    public static final ResourceLocation GENERATOR_LOADER = new ResourceLocation(Nexus.MODID, "pipeloader");

    public static void register(ModelEvent.RegisterGeometryLoaders event) {
        event.register(new ResourceLocation(Nexus.MODID, "pipeloader"), new PipeModelLoader());
    }

    @Override
    public CableModelGeometry read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
        boolean facade = jsonObject.has("facade") && jsonObject.get("facade").getAsBoolean();
        double size = jsonObject.has("size") ? jsonObject.get("size").getAsDouble() : 0;
        String[] array = new String[8];
        if (jsonObject.has("locations")) for (int i = 0; i < 8; i++) array[i] = jsonObject.getAsJsonArray("locations").asList().get(i).getAsString();
        return new CableModelGeometry(facade, size, array);
    }

    public static class CableModelGeometry implements IUnbakedGeometry<CableModelGeometry> {
        private final boolean facade;
        private final double size;
        private final String[] textures;

        public CableModelGeometry(boolean facade, double size, String[] textures) {
            this.facade = facade;
            this.size = size;
            this.textures = textures;
        }

        @Override
        public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {
            return new PipeBakedModel(context, facade, size, textures);
        }
    }
}
