package com.cursee.blowguns.core.compat;

import com.cursee.blowguns.client.renderer.entity.layers.DartPouchRenderLayer;
import com.cursee.blowguns.core.registry.ModItems;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.Trinket;
import dev.emi.trinkets.api.TrinketsApi;
import dev.emi.trinkets.api.client.TrinketRenderer;
import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class FabricTrinketsHelper {

    public static void registerTrinkets() {
        TrinketsApi.registerTrinket(ModItems.DART_POUCH, new BackpackTrinket());
    }

    public static void registerRenderersForTrinkets() {
        TrinketRendererRegistry.registerRenderer(ModItems.DART_POUCH, new DartPouchTrinketRenderer());
    }

    public static class DartPouchTrinketRenderer implements TrinketRenderer {

        @Override
        public void render(ItemStack itemStack, SlotReference slotReference, EntityModel<? extends LivingEntity> entityModel, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, LivingEntity livingEntity, float v, float v1, float v2, float v3, float v4, float v5) {
            Model model = DartPouchRenderLayer.BAKED_MODEL;
            model.renderToBuffer(poseStack, multiBufferSource.getBuffer(model.renderType(DartPouchRenderLayer.TEXTURE_LOCATION)), i, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F);
        }
    }
}
