package mod.syconn.nexus.datagen;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import mod.syconn.nexus.Nexus;
import mod.syconn.nexus.Registration;
import mod.syconn.nexus.client.loader.PipeModelLoader;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.CustomLoaderBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.Arrays;

public class BlockStateGen extends BlockStateProvider {

    public BlockStateGen(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Nexus.MODID, exFileHelper);
    }

    protected void registerStatesAndModels() {
        simpleBlockWithItem(Registration.NEXUS.get(), cubeAll(Registration.NEXUS.get()));
        registerItemCables();
    }

//    private void registerEnergyCables() {
//        BlockModelBuilder model = models().getBuilder("pipe")
//                .parent(models().getExistingFile(mcLoc("cube")))
//                .customLoader((builder, helper) -> new PipeLoaderBuilder(PipeModelLoader.GENERATOR_LOADER, builder, helper, false, .4, new String[]{"block/pipe/connector",
//                        "block/pipe/normal", "block/pipe/none", "block/pipe/end", "block/pipe/corner", "block/pipe/three", "block/pipe/cross", "block/pipe/side"}))
//                .end();
//        simpleBlockWithItem(Registration.ITEM_PIPE.get(), model);
//    }

    private void registerItemCables() {
        BlockModelBuilder model = models().getBuilder("item_pipe")
                .parent(models().getExistingFile(mcLoc("cube")))
                .renderType("cutout")
                .customLoader((builder, helper) -> new PipeLoaderBuilder(PipeModelLoader.GENERATOR_LOADER, builder, helper, false, .3, new String[]{"block/itempipe/connector",
                        "block/itempipe/normal", "block/itempipe/none", "block/itempipe/end", "block/itempipe/corner", "block/itempipe/three", "block/itempipe/cross", "block/itempipe/side"}))
                .end();
        simpleBlockWithItem(Registration.ITEM_PIPE.get(), model);
    }

//    private void registerFacade() {
//        BlockModelBuilder model = models().getBuilder("facade")
//                .parent(models().getExistingFile(mcLoc("cube")))
//                .customLoader((builder, helper) -> new PipeLoaderBuilder(PipeModelLoader.GENERATOR_LOADER, builder, helper, true))
//                .end();
////        simpleBlock(Registration.FACADE_BLOCK.get(), model);
//    }

    public static class PipeLoaderBuilder extends CustomLoaderBuilder<BlockModelBuilder> {

        private final boolean facade;
        private final double size;
        private final String[] textures;

        public PipeLoaderBuilder(ResourceLocation loader, BlockModelBuilder parent, ExistingFileHelper existingFileHelper, boolean facade, double size, String[] textures) {
            super(loader, parent, existingFileHelper, false);
            this.facade = facade;
            this.size = size;
            this.textures = textures;
        }

        @Override
        public JsonObject toJson(JsonObject json) {
            JsonObject obj = super.toJson(json);
            obj.addProperty("facade", facade);
            obj.addProperty("size", size);
            obj.add("locations", new Gson().toJsonTree(textures));
            return obj;
        }
    }
}
