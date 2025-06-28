package com.cursee.blowguns;

import com.cursee.blowguns.client.model.DartPouchModel;
import com.cursee.blowguns.client.renderer.entity.layers.DartPouchRenderLayer;
import com.cursee.blowguns.core.compat.ForgeCuriosHelper;
import com.cursee.blowguns.platform.Services;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.function.Consumer;
import java.util.function.Function;

public class BlowgunsClientForge {

    public BlowgunsClientForge(final IEventBus modEventBus) {

        BlowgunsClient.init();

        modEventBus.addListener((Consumer<FMLClientSetupEvent>) event -> {
            BlowgunsClient.setup();
            event.enqueueWork(() -> {
                if (Services.PLATFORM.additionalSlotToCheck()) ForgeCuriosHelper.registerRenderersForCurios();
            });
        });

        modEventBus.addListener((Consumer<EntityRenderersEvent.RegisterLayerDefinitions>) event -> {
            event.registerLayerDefinition(DartPouchModel.LAYER_LOCATION, DartPouchModel::createBodyLayer);
        });

        modEventBus.addListener((Consumer<EntityRenderersEvent.AddLayers>) event -> {
            addLayerToPlayerSkin(event, "default", DartPouchRenderLayer::new);
            addLayerToPlayerSkin(event, "slim", DartPouchRenderLayer::new);
        });
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static <E extends Player, M extends HumanoidModel<E>>
    void addLayerToPlayerSkin(EntityRenderersEvent.AddLayers event, String skinName, Function<LivingEntityRenderer<E, M>, ? extends RenderLayer<E, M>> factory) {
        LivingEntityRenderer renderer = event.getSkin(skinName);
        if (renderer != null) renderer.addLayer(factory.apply(renderer));
    }
}
