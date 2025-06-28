package com.cursee.blowguns;

import com.cursee.blowguns.client.renderer.entity.DartRenderer;
import com.cursee.blowguns.core.registry.ModEntities;
import com.cursee.blowguns.platform.Services;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class BlowgunsClient {

    public static void init() {
        Services.PLATFORM.registerTippedDartItemColoring();
    }

    public static void setup() {
        Services.PLATFORM.registerEntityRenderer(ModEntities.DART, DartRenderer::new);
    }

//    private static <T extends Item> void registerDartPouchProperties(T item) {
//
//
//        ItemProperties.register(Items.BUNDLE, new ResourceLocation("filled"), (p_174625_, p_174626_, p_174627_, p_174628_) -> {
//            return BundleItem.getFullnessDisplay(p_174625_);
//        });
//    }
}
