package mod.syconn.nexus.network.packets;

import mod.syconn.nexus.Nexus;
import mod.syconn.nexus.blockentities.AbstractInterfaceBE;
import mod.syconn.nexus.util.ItemStackHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public record AddStack(ItemStack stack, BlockPos pos) implements CustomPacketPayload {

    public static ResourceLocation ID = new ResourceLocation(Nexus.MODID, "add_stack");

    public static AddStack create(FriendlyByteBuf buf) {
        return new AddStack(buf.readItem(), buf.readBlockPos());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeItem(stack);
        buf.writeBlockPos(pos);
    }

    public ResourceLocation id() {
        return ID;
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> ctx.player().ifPresent(player -> {
            if (player.level() instanceof ServerLevel sl && player.level().getBlockEntity(pos) instanceof AbstractInterfaceBE be) {
                ItemStack addStack = ItemStackHelper.canAddItemStack(stack.copy(), sl, be.getUUID(), true);
                if (!ItemStack.matches(stack.copy(), addStack)) player.addItem(addStack);
            }
        }));
    }
}
