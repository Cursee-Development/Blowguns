package com.cursee.blowguns.client.renderer.entity.layers;

import com.cursee.blowguns.Blowguns;
import com.cursee.blowguns.client.model.DartPouchModel;
import com.cursee.blowguns.core.world.item.DartPouchItem;
import com.cursee.blowguns.platform.Services;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.concurrent.atomic.AtomicBoolean;

public class DartPouchRenderLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public static final ResourceLocation TEXTURE_LOCATION = Blowguns.identifier("textures/entity/model/dart_pouch.png");
    public static final DartPouchModel<LivingEntity> BAKED_MODEL = new DartPouchModel<LivingEntity>(Minecraft.getInstance().getEntityModels().bakeLayer(DartPouchModel.LAYER_LOCATION));

    public DartPouchRenderLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> $$0) {
        super($$0);
    }

    static final float PIXEL = 0.0625f; // equal to 1.0f divided by 16.0f, or 1/16th of a block

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, AbstractClientPlayer player, float v, float v1, float v2, float v3, float v4, float v5) {
        AtomicBoolean foundPouchOnPlayer = new AtomicBoolean(false);

        for (int index = 0; index < player.getInventory().items.size(); index++) {
            if (player.getInventory().items.get(index).getItem() instanceof DartPouchItem) {
                foundPouchOnPlayer.set(true);
                break;
            }
        }

        if (!foundPouchOnPlayer.get() && Services.PLATFORM.additionalSlotToCheck()) {
            foundPouchOnPlayer.set(Services.PLATFORM.hasDartPouchInAdditionalSlot(player));
        }

        if (!foundPouchOnPlayer.get()) return;

        poseStack.pushPose();
        BAKED_MODEL.copyBody(this.getParentModel().body);
        BAKED_MODEL.renderToBuffer(poseStack, multiBufferSource.getBuffer(BAKED_MODEL.renderType(TEXTURE_LOCATION)), i, OverlayTexture.NO_OVERLAY, 0, 0, 0, 0.5f);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(PIXEL, PIXEL * 7, -(PIXEL * 3)); // pos x is left, poos y is down, pos z is back
        if (player.isCrouching()) poseStack.translate(0, PIXEL, PIXEL*4);
        BAKED_MODEL.copyBody(this.getParentModel().body);
        BAKED_MODEL.renderToBuffer(poseStack, multiBufferSource.getBuffer(BAKED_MODEL.renderType(TEXTURE_LOCATION)), i, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F);
        poseStack.popPose();
    }
}
