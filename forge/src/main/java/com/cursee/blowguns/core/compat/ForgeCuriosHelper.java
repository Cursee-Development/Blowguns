package com.cursee.blowguns.core.compat;

import com.cursee.blowguns.client.renderer.entity.layers.DartPouchRenderLayer;
import com.cursee.blowguns.core.registry.ModItems;
import com.cursee.blowguns.core.world.item.DartPouchItem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class ForgeCuriosHelper {

    public static void registerRenderersForCurios() {
        CuriosRendererRegistry.register(ModItems.DART_POUCH, BackpackCuriosRenderer::new);
    }

    public static class BackpackCuriosRenderer implements ICurioRenderer {

        @Override
        public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack itemStack, SlotContext slotContext, PoseStack poseStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource multiBufferSource, int i, float v, float v1, float v2, float v3, float v4, float v5) {
            Model model = DartPouchRenderLayer.BAKED_MODEL;
            model.renderToBuffer(poseStack, multiBufferSource.getBuffer(model.renderType(DartPouchRenderLayer.TEXTURE_LOCATION)), i, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F);
        }
    }
}
