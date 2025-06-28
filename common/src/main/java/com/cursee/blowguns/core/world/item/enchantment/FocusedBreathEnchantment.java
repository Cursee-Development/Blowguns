package com.cursee.blowguns.core.world.item.enchantment;

import com.cursee.blowguns.core.world.item.BlowgunItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class FocusedBreathEnchantment extends Enchantment {

    public FocusedBreathEnchantment(Rarity rarity, EquipmentSlot... applicableSlots) {
        super(rarity, EnchantmentCategory.BREAKABLE, applicableSlots);
    }

    public int getMinCost(int enchantmentLevel) {
        return 10 + 20 * (enchantmentLevel - 1);
    }

    public int getMaxCost(int enchantmentLevel) {
        return super.getMinCost(enchantmentLevel) + 50;
    }

    public int getMaxLevel() {
        return 2;
    }

    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof BlowgunItem || super.canEnchant(stack);
    }
}
