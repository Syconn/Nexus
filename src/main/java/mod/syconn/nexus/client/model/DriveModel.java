package mod.syconn.nexus.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.syconn.nexus.Nexus;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class DriveModel extends Model {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Nexus.MODID, "drive"), "main");
	private final ModelPart drive;

	public DriveModel(ModelPart root) {
        super(RenderType::entityCutoutNoCull);
        this.drive = root.getChild("drive");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition drive = partdefinition.addOrReplaceChild("drive", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, 7.0F, -3.0F, 3.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 16.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 16, 16);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		drive.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}