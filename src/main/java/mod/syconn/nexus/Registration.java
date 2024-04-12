package mod.syconn.nexus;

import mod.syconn.nexus.blockentities.ExternalStorageBE;
import mod.syconn.nexus.blockentities.InterfaceBE;
import mod.syconn.nexus.blockentities.ItemPipeBE;
import mod.syconn.nexus.blocks.*;
import mod.syconn.nexus.world.menu.InterfaceMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static mod.syconn.nexus.Nexus.MODID;

public class Registration {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Nexus.MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MODID);
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(BuiltInRegistries.MENU, MODID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredBlock<Block> NEXUS = BLOCKS.register("nexus", NexusBlock::new);
    public static final DeferredBlock<Block> ITEM_PIPE = BLOCKS.register("item_pipe", ItemPipe::new);
    public static final DeferredBlock<Block> INTERFACE = BLOCKS.register("interface", InterfaceBlock::new);
    public static final DeferredBlock<Block> EXTERNAL_STORAGE = BLOCKS.register("external_storage", ExternalStorage::new);
    public static final DeferredBlock<Block> EXTERNAL_STORAGE_DUMMY = BLOCKS.register("external_storage_dummy", () -> new DirectionalBlock(BlockBehaviour.Properties.of()));

    public static final DeferredItem<BlockItem> NEXUS_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("nexus", NEXUS);
    public static final DeferredItem<BlockItem> ITEM_PIPE_ITEM = ITEMS.registerSimpleBlockItem("item_pipe", ITEM_PIPE);
    public static final DeferredItem<BlockItem> INTERFACE_ITEM = ITEMS.registerSimpleBlockItem("interface", INTERFACE);
    public static final DeferredItem<BlockItem> EXTERNAL_STORAGE_ITEM = ITEMS.registerSimpleBlockItem("external_storage", EXTERNAL_STORAGE);

    public static final Supplier<BlockEntityType<ItemPipeBE>> ITEM_PIPE_BE = BLOCK_ENTITIES.register("item_pipe", () -> BlockEntityType.Builder.of(ItemPipeBE::new, ITEM_PIPE.get()).build(null));
    public static final Supplier<BlockEntityType<ExternalStorageBE>> EXTERNAL_STORAGE_BE = BLOCK_ENTITIES.register("external_storage", () -> BlockEntityType.Builder.of(ExternalStorageBE::new, EXTERNAL_STORAGE.get()).build(null));
    public static final Supplier<BlockEntityType<InterfaceBE>> INTERFACE_BE = BLOCK_ENTITIES.register("interface", () -> BlockEntityType.Builder.of(InterfaceBE::new, INTERFACE.get()).build(null));

    public static final TagKey<Block> PIPES = BlockTags.create(new ResourceLocation(MODID, "pipes"));
    public static final TagKey<Block> PIPE_CONNECTIVE = BlockTags.create(new ResourceLocation(MODID, "pipe_connective"));
    public static final TagKey<Block> DIRECTIONAL_PIPE_CONNECTIVE = BlockTags.create(new ResourceLocation(MODID, "directional_pipe_connective"));
    public static final TagKey<Block> OPPOSITE_DIRECTIONAL_PIPE_CONNECTIVE = BlockTags.create(new ResourceLocation(MODID, "opp_dir_con"));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> NEXUS_TAB = TABS.register("nexus", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + MODID)).icon(() -> Registration.NEXUS.get().asItem().getDefaultInstance()).build());

    public static final Supplier<MenuType<InterfaceMenu>> INTERFACE_MENU = MENUS.register("interface", () -> IMenuTypeExtension.create((windowId, inv, data) -> new InterfaceMenu(windowId, inv.player, data.readBlockPos())));

    public static void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == NEXUS_TAB.getKey())
        {
            ITEMS.getEntries().forEach(i -> event.accept(i.get()));
        }
    }
}
