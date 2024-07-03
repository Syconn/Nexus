package mod.syconn.nexus.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpgradeItem extends Item {

    private final int upgradeSpeed;

    public UpgradeItem(Properties p_41383_, int speed) {
        super(p_41383_.rarity(Rarity.EPIC));
        upgradeSpeed = speed;
    }

    public int getUpgradeSpeed() {
        return upgradeSpeed;
    }

    @Override
    public void appendHoverText(ItemStack p_41421_, @Nullable Level p_41422_, List<Component> p_41423_, TooltipFlag p_41424_) {
        p_41423_.add(Component.translatable("Upgrade Speed: " + upgradeSpeed));
        super.appendHoverText(p_41421_, p_41422_, p_41423_, p_41424_);
    }
}
