package mod.syconn.nexus.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import mod.syconn.nexus.Nexus;
import mod.syconn.nexus.blockentities.DriveBE;
import mod.syconn.nexus.blocks.DriveBlock;
import mod.syconn.nexus.client.model.DriveModel;
import mod.syconn.nexus.util.data.DriveSlot;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public class DriveBER implements BlockEntityRenderer<DriveBE> {

    private final DriveModel model;

    public DriveBER(BlockEntityRendererProvider.Context ctx) {
        model = new DriveModel(ctx.bakeLayer(DriveModel.LAYER_LOCATION));
    }

    public void render(DriveBE pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        if (pBlockEntity.getLevel() != null && pBlockEntity.getLevel().getBlockState(pBlockEntity.getBlockPos()).getBlock() instanceof DriveBlock) {
            pPoseStack.pushPose();
            Direction facing = pBlockEntity.getLevel().getBlockState(pBlockEntity.getBlockPos()).getValue(DriveBlock.FACING);
            pPoseStack.translate(0.5, -0.6875, 0.5);
            pPoseStack.mulPose(Axis.YP.rotationDegrees((facing.get2DDataValue() - 2) * -90));
            pPoseStack.translate(0.25, 0, -0.3);
            for (int i = 0; i < pBlockEntity.getDriveSlots().length; i++) {
                DriveSlot driveSlot = pBlockEntity.getDriveSlots()[i];
                if (driveSlot != null) {
                    pPoseStack.pushPose();
                    pPoseStack.translate( i > 4 ? -0.4375 : 0, i > 4 ?  -0.125 * (i - 5) : -0.125 * i, 0);
                    model.renderToBuffer(pPoseStack, pBuffer.getBuffer(RenderType.entityCutoutNoCull(driveSlot.getTexture())), LightTexture.FULL_BLOCK, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
                    pPoseStack.popPose();
                }
            }
            pPoseStack.popPose();
        }
    }
}