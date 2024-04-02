package mod.syconn.nexus.datagen;

import mod.syconn.nexus.Registration;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

import java.util.Collections;
import java.util.List;

public class BlockLootTables extends BlockLootSubProvider {

    public BlockLootTables() {
        super(Collections.emptySet(), FeatureFlags.REGISTRY.allFlags());
    }

    protected void generate() {
        dropWhenSilkTouch(Registration.ITEM_PIPE.get());
        dropSelf(Registration.NEXUS.get());
        dropSelf(Registration.INTERFACE.get());
    }

    protected Iterable<Block> getKnownBlocks() {
        return List.of(Registration.NEXUS.get(), Registration.ITEM_PIPE.get(), Registration.INTERFACE.get());
    }
}
