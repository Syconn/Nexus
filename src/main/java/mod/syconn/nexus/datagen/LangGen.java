package mod.syconn.nexus.datagen;

import mod.syconn.nexus.Registration;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

import static mod.syconn.nexus.Nexus.MODID;

public class LangGen extends LanguageProvider {

    public LangGen(PackOutput output) {
        super(output, MODID, "en_us");
    }

    protected void addTranslations()
    {
        this.addBlock(Registration.NEXUS, "Nexus");
        this.addBlock(Registration.ITEM_PIPE, "Item Pipe");
        this.add("itemGroup." + MODID, "Nexus");
    }
}
