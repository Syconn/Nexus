package mod.syconn.nexus;

import mod.syconn.nexus.datagen.BlockStateGen;
import mod.syconn.nexus.datagen.ItemModelGen;
import mod.syconn.nexus.datagen.LangGen;
import mod.syconn.nexus.network.Channel;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(Nexus.MODID)
public class Nexus
{
    public static final String MODID = "nexus";

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> NEXUS_TAB = CREATIVE_MODE_TABS.register("nexus", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + MODID)).icon(() -> Registration.NEXUS.get().asItem().getDefaultInstance()).build());

    public Nexus(IEventBus modEventBus)
    {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::gatherData);
        modEventBus.addListener(Registration::registerCapabilities);
        modEventBus.addListener(Channel::onRegisterPayloadHandler);
        modEventBus.addListener(Registration::addCreative);

        Registration.BLOCKS.register(modEventBus);
        Registration.ITEMS.register(modEventBus);
        Registration.BLOCK_ENTITIES.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {

    }

    public void gatherData(GatherDataEvent event)
    {
        var fileHelper = event.getExistingFileHelper();
        var pack = event.getGenerator().getVanillaPack(true);
        pack.addProvider(LangGen::new);
        pack.addProvider(pOutput -> new BlockStateGen(pOutput, fileHelper));
        pack.addProvider(pOutput -> new ItemModelGen(pOutput, fileHelper));
    }
}
