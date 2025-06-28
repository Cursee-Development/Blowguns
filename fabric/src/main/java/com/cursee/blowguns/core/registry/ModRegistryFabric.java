package com.cursee.blowguns.core.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ModRegistryFabric {

    public static void register() {
        bind(BuiltInRegistries.ITEM, ModItems::register);
        bind(BuiltInRegistries.ENCHANTMENT, ModEnchantments::register);
        bind(BuiltInRegistries.CREATIVE_MODE_TAB, ModTabs::register);
        bind(BuiltInRegistries.ENTITY_TYPE, ModEntities::register);
    }

    private static <T> void bind(Registry<? super T> registry, Consumer<BiConsumer<T, ResourceLocation>> source) {
        source.accept((t, resourceLocation) -> {
            Registry.register(registry, resourceLocation, t);
        });
    }
}
