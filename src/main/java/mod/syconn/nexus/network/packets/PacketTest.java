package mod.syconn.nexus.network.packets;

import mod.syconn.nexus.Nexus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public record PacketTest(BlockPos pos, int button) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(Nexus.MODID, "test");

    public static PacketTest create(FriendlyByteBuf buf) {
        return new PacketTest(buf.readBlockPos(), buf.readByte());
    }

    public static PacketTest create(BlockPos pos, int button) {
        return new PacketTest(pos, button);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeByte(button);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> {
            ctx.player().ifPresent(player -> {
                //CODE
                return;
            });
        });
    }
}