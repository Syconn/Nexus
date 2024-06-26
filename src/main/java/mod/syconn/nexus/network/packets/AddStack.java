package mod.syconn.nexus.network.packets;

import mod.syconn.nexus.Nexus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public record AddStack(ItemStack stack) implements CustomPacketPayload {

    public static ResourceLocation ID = new ResourceLocation(Nexus.MODID, "add_stack");

    public static AddStack create(FriendlyByteBuf buf) {
        return new AddStack(buf.readItem());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeItem(stack);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> {
            ctx.player().ifPresent(player -> {
                player.addItem(stack);
            });
        });
    }
}
