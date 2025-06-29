package com.cursee.blowguns.mixin;

import com.cursee.blowguns.client.renderer.entity.layers.DartPouchRenderLayer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public class FabricPlayerRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void camping_$_init(EntityRendererProvider.Context context, boolean useSlimModel, CallbackInfo ci) {
        PlayerRenderer instance = (PlayerRenderer) (Object) this;
        instance.addLayer(new DartPouchRenderLayer(instance));
    }
}
