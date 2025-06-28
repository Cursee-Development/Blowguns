package com.cursee.blowguns.platform.services;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface IPlatformHelper {

    /**
     * Gets the name of the current platform
     *
     * @return The name of the current platform.
     */
    String getPlatformName();

    /**
     * Checks if a mod with the given id is loaded.
     *
     * @param modId The mod to check if it is loaded.
     * @return True if the mod is loaded, false otherwise.
     */
    boolean isModLoaded(String modId);

    /**
     * Check if the game is currently in a development environment.
     *
     * @return True if in a development environment, false otherwise.
     */
    boolean isDevelopmentEnvironment();

    /**
     * Gets the root directory of the minecraft instance.
     *
     * @return The String form of the minecraft instance's root folder.
     */
    String getGameDirectory();

    /**
     * Check if the mod is loaded in a client instance.
     *
     * @return True if loaded on a client, false otherwise.
     */
    boolean isClientSide();

    /**
     * Gets the name of the environment type as a string.
     *
     * @return The name of the environment type.
     */
    default String getEnvironmentName() {

        return isDevelopmentEnvironment() ? "development" : "production";
    }

    CreativeModeTab.Builder createTabBuilder();

    <T extends Entity> EntityType.Builder<T> createEntityType(BiFunction<EntityType<T>, Level, T> constructor, MobCategory category);

    <E extends Entity, T extends EntityType<E>, R extends EntityRenderer<E>> void registerEntityRenderer(T entityType, Function<EntityRendererProvider.Context, R> entityRendererConstructor);

    void registerTippedDartItemColoring();

    void registerTippedDartRecipeSerializer();

    default boolean additionalSlotToCheck() {
        return isModLoaded("trinkets") || isModLoaded("curios");
    }

    boolean hasDartPouchInAdditionalSlot(LivingEntity entity);
    ItemStack getDartFromAdditionalSlot(LivingEntity entity);
    void removeDartFromAdditionalSlot(LivingEntity entity);
}