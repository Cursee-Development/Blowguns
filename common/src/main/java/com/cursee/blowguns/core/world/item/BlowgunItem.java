package com.cursee.blowguns.core.world.item;

import com.cursee.blowguns.core.registry.ModEnchantments;
import com.cursee.blowguns.core.registry.ModItems;
import com.cursee.blowguns.core.tag.ModItemTags;
import com.cursee.blowguns.core.world.entity.projectile.Dart;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

import java.util.function.Predicate;

public class BlowgunItem extends ProjectileWeaponItem {

    public static final Predicate<ItemStack> DART_ONLY = (itemStack) -> itemStack.is(ModItemTags.DARTS);

    private int fired = 0;
    private int maxShots = 1;

    private int time = 0;
    private int timeToShoot = 15;

    private int inaccuracy = 15;

    public BlowgunItem(Properties properties) {
        super(properties);
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return DART_ONLY;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

        this.maxShots = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.EXTRA_BREATH, player.getItemInHand(hand)) + 1;
        this.inaccuracy = 15 - (5 * EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.FOCUSED_BREATH, player.getItemInHand(hand)));
        this.timeToShoot = 15 - (5 * EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.QUICK_BREATH, player.getItemInHand(hand)));

        player.startUsingItem(hand);

        player.awardStat(Stats.ITEM_USED.get(this));

        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingUseDuration) {
        super.onUseTick(level, entity, stack, remainingUseDuration);

        ItemStack dartStack = entity.getProjectile(stack);

        boolean entityIsPlayer = entity instanceof Player;
        boolean entityIsCreativePlayer = entityIsPlayer && ((Player) entity).getAbilities().instabuild;
        boolean entitySpawnsDart = !dartStack.isEmpty() || entityIsCreativePlayer;

        if (entitySpawnsDart && fired < maxShots && (time == 0 || time % timeToShoot == 0)) {

            level.playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.CAKE_ADD_CANDLE, SoundSource.NEUTRAL, 1.0F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));

            // fire the dart and handle items on the server
            if (!level.isClientSide) {

                // create a dart and fire it from the entity

                DartItem dartItem = (DartItem) (dartStack.getItem() instanceof DartItem ? dartStack.getItem() : ModItems.DART);
                Dart dart = dartItem.createDart(level, dartStack, entity);
                dart.pickup = AbstractArrow.Pickup.DISALLOWED;
                // dart.setEffectsFromItem(dartStack);
                dart.shootFromRotation(entity, entity.getXRot(), entity.getYRot(), 0.0F, 1.5F, (float) this.inaccuracy);
                if (level.addFreshEntity(dart)) fired += 1;

                // damage the blowgun and remove a dart from the entity
                if (!entityIsCreativePlayer) {
                    stack.hurtAndBreak(1, entity, (self) -> self.broadcastBreakEvent(entity.getUsedItemHand()));

                    dartStack.shrink(1);
                    if (dartStack.isEmpty() && entityIsPlayer) {
                        ((Player) entity).getInventory().removeItem(dartStack);
                    }
                }
            }

            if (fired >= maxShots) {
                entity.stopUsingItem();
                if (entityIsPlayer) ((Player) entity).getCooldowns().addCooldown(this, 15);
                fired = 0;
            }
        }

        time += 1;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeCharged) {
        super.releaseUsing(stack, level, entity, timeCharged);
        this.time = 0;
        if (entity instanceof Player) ((Player) entity).getCooldowns().addCooldown(this, 15);
    }

    /**
     * @see BehaviorUtils#isWithinAttackRange(Mob, LivingEntity, int)
     */
    @Override
    public int getDefaultProjectileRange() {
        return 8;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 200; // 10 seconds * 20 ticks per second = 100 ticks
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.TOOT_HORN;
    }

    @Override
    public int getEnchantmentValue() {
        return 22;
    }
}
