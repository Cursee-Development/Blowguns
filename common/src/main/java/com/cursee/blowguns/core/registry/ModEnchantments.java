package com.cursee.blowguns.core.registry;

import com.cursee.blowguns.Blowguns;
import com.cursee.blowguns.core.world.item.enchantment.ExtraBreathEnchantment;
import com.cursee.blowguns.core.world.item.enchantment.FocusedBreathEnchantment;
import com.cursee.blowguns.core.world.item.enchantment.QuickBreathEnchantment;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.function.BiConsumer;

public class ModEnchantments {

    public static final Enchantment EXTRA_BREATH = new ExtraBreathEnchantment(Enchantment.Rarity.COMMON, EquipmentSlot.MAINHAND);
    public static final Enchantment FOCUSED_BREATH = new FocusedBreathEnchantment(Enchantment.Rarity.COMMON, EquipmentSlot.MAINHAND);
    public static final Enchantment QUICK_BREATH = new QuickBreathEnchantment(Enchantment.Rarity.COMMON, EquipmentSlot.MAINHAND);

    public static void register(BiConsumer<Enchantment, ResourceLocation> consumer) {
        consumer.accept(EXTRA_BREATH, Blowguns.identifier("extra_breath"));
        consumer.accept(FOCUSED_BREATH, Blowguns.identifier("focused_breath"));
        consumer.accept(QUICK_BREATH, Blowguns.identifier("quick_breath"));
    }
}
