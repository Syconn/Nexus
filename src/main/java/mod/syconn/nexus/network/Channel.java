package mod.syconn.nexus.network;

import mod.syconn.nexus.Nexus;
import mod.syconn.nexus.network.packets.AddStack;
import mod.syconn.nexus.network.packets.RefreshInterface;
import mod.syconn.nexus.network.packets.ScrollInterface;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

public class Channel {

    public static void onRegisterPayloadHandler(RegisterPayloadHandlerEvent event) {
        final IPayloadRegistrar registrar = event.registrar(Nexus.MODID).versioned("1.0").optional();
        registrar.play(ScrollInterface.ID, ScrollInterface::create, handler -> handler.server(ScrollInterface::handle));
        registrar.play(RefreshInterface.ID, RefreshInterface::create, handler -> handler.server(RefreshInterface::handle));
        registrar.play(AddStack.ID, AddStack::create, handler -> handler.server(AddStack::handle));
    }

    public static <MSG extends CustomPacketPayload> void sendToServer(MSG message) {
        PacketDistributor.SERVER.noArg().send(message);
    }

    public static <MSG extends CustomPacketPayload> void sendToPlayer(MSG message, ServerPlayer player) {
        PacketDistributor.PLAYER.with(player).send(message);
    }
}
