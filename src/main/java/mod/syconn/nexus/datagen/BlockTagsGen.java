package mod.syconn.nexus.datagen;

import mod.syconn.nexus.Nexus;
import mod.syconn.nexus.Registration;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class BlockTagsGen extends BlockTagsProvider {

    public BlockTagsGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Nexus.MODID, existingFileHelper);
    }

    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(BlockTags.NEEDS_IRON_TOOL).add(Registration.INTERFACE.get()).add(Registration.NEXUS.get()).add(Registration.CRAFTING_INTERFACE.get()).add(Registration.DRIVE.get());
        this.tag(BlockTags.NEEDS_STONE_TOOL).add(Registration.ITEM_PIPE.get()).add(Registration.EXTERNAL_STORAGE.get());

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(Registration.CRAFTING_INTERFACE.get()).add(Registration.INTERFACE.get(), Registration.EXTERNAL_STORAGE.get(), Registration.NEXUS.get(), Registration.ITEM_PIPE.get());

        this.tag(Registration.PIPE_CONNECTIVE).add(Registration.NEXUS.get()).add(Registration.DRIVE.get());
        this.tag(Registration.DIRECTIONAL_PIPE_CONNECTIVE).add(Registration.CRAFTING_INTERFACE.get()).add(Registration.INTERFACE.get(), Registration.EXTERNAL_STORAGE.get());
        this.tag(Registration.OPPOSITE_DIRECTIONAL_PIPE_CONNECTIVE).add(Registration.EXTERNAL_STORAGE.get());
        this.tag(Registration.PIPES).add(Registration.ITEM_PIPE.get(), Registration.EXTERNAL_STORAGE.get());
    }
}
