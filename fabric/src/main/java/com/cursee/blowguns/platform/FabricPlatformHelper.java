package com.cursee.blowguns.platform;

import com.cursee.blowguns.Blowguns;
import com.cursee.blowguns.core.registry.ModItems;
import com.cursee.blowguns.core.world.item.DartPouchItem;
import com.cursee.blowguns.core.world.item.crafting.TippedDartRecipe;
import com.cursee.blowguns.platform.services.IPlatformHelper;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Function;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {

        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public String getGameDirectory() {

        return FabricLoader.getInstance().getGameDir().toString();
    }

    @Override
    public boolean isClientSide() {

        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    @Override
    public CreativeModeTab.Builder createTabBuilder() {
        return FabricItemGroup.builder();
    }

    @Override
    public <T extends Entity> EntityType.Builder<T> createEntityType(BiFunction<EntityType<T>, Level, T> constructor, MobCategory category) {
        return EntityType.Builder.of(constructor::apply, category);
    }

    @Override
    public <E extends Entity, T extends EntityType<E>, R extends EntityRenderer<E>> void registerEntityRenderer(T entityType, Function<EntityRendererProvider.Context, R> entityRendererConstructor) {
        EntityRendererRegistry.register(entityType, entityRendererConstructor::apply);
    }

    @Override
    public void registerTippedDartItemColoring() {
        ColorProviderRegistry.ITEM.register((itemStack, color) -> color == 0 ? PotionUtils.getColor(itemStack) : -1, ModItems.TIPPED_DART);
    }

    public void registerTippedDartRecipeSerializer() {
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Blowguns.identifier("crafting_special_tipped_dart"), new SimpleCraftingRecipeSerializer<TippedDartRecipe>(TippedDartRecipe::new));
    }

    @Override
    public ItemStack getDartFromAdditionalSlot(LivingEntity entity) {

        AtomicReference<ItemStack> atomicCopy = new AtomicReference<>(ItemStack.EMPTY);
        TrinketsApi.getTrinketComponent(entity).ifPresent(component -> {
//            component.forEach((slotReference, pouchStack) -> {
//                if (pouchStack.getItem() instanceof DartPouchItem) {
//                    Optional<ItemStack> optional = DartPouchItem.removeOne(pouchStack);
//                    optional.ifPresent(atomicCopy::set);
//                }
//            });
//            ItemStack pouchStack = component.getAllEquipped().get(0).getB();
//            if (pouchStack.getItem() instanceof DartPouchItem) {
//                Optional<ItemStack> optional = DartPouchItem.removeOne(pouchStack);
//                optional.ifPresent(atomicCopy::set);
//                DartPouchItem.add(pouchStack, atomicCopy.get());
//            }
        });

        return atomicCopy.get();
    }

    @Override
    public void removeDartFromAdditionalSlot(LivingEntity entity) {
        AtomicReference<ItemStack> atomicCopy = new AtomicReference<>(ItemStack.EMPTY);
        TrinketsApi.getTrinketComponent(entity).ifPresent(component -> {
            component.forEach((slotReference, pouchStack) -> {
                if (pouchStack.getItem() instanceof DartPouchItem) {
                    Optional<ItemStack> optional = DartPouchItem.removeOne(pouchStack);
                    optional.ifPresent(stack -> {
                        if ((!(entity instanceof Player player && player.getAbilities().instabuild))) stack.shrink(1);
                        atomicCopy.set(stack);
                    });
                    DartPouchItem.add(pouchStack, atomicCopy.get());
                }
            });


            if (component.getAllEquipped().get(0).getB().getItem() instanceof DartPouchItem) {
                Optional<ItemStack> optional = DartPouchItem.removeOne(component.getAllEquipped().get(0).getB());
                optional.ifPresent(stack -> {
                    if ((!(entity instanceof Player player && player.getAbilities().instabuild))) stack.shrink(1);
                    atomicCopy.set(stack);
                });
                DartPouchItem.add(component.getAllEquipped().get(0).getB(), atomicCopy.get());
            }
        });
    }

    @Override
    public boolean hasDartPouchInAdditionalSlot(LivingEntity entity) {
        return false;
    }
}
