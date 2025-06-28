package com.cursee.blowguns.core.registry;

import com.cursee.blowguns.Blowguns;
import com.cursee.blowguns.core.world.entity.projectile.Dart;
import com.cursee.blowguns.platform.Services;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.function.BiConsumer;

public class ModEntities {

    public static final EntityType<Dart> DART = Services.PLATFORM.<Dart>createEntityType(Dart::new, MobCategory.MISC)
            .sized(0.5f, 0.5f)
            .clientTrackingRange(4)
            .updateInterval(20)
            .build(forge() ? null : Blowguns.identifier("dart").toString());

    public static void register(BiConsumer<EntityType<?>, ResourceLocation> consumer) {
        consumer.accept(DART, Blowguns.identifier("dart"));
    }

    private static boolean forge() {
        return Services.PLATFORM.getEnvironmentName().equalsIgnoreCase("forge");
    }
}
