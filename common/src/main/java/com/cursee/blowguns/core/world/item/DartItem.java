package com.cursee.blowguns.core.world.item;

import com.cursee.blowguns.core.world.entity.projectile.Dart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class DartItem extends Item {

    public DartItem(Properties properties) {
        super(properties);
    }

    public Dart createDart(Level level, ItemStack stack, LivingEntity shooter) {
        Dart dart = new Dart(level, shooter);
        dart.setEffectsFromItem(stack);
        return dart;
    }


}
