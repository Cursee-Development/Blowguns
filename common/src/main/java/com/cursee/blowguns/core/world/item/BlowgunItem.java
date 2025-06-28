package com.cursee.blowguns.core.world.item;

import com.cursee.blowguns.Constants;
import com.cursee.blowguns.core.registry.ModEnchantments;
import com.cursee.blowguns.core.registry.ModItems;
import com.cursee.blowguns.core.tag.ModItemTags;
import com.cursee.blowguns.core.world.entity.projectile.Dart;
import com.cursee.blowguns.platform.Services;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

public class BlowgunItem extends ProjectileWeaponItem {

    public static final Predicate<ItemStack> DART_ONLY = (itemStack) -> itemStack.is(ModItemTags.DARTS);

    public BlowgunItem(Properties properties) {
        super(properties);
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return DART_ONLY;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }

    // private int time = 0;
    private final Map<UUID, Integer> time = new HashMap<>();
    private final Map<UUID, Integer> fired = new HashMap<>();

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack blowgunStack, int remainingUseDuration) {
        super.onUseTick(level, entity, blowgunStack, remainingUseDuration);
        if (!entity.isUsingItem()) return;

        final UUID entityUUID = entity.getUUID();

        boolean entityIsPlayer = entity instanceof Player;
        boolean entityIsCreativePlayer = entityIsPlayer && ((Player) entity).getAbilities().instabuild;

        int maxShots = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.EXTRA_BREATH, entity.getItemInHand(entity.getUsedItemHand())) + 1;
        int timeToShoot = 15 - (5 * EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.QUICK_BREATH, entity.getItemInHand(entity.getUsedItemHand())));

        if (!this.time.containsKey(entityUUID)) this.time.put(entityUUID, 0);
        else if ((fired.computeIfAbsent(entityUUID, uuid -> 0) >= maxShots || this.time.computeIfAbsent(entityUUID, uuid -> 0) >= this.getUseDuration(blowgunStack)) && entity instanceof Player player) {
            player.getCooldowns().addCooldown(this, 15);
        }

        if (fired.computeIfAbsent(entityUUID, uuid -> 0) >= maxShots || this.time.computeIfAbsent(entityUUID, uuid -> 0) >= this.getUseDuration(blowgunStack)) {
            entity.stopUsingItem();
            if (entityIsPlayer) ((Player) entity).getCooldowns().addCooldown(this, 15);
            fired.put(entityUUID, 0);
            time.put(entityUUID, 0);
        }

        // used if we search for the pouch in the player's inventory, so we don't have to traverse their inventory twice
        AtomicInteger pouchIndex = new AtomicInteger(-1);

        AtomicBoolean dartFromInventoryPouch = new AtomicBoolean(false);
        boolean dartFromModSlotPouch = false;

        // attempt to get dart from entity inventory
        final AtomicReference<ItemStack> dartStack = new AtomicReference<ItemStack>(entity.getProjectile(blowgunStack));

        // attempt to get dart from pouch in player inventory

        if (dartStack.get().isEmpty() && entity instanceof Player player && !dartFromInventoryPouch.get()) {

            for (int i = 0; i < player.getInventory().items.size(); i++) {
                ItemStack pouchStack = player.getInventory().items.get(i);

                if (pouchStack.getItem() instanceof DartPouchItem) {
                    Optional<ItemStack> removed = DartPouchItem.removeOne(pouchStack);

                    if (removed.isPresent()) {
                        ItemStack dart = removed.get();
                        dartStack.set(dart.copy()); // isolate the fired dart

//                        // only remove is player is not creative
//                        if (!(entity instanceof Player p && p.getAbilities().instabuild)) {
//                            dart.shrink(1); // safer than setCount(getCount() - 1)
//                        }

                        // re-add the updated dart stack back to the pouch
                        DartPouchItem.add(pouchStack, dart);

                        dartFromInventoryPouch.set(true);

                        player.getInventory().setChanged();
                        pouchIndex.set(i);
                        break;
                    }
                }
            }
        }

        // attempt to get dart from pouch in trinkets/curios slot
        if (dartStack.get().isEmpty() && Services.PLATFORM.additionalSlotToCheck()) {
            dartStack.set(Services.PLATFORM.getDartFromAdditionalSlot(entity));
            dartFromModSlotPouch = !dartStack.get().isEmpty();
        }

        boolean entitySpawnsDart = !dartStack.get().isEmpty() || entityIsCreativePlayer;
        
        if (entitySpawnsDart && fired.computeIfAbsent(entityUUID, uuid -> 0) < maxShots && (time.computeIfAbsent(entityUUID, uuid -> 0) == 0 || time.computeIfAbsent(entityUUID, uuid -> 0) % (timeToShoot) == 0)) {

            level.playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.CAKE_ADD_CANDLE, SoundSource.NEUTRAL, 1.0F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));

            // fire the dart and handle items on the server
            if (!level.isClientSide) {

                // create a dart and fire it from the entity

                DartItem dartItem = (DartItem) (dartStack.get().getItem() instanceof DartItem ? dartStack.get().getItem() : ModItems.DART);
                Dart dart = dartItem.createDart(level, dartStack.get(), entity);
                dart.pickup = AbstractArrow.Pickup.DISALLOWED;
                // dart.setEffectsFromItem(dartStack);

                int inaccuracy = 15 - (5 * EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.FOCUSED_BREATH, entity.getItemInHand(entity.getUsedItemHand())));
                dart.shootFromRotation(entity, entity.getXRot(), entity.getYRot(), 0.0F, 1.5F, (float) inaccuracy);
                // if (level.addFreshEntity(dart)) fired += 1;
                if (level.addFreshEntity(dart)) fired.put(entityUUID, fired.getOrDefault(entityUUID, 0) + 1);

                // damage the blowgun and remove a dart from the entity
                if (!entityIsCreativePlayer) {
                    blowgunStack.hurtAndBreak(1, entity, (self) -> self.broadcastBreakEvent(entity.getUsedItemHand()));

                    if (!dartFromInventoryPouch.get() && !dartFromModSlotPouch) {
                        ItemStack overwrite = dartStack.get();
                        overwrite.shrink(1);
                        dartStack.set(overwrite);
                        if (dartStack.get().isEmpty() && entityIsPlayer) {
                            ((Player) entity).getInventory().removeItem(dartStack.get());
                        }
                    }
                    else if (dartFromInventoryPouch.get() && entity instanceof Player player) {

                        if (pouchIndex.get() == -1) {
                            for (int i = 0; i < player.getInventory().items.size(); i++) {
                                ItemStack pouchStack = player.getInventory().items.get(i);

                                if (pouchStack.getItem() instanceof DartPouchItem) {
                                    Optional<ItemStack> removed = DartPouchItem.removeOne(pouchStack);

                                    if (removed.isPresent()) {
                                        ItemStack dartStackX = removed.get();
                                        dartStack.set(dartStackX.copy()); // isolate the fired dart

                                        // only remove is player is not creative
                                        if (!(entity instanceof Player p && p.getAbilities().instabuild)) {
                                            dartStackX.shrink(1); // safer than setCount(getCount() - 1)
                                        }

                                        // re-add the updated dart stack back to the pouch
                                        DartPouchItem.add(pouchStack, dartStackX);

                                        player.getInventory().setChanged();
                                        break;
                                    }
                                }
                            }
                        }

                        else {
                            ItemStack pouchStack = player.getInventory().items.get(pouchIndex.get());

                            if (pouchStack.getItem() instanceof DartPouchItem) {
                                Optional<ItemStack> removed = DartPouchItem.removeOne(pouchStack);

                                if (removed.isPresent()) {
                                    ItemStack dartStackX = removed.get();
                                    dartStack.set(dartStackX.copy()); // isolate the fired dart

                                    // only remove is player is not creative
                                    if (!(entity instanceof Player p && p.getAbilities().instabuild)) {
                                        dartStackX.shrink(1); // safer than setCount(getCount() - 1)
                                    }

                                    // re-add the updated dart stack back to the pouch
                                    DartPouchItem.add(pouchStack, dartStackX);

                                    player.getInventory().setChanged();
                                }
                            }
                        }

                    }
                    else if (dartFromModSlotPouch) {
                        Services.PLATFORM.removeDartFromAdditionalSlot(entity);
                    }
                }
            }

            if (fired.get(entityUUID) >= maxShots || time.get(entityUUID) >= this.getUseDuration(blowgunStack)) {
                entity.stopUsingItem();
                if (entityIsPlayer) ((Player) entity).getCooldowns().addCooldown(this, 15);
                fired.put(entityUUID, 0);
                time.put(entityUUID, 0);
            }
        }

        time.put(entityUUID, time.get(entityUUID) + 1);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeCharged) {
        super.releaseUsing(stack, level, entity, timeCharged);
        time.remove(entity.getUUID());
        fired.remove(entity.getUUID());
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
