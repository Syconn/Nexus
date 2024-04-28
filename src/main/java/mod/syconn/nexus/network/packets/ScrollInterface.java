package mod.syconn.nexus.network.packets;

import mod.syconn.nexus.Nexus;
import mod.syconn.nexus.blockentities.InterfaceBE;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public record ScrollInterface(BlockPos pos, float scrollOffset) implements CustomPacketPayload {

    public static ResourceLocation ID = new ResourceLocation(Nexus.MODID, "scroll_interface");

    public static ScrollInterface create(FriendlyByteBuf buf) {
        return new ScrollInterface(buf.readBlockPos(), buf.readFloat());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeFloat(scrollOffset);
    }

    public ResourceLocation id() {
        return ID;
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> {
            ctx.player().ifPresent(player -> {
                if (player.level().getBlockEntity(pos) instanceof InterfaceBE be) {
                    int line = (int) Math.ceil(be.getInvSize() / 9.0) - 5;
                    be.setLine((int) (line * scrollOffset));
                }
            });
        });
    }
}