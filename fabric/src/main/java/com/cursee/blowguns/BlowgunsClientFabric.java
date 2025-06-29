package com.cursee.blowguns;

import com.cursee.blowguns.client.model.DartPouchModel;
import com.cursee.blowguns.core.compat.FabricTrinketsHelper;
import com.cursee.blowguns.platform.Services;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;

public class BlowgunsClientFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlowgunsClient.init();
        BlowgunsClient.setup();

        EntityModelLayerRegistry.registerModelLayer(DartPouchModel.LAYER_LOCATION, DartPouchModel::createBodyLayer);

//        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) -> {
//            if (entityType == EntityType.PLAYER) {
//                registrationHelper.register(new DartPouchRenderLayer());
//            }
//        });
        if (Services.PLATFORM.additionalSlotToCheck()) FabricTrinketsHelper.registerRenderersForTrinkets();
    }

//    private static <E extends Player, M extends HumanoidModel<E>>
//    void addLayerToPlayerSkin(LivingEntityFeatureRendererRegistrationCallback event, String skinName, Function<LivingEntityRenderer<E, M>, ? extends RenderLayer<E, M>> factory) {
//        LivingEntityRenderer renderer = event.getSkin(skinName);
//        if (renderer != null) renderer.addLayer(factory.apply(renderer));
//    }
}
