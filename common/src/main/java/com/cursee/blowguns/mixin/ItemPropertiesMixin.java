package com.cursee.blowguns.mixin;

import com.cursee.blowguns.core.registry.ModItems;
import com.cursee.blowguns.core.world.item.DartPouchItem;
import com.google.common.collect.Maps;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ItemProperties.class)
public class ItemPropertiesMixin {

    @Shadow @Final
    private static final Map<Item, Map<ResourceLocation, ItemPropertyFunction>> PROPERTIES = Maps.newHashMap();

    @Shadow
    private static void register(Item item, ResourceLocation name, ClampedItemPropertyFunction property) {
        throw new AssertionError();
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void blowguns$clinit(CallbackInfo ci) {
        if (PROPERTIES != null) register(ModItems.DART_POUCH, new ResourceLocation("filled"), (p_174625_, p_174626_, p_174627_, p_174628_) -> {
            return DartPouchItem.getFullnessDisplay(p_174625_);
        });
    }
}
