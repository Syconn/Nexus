package mod.syconn.nexus.client;

import mod.syconn.nexus.client.screen.NetworkManagerScreen;
import mod.syconn.nexus.network.Channel;
import mod.syconn.nexus.network.packets.AddStack;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.Map;

public class ClientHooks {

    public static void open(Map<Block, Integer> blocks) {
        Minecraft.getInstance().setScreen(new NetworkManagerScreen(blocks));
    }

    public static void addStackToPlayer(ItemStack stack, BlockPos pos) {
        Channel.sendToServer(new AddStack(stack, pos));
    }
}
