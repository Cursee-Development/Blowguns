package com.cursee.blowguns.platform;

import com.cursee.blowguns.Blowguns;
import com.cursee.blowguns.BlowgunsForge;
import com.cursee.blowguns.Constants;
import com.cursee.blowguns.core.registry.ModItems;
import com.cursee.blowguns.core.world.item.DartPouchItem;
import com.cursee.blowguns.core.world.item.crafting.TippedDartRecipe;
import com.cursee.blowguns.platform.services.IPlatformHelper;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.item.crafting.TippedArrowRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.RegisterEvent;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {

        return "Forge";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return !FMLLoader.isProduction();
    }

    @Override
    public String getGameDirectory() {

        return FMLPaths.GAMEDIR.get().toString();
    }

    @Override
    public boolean isClientSide() {

        return FMLEnvironment.dist == Dist.CLIENT;
    }

    @Override
    public CreativeModeTab.Builder createTabBuilder() {
        return CreativeModeTab.builder().withTabsBefore(CreativeModeTabs.SPAWN_EGGS);
    }

    @Override
    public <T extends Entity> EntityType.Builder<T> createEntityType(BiFunction<EntityType<T>, Level, T> constructor, MobCategory category) {
        return EntityType.Builder.<T>of(constructor::apply, category);
    }

    @Override
    public <E extends Entity, T extends EntityType<E>, R extends EntityRenderer<E>> void registerEntityRenderer(T entityType, Function<EntityRendererProvider.Context, R> entityRendererConstructor) {
        EntityRenderers.register(entityType, entityRendererConstructor::apply);
    }

    @Override
    public void registerTippedDartItemColoring() {
        BlowgunsForge.EVENT_BUS.addListener((Consumer<RegisterColorHandlersEvent.Item>) event -> {
            event.register((stack, i) -> i == 0 ? PotionUtils.getColor(stack) : -1, ModItems.TIPPED_DART);
        });
    }

    public void registerTippedDartRecipeSerializer() {
        BlowgunsForge.EVENT_BUS.addListener((Consumer<RegisterEvent>) event -> {
            if (event.getRegistryKey() != Registries.RECIPE_SERIALIZER) return;
            event.register(Registries.RECIPE_SERIALIZER, recipeSerializerRegisterHelper -> {
                recipeSerializerRegisterHelper.register(Blowguns.identifier("crafting_special_tipped_dart"), new SimpleCraftingRecipeSerializer<TippedDartRecipe>(TippedDartRecipe::new));
            });
        });
    }

    @Override
    public ItemStack getDartFromAdditionalSlot(LivingEntity entity) {

        AtomicReference<ItemStack> atomicCopy = new AtomicReference<>(ItemStack.EMPTY);

        CuriosApi.getCuriosInventory(entity).ifPresent(iCuriosItemHandler -> {
            iCuriosItemHandler.findFirstCurio(ModItems.DART_POUCH).ifPresent(slotResult -> {
                if (slotResult.stack().getItem() instanceof DartPouchItem) {
                    Optional<ItemStack> optional = DartPouchItem.removeOne(slotResult.stack());
                    optional.ifPresent(atomicCopy::set);
                    DartPouchItem.add(slotResult.stack(), atomicCopy.get());
                }
            });
        });

        return atomicCopy.get();
    }

    @Override
    public void removeDartFromAdditionalSlot(LivingEntity entity) {
        AtomicReference<ItemStack> atomicCopy = new AtomicReference<>(ItemStack.EMPTY);

        CuriosApi.getCuriosInventory(entity).ifPresent(iCuriosItemHandler -> {
            iCuriosItemHandler.findFirstCurio(ModItems.DART_POUCH).ifPresent(slotResult -> {
                if (slotResult.stack().getItem() instanceof DartPouchItem) {
                    Optional<ItemStack> optional = DartPouchItem.removeOne(slotResult.stack());
                    optional.ifPresent(stack -> {
                        if (!(entity instanceof Player player && player.getAbilities().instabuild)) stack.setCount(stack.getCount() - 1);
                        atomicCopy.set(stack);
                    });
                    DartPouchItem.add(slotResult.stack(), atomicCopy.get());
                }
            });
        });
    }

    @Override
    public boolean hasDartPouchInAdditionalSlot(LivingEntity entity) {

        AtomicBoolean foundPouch = new AtomicBoolean(false);

        CuriosApi.getCuriosInventory(entity).ifPresent(iCuriosItemHandler -> {
            iCuriosItemHandler.findFirstCurio(ModItems.DART_POUCH).ifPresent(slotResult -> foundPouch.set(true));
        });

        return foundPouch.get();
    }
}