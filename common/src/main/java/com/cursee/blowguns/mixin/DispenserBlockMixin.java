package com.cursee.blowguns.mixin;

import com.cursee.blowguns.core.registry.ModItems;
import com.cursee.blowguns.core.world.entity.projectile.Dart;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DispenserBlock.class)
public class DispenserBlockMixin {

    @Unique
    private static boolean blowguns$registeredDispenseBehaviors = false;

    @Inject(method = "getDispenseMethod", at = @At("HEAD"))
    private void more_bows_and_arrows$getDispenseMethod(ItemStack stack, CallbackInfoReturnable<DispenseItemBehavior> cir) {
        if (blowguns$registeredDispenseBehaviors) return;
        DispenserBlock.registerBehavior(ModItems.DART, new AbstractProjectileDispenseBehavior() {
            protected Projectile getProjectile(Level level, Position position, ItemStack itemStack) {
                Dart dart = new Dart(position.x(), position.y(), position.z(), level);
                dart.pickup = AbstractArrow.Pickup.DISALLOWED;
                return dart;
            }
        });
        DispenserBlock.registerBehavior(ModItems.TIPPED_DART, new AbstractProjectileDispenseBehavior() {
            protected Projectile getProjectile(Level level, Position position, ItemStack itemStack) {
                Dart dart = new Dart(position.x(), position.y(), position.z(), level);
                dart.setEffectsFromItem(itemStack);
                dart.pickup = AbstractArrow.Pickup.DISALLOWED;
                return dart;
            }
        });

        blowguns$registeredDispenseBehaviors = true;
    }
}
