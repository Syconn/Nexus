package mod.syconn.nexus.datagen;

import com.google.gson.JsonObject;
import mod.syconn.nexus.Nexus;
import mod.syconn.nexus.Registration;
import mod.syconn.nexus.client.model.PipeModelLoader;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.CustomLoaderBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class BlockStateGen extends BlockStateProvider {

    public BlockStateGen(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Nexus.MODID, exFileHelper);
    }

    protected void registerStatesAndModels() {
        simpleBlockWithItem(Registration.NEXUS.get(), cubeAll(Registration.NEXUS.get()));
        registerCables();
    }

    private void registerCables() {
        BlockModelBuilder model = models().getBuilder("pipe")
                .parent(models().getExistingFile(mcLoc("cube")))
                .customLoader((builder, helper) -> new PipeLoaderBuilder(PipeModelLoader.GENERATOR_LOADER, builder, helper, false))
                .end();
        simpleBlockWithItem(Registration.ITEM_PIPE.get(), model);
    }

    private void registerFacade() {
        BlockModelBuilder model = models().getBuilder("facade")
                .parent(models().getExistingFile(mcLoc("cube")))
                .customLoader((builder, helper) -> new PipeLoaderBuilder(PipeModelLoader.GENERATOR_LOADER, builder, helper, true))
                .end();
//        simpleBlock(Registration.FACADE_BLOCK.get(), model);
    }

    public static class PipeLoaderBuilder extends CustomLoaderBuilder<BlockModelBuilder> {

        private final boolean facade;

        public PipeLoaderBuilder(ResourceLocation loader, BlockModelBuilder parent, ExistingFileHelper existingFileHelper, boolean facade) {
            super(loader, parent, existingFileHelper, false);
            this.facade = facade;
        }

        @Override
        public JsonObject toJson(JsonObject json) {
            JsonObject obj = super.toJson(json);
            obj.addProperty("facade", facade);
            return obj;
        }
    }
}
