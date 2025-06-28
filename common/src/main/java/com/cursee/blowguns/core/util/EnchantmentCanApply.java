package com.cursee.blowguns.core.util;

import net.minecraft.world.item.ItemStack;

public interface EnchantmentCanApply {
    boolean canApplyAtEnchantingTable(ItemStack stack);
}
