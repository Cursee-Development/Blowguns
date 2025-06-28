package com.cursee.blowguns.mixin;

import com.cursee.blowguns.core.util.EnchantmentCanApply;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = EnchantmentHelper.class, priority = 9999)
public class EnchantmentHelperMixin {

//    @Redirect(
//            method = "getAvailableEnchantmentResults",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/world/item/enchantment/EnchantmentCategory;canEnchant(Lnet/minecraft/world/item/Item;)Z"
//            )
//    )
//    private static boolean redirectCanEnchant(EnchantmentCategory category, Item item, int level, ItemStack stack, boolean allowTreasure) {
//        Enchantment enchantment = null;
//        for (Enchantment e : BuiltInRegistries.ENCHANTMENT) {
//            if (e.category == category && e.category.canEnchant(item)) {
//                enchantment = e;
//                break;
//            }
//        }
//        if (enchantment == null) return false;
//        return ((EnchantmentCanApply)(Object)enchantment).canApplyAtEnchantingTable(stack);
//    }


    @Inject(method = "getAvailableEnchantmentResults", at = @At("HEAD"), cancellable = true)
    private static void cursee$overrideAvailableEnchantments(int level, ItemStack stack, boolean allowTreasure, CallbackInfoReturnable<List<EnchantmentInstance>> cir) {
        List<EnchantmentInstance> list = new ArrayList<>();
        boolean isBook = stack.is(Items.BOOK);

        for (Enchantment enchantment : BuiltInRegistries.ENCHANTMENT) {
            boolean allow = (!enchantment.isTreasureOnly() || allowTreasure)
                    && enchantment.isDiscoverable()
                    && (((EnchantmentCanApply)(Object)enchantment).canApplyAtEnchantingTable(stack) || isBook);

            if (!allow) continue;

            for (int i = enchantment.getMaxLevel(); i >= enchantment.getMinLevel(); --i) {
                if (level >= enchantment.getMinCost(i) && level <= enchantment.getMaxCost(i)) {
                    list.add(new EnchantmentInstance(enchantment, i));
                    break;
                }
            }
        }

        cir.setReturnValue(list); // ⬅️ Prevent original method from executing
    }
}
