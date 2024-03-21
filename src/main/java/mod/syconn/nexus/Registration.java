package mod.syconn.nexus;

import mod.syconn.nexus.blockentities.ItemPipeBE;
import mod.syconn.nexus.blocks.ItemPipe;
import mod.syconn.nexus.blocks.NexusBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static mod.syconn.nexus.Nexus.MODID;

public class Registration {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Nexus.MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MODID);

    public static final DeferredBlock<Block> NEXUS = BLOCKS.register("nexus", NexusBlock::new);
    public static final DeferredItem<BlockItem> NEXUS_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("nexus", NEXUS);
    public static final DeferredBlock<Block> ITEM_PIPE = BLOCKS.register("item_pipe", ItemPipe::new);
    public static final DeferredItem<BlockItem> ITEM_PIPE_ITEM = ITEMS.registerSimpleBlockItem("item_pipe", ITEM_PIPE);
    public static final Supplier<BlockEntityType<ItemPipeBE>> ITEM_PIPE_BE = BLOCK_ENTITIES.register("item_pipe", () -> BlockEntityType.Builder.of(ItemPipeBE::new, ITEM_PIPE.get()).build(null));

    public static void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == Nexus.NEXUS_TAB.getKey())
        {
            ITEMS.getEntries().forEach(i -> event.accept(i.get()));
        }
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {

    }
}
