package com.cursee.blowguns.mixin;

import com.cursee.blowguns.core.util.EnchantmentCanApply;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = Enchantment.class, priority = 9999)
public abstract class EnchantmentMixin implements EnchantmentCanApply {

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        Enchantment instance = (Enchantment) (Object) this;
        return instance.category.canEnchant(stack.getItem());
    }
}
