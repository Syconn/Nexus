package mod.syconn.nexus.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.syconn.nexus.blockentities.DriveBE;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class DriveBER implements BlockEntityRenderer<DriveBE> {

    public DriveBER(BlockEntityRendererProvider.Context ctx) {

    }

    public void render(DriveBE pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        System.out.println("Here");
    }
}