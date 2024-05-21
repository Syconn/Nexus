package mod.syconn.nexus.datagen;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import mod.syconn.nexus.Nexus;
import mod.syconn.nexus.Registration;
import mod.syconn.nexus.blocks.InterfaceBlock;
import mod.syconn.nexus.blocks.PipeAttachmentBlock;
import mod.syconn.nexus.client.loader.PipeModelLoader;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class BlockStateGen extends BlockStateProvider {

    public BlockStateGen(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Nexus.MODID, exFileHelper);
    }

    protected void registerStatesAndModels() {
        simpleBlockWithItem(Registration.NEXUS.get(), cubeAll(Registration.NEXUS.get()));
        registerItemCables();
        registerCableExtensions();
        registerDynamicStates();
    }

    private void registerDynamicStates() {
        simpleBlockItem(Registration.INTERFACE.get(), generated(Registration.INTERFACE.get()));
        simpleBlockItem(Registration.DRIVE.get(), generated(Registration.DRIVE.get()));
        simpleBlockItem(Registration.CRAFTING_INTERFACE.get(), generated(Registration.CRAFTING_INTERFACE.get()));
        getVariantBuilder(Registration.INTERFACE.get()).forAllStatesExcept(state -> {
            Direction direction = state.getValue(InterfaceBlock.FACING);
            boolean active = state.getValue(InterfaceBlock.ACTIVE);
            return ConfiguredModel.builder().modelFile(generated(Registration.INTERFACE.get(), !active ? "_off" : ""))
                    .rotationY(direction.getAxis().isVertical() ? 0 : (int) direction.toYRot())
                    .rotationX(direction == Direction.DOWN ? 270 : direction == Direction.UP ? 90 : 0).build();
        }, PipeAttachmentBlock.DOWN, PipeAttachmentBlock.EAST, PipeAttachmentBlock.WEST, PipeAttachmentBlock.NORTH, PipeAttachmentBlock.UP, PipeAttachmentBlock.SOUTH);
        getVariantBuilder(Registration.CRAFTING_INTERFACE.get()).forAllStatesExcept(state -> {
            Direction direction = state.getValue(InterfaceBlock.FACING);
            boolean active = state.getValue(InterfaceBlock.ACTIVE);
            return ConfiguredModel.builder().modelFile(generated(Registration.CRAFTING_INTERFACE.get(), !active ? "_off" : ""))
                    .rotationY(direction.getAxis().isVertical() ? 0 : (int) direction.toYRot())
                    .rotationX(direction == Direction.DOWN ? 270 : direction == Direction.UP ? 90 : 0).build();
        }, PipeAttachmentBlock.DOWN, PipeAttachmentBlock.EAST, PipeAttachmentBlock.WEST, PipeAttachmentBlock.NORTH, PipeAttachmentBlock.UP, PipeAttachmentBlock.SOUTH);
        getVariantBuilder(Registration.EXTERNAL_STORAGE_DUMMY.get()).forAllStates(state -> {
            Direction direction = state.getValue(InterfaceBlock.FACING);
            return ConfiguredModel.builder().modelFile(generated(Registration.EXTERNAL_STORAGE_DUMMY.get()))
                    .rotationY(direction.getAxis().isVertical() ? 0 : (int) direction.toYRot())
                    .rotationX(direction == Direction.DOWN ? 270 : direction == Direction.UP ? 90 : 0).build();
        });
        getVariantBuilder(Registration.DRIVE.get()).forAllStatesExcept(state -> {
            Direction direction = state.getValue(InterfaceBlock.FACING);
            return ConfiguredModel.builder().modelFile(generated(Registration.DRIVE.get()))
                    .rotationY(direction.getAxis().isVertical() ? 0 : (int) direction.toYRot()).build();
        }, PipeAttachmentBlock.DOWN, PipeAttachmentBlock.EAST, PipeAttachmentBlock.WEST, PipeAttachmentBlock.NORTH, PipeAttachmentBlock.UP, PipeAttachmentBlock.SOUTH);
    }

    private void registerItemCables() {
        BlockModelBuilder model = models().getBuilder("item_pipe")
                .parent(models().getExistingFile(mcLoc("cube")))
                .renderType("cutout")
                .customLoader((builder, helper) -> new PipeLoaderBuilder(PipeModelLoader.GENERATOR_LOADER, builder, helper, false, .3, new String[]{"block/itempipe/connector",
                        "block/itempipe/normal", "block/itempipe/none", "block/itempipe/end", "block/itempipe/corner", "block/itempipe/three", "block/itempipe/cross", "block/itempipe/side"}))
                .end();
        simpleBlockWithItem(Registration.ITEM_PIPE.get(), model);
    }

    private void registerCableExtensions() {
        BlockModelBuilder model = models().getBuilder("external_storage")
                .parent(models().getExistingFile(mcLoc("cube")))
                .renderType("cutout")
                .customLoader((builder, helper) -> new PipeLoaderBuilder(PipeModelLoader.GENERATOR_LOADER, builder, helper, false, .3, new String[]{"block/itempipe/connector",
                        "block/itempipe/normal", "block/itempipe/none", "block/itempipe/end", "block/itempipe/corner", "block/itempipe/three", "block/itempipe/cross", "block/itempipe/side"}))
                .end();
        simpleBlockWithItem(Registration.EXTERNAL_STORAGE.get(), model);
    }

    private ModelFile generated(Block block) {
        return new ModelFile.UncheckedModelFile(modLoc("block/" + BuiltInRegistries.BLOCK.getKey(block).getPath()));
    }

    private ModelFile generated(Block block, String s) {
        return new ModelFile.UncheckedModelFile(modLoc("block/" + BuiltInRegistries.BLOCK.getKey(block).getPath() + s));
    }

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
