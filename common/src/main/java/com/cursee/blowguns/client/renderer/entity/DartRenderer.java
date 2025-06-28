package com.cursee.blowguns.client.renderer.entity;

import com.cursee.blowguns.Blowguns;
import com.cursee.blowguns.core.world.entity.projectile.Dart;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class DartRenderer extends ArrowRenderer<Dart> {

    public static final ResourceLocation DART_TEXTURE = Blowguns.identifier("textures/entity/dart/dart.png");

    public DartRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(Dart dart) {
        return DART_TEXTURE;
    }

    @Override
    public boolean shouldRender(Dart livingEntity, Frustum camera, double camX, double camY, double camZ) {
        return true;
    }
}
