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
        this.addBlock(Registration.INTERFACE, "Interface");
        this.addBlock(Registration.CRAFTING_INTERFACE, "Crafting Interface");
        this.addBlock(Registration.EXTERNAL_STORAGE, "External Storage");
        this.addBlock(Registration.DRIVE, "Drive Block");
        this.addItem(Registration.STORAGE_DRIVE, "Storage Drive");
        this.addItem(Registration.DIAMOND_UPGRADE, "Diamond Upgrade");
        this.addItem(Registration.GOLD_UPGRADE, "Gold Upgrade");
        this.addItem(Registration.IRON_UPGRADE, "Iron Upgrade");
        this.addItem(Registration.EMERALD_UPGRADE, "Emerald Upgrade");
        this.addItem(Registration.NETHERITE_UPGRADE, "Netherite Upgrade");
        this.add("itemGroup." + MODID, "Nexus");
    }
}
