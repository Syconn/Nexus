package mod.syconn.nexus.network.packets;

import mod.syconn.nexus.Nexus;
import mod.syconn.nexus.blockentities.AbstractInterfaceBE;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public record RefreshInterface(BlockPos pos) implements CustomPacketPayload {

    public static ResourceLocation ID = new ResourceLocation(Nexus.MODID, "refresh_interface");

    public static RefreshInterface create(FriendlyByteBuf buf) {
        return new RefreshInterface(buf.readBlockPos());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> {
            ctx.player().ifPresent(player -> {
                if (player.level().getBlockEntity(pos) instanceof AbstractInterfaceBE be) be.updateScreen();
            });
        });
    }
}
