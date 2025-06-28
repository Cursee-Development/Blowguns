package com.cursee.blowguns.client.model;

import com.cursee.blowguns.Blowguns;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;

public class DartPouchModel<T extends Entity> extends EntityModel<T> {

    // private static ModelPart bakedPouch = null;
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Blowguns.identifier("pouch"), "main");
    private final ModelPart pouch;

    public DartPouchModel(ModelPart root) {
        this.pouch = root.getChild("pouch");
        // bakedPouch = Minecraft.getInstance().getEntityModels().bakeLayer(LAYER_LOCATION);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition pouch = partdefinition.addOrReplaceChild("pouch",
                // CubeListBuilder.create().texOffs(8, 8).addBox(0, 0, 0, 4.0f, 4.0f, 4.0f, new CubeDeformation(0.0F)),
                CubeListBuilder.create().texOffs(8, 8).addBox(0, 0, 0, 3.0f, 2.0f, 1.0f, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        poseStack.pushPose();
        this.pouch.render(poseStack, buffer, packedLight, packedOverlay);
        poseStack.popPose();
    }

    @Override
    public void setupAnim(T t, float v, float v1, float v2, float v3, float v4) {
        // no-op
    }

    public void copyBody(ModelPart model) {
        pouch.copyFrom(model);
    }
}
