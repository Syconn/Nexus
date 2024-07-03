package mod.syconn.nexus.datagen;

import mod.syconn.nexus.Nexus;
import mod.syconn.nexus.Registration;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ItemModelGen extends ItemModelProvider {

    public ItemModelGen(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Nexus.MODID, existingFileHelper);
    }

    protected void registerModels() {
        singleTexture(Registration.DIAMOND_UPGRADE.get());
        singleTexture(Registration.GOLD_UPGRADE.get());
        singleTexture(Registration.IRON_UPGRADE.get());
        singleTexture(Registration.EMERALD_UPGRADE.get());
        singleTexture(Registration.NETHERITE_UPGRADE.get());
        getBuilder(Registration.STORAGE_DRIVE.get().toString()).parent(generate()).texture("layer0", modLoc("item/drive_item_green"))
                .override().predicate(modLoc("color"), 1).model(generated("item/drive_item_yellow")).end()
                .override().predicate(modLoc("color"), 2).model(generated("item/drive_item_red")).end();
        getBuilder("drive_item_yellow").parent(generate()).texture("layer0", modLoc("item/drive_item_yellow"));
        getBuilder("drive_item_red").parent(generate()).texture("layer0", modLoc("item/drive_item_red"));
    }

    private ResourceLocation generated(){
        return new ResourceLocation("item/generated");
    }

    private ModelFile generated(String loc) {
        return new ModelFile.UncheckedModelFile(modLoc(loc));
    }

    private ModelFile.UncheckedModelFile generate(){
        return new ModelFile.UncheckedModelFile("item/generated");
    }

    private ItemModelBuilder singleTexture(Item item) {
        return super.singleTexture(item.toString(), generated(), "layer0", modLoc("item/" + BuiltInRegistries.ITEM.getKey(item).getPath()));
    }
}
