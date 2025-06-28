package com.cursee.blowguns.core.registry;

import com.cursee.blowguns.Blowguns;
import com.cursee.blowguns.platform.Services;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;

import java.util.function.BiConsumer;

public class ModTabs {

    public static final CreativeModeTab BLOWGUNS_TAB = Services.PLATFORM.createTabBuilder()
            .icon(() -> new ItemStack(ModItems.BLOWGUN))
            .title(Component.translatable("itemGroup.blowguns"))
            .displayItems((itemDisplayParameters, output) -> {
                output.accept(ModItems.BLOWGUN);
                output.accept(ModItems.DART_POUCH);
                output.accept(ModItems.DART);
                itemDisplayParameters.holders().lookup(Registries.POTION).ifPresent((lookup) -> generatePotionEffectTypes(output, lookup, ModItems.TIPPED_DART));
            }).build();

    public static void register(BiConsumer<CreativeModeTab, ResourceLocation> consumer) {
        consumer.accept(BLOWGUNS_TAB, Blowguns.identifier("blowguns_tab"));
    }

    private static void generatePotionEffectTypes(CreativeModeTab.Output output, HolderLookup<Potion> potions, Item potionItem) {
        potions.listElements()
                .filter((potion) -> !potion.is(Potions.EMPTY_ID))
                .map((potion) -> PotionUtils.setPotion(new ItemStack(potionItem), potion.value()))
                .forEach(output::accept);
    }
}
