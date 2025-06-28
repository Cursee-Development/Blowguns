package com.cursee.blowguns.core.world.entity.projectile;

import com.cursee.blowguns.core.registry.ModEntities;
import com.cursee.blowguns.core.registry.ModItems;
import com.google.common.collect.Sets;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.Set;

public class Dart extends AbstractArrow {

    private static final int EXPOSED_POTION_DECAY_TIME = 600;
    private static final int NO_EFFECT_COLOR = -1;
    private static final EntityDataAccessor<Integer> ID_EFFECT_COLOR = SynchedEntityData.defineId(Dart.class, EntityDataSerializers.INT);
    private static final byte EVENT_POTION_PUFF = 0;

    private Potion potion;
    private final Set<MobEffectInstance> effects;
    private boolean fixedColor;

    public Dart(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
        this.potion = Potions.EMPTY;
        this.effects = Sets.newHashSet();
    }

    public Dart(double x, double y, double z, Level level) {
        super(ModEntities.DART, x, y, z, level);
        this.potion = Potions.EMPTY;
        this.effects = Sets.newHashSet();
    }

    public Dart(LivingEntity livingEntity, Level level) {
        super(ModEntities.DART, livingEntity, level);
        this.potion = Potions.EMPTY;
        this.effects = Sets.newHashSet();
    }

    public Dart(Level level, LivingEntity livingEntity) {
        this(livingEntity, level);
    }

    @Override
    protected ItemStack getPickupItem() {
        if (this.effects.isEmpty() && this.potion == Potions.EMPTY) {
            return new ItemStack(ModItems.DART);
        }
        else {
            ItemStack itemstack = new ItemStack(ModItems.TIPPED_DART);
            PotionUtils.setPotion(itemstack, this.potion);
            PotionUtils.setCustomEffects(itemstack, this.effects);
            if (this.fixedColor) {
                itemstack.getOrCreateTag().putInt("CustomPotionColor", this.getColor());
            }

            return itemstack;
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ID_EFFECT_COLOR, NO_EFFECT_COLOR);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.potion != Potions.EMPTY) {
            compound.putString("Potion", BuiltInRegistries.POTION.getKey(this.potion).toString());
        }

        if (this.fixedColor) {
            compound.putInt("Color", this.getColor());
        }

        if (!this.effects.isEmpty()) {
            ListTag listtag = new ListTag();

            for(MobEffectInstance mobeffectinstance : this.effects) {
                listtag.add(mobeffectinstance.save(new CompoundTag()));
            }

            compound.put("CustomPotionEffects", listtag);
        }

    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Potion", 8)) {
            this.potion = PotionUtils.getPotion(compound);
        }

        for(MobEffectInstance mobeffectinstance : PotionUtils.getCustomEffects(compound)) {
            this.addEffect(mobeffectinstance);
        }

        if (compound.contains("Color", Tag.TAG_ANY_NUMERIC)) {
            this.setFixedColor(compound.getInt("Color"));
        } else {
            this.updateColor();
        }
    }

    @Override
    protected void doPostHurtEffects(LivingEntity living) {
        super.doPostHurtEffects(living);
        Entity entity = this.getEffectSource();

        for(MobEffectInstance mobeffectinstance : this.potion.getEffects()) {
            living.addEffect(new MobEffectInstance(mobeffectinstance.getEffect(), Math.max(mobeffectinstance.mapDuration((p_268168_) -> p_268168_ / 8), 1), mobeffectinstance.getAmplifier(), mobeffectinstance.isAmbient(), mobeffectinstance.isVisible()), entity);
        }

        if (!this.effects.isEmpty()) {
            for(MobEffectInstance mobeffectinstance1 : this.effects) {
                living.addEffect(mobeffectinstance1, entity);
            }
        }

    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            if (this.inGround) {
                if (this.inGroundTime % 5 == EVENT_POTION_PUFF) {
                    this.makeParticle(1);
                }
            }
            else {
                this.makeParticle(2);
            }
        }
        else if (this.inGround && this.inGroundTime != 0 && !this.effects.isEmpty() && this.inGroundTime >= EXPOSED_POTION_DECAY_TIME) {
            this.level().broadcastEntityEvent(this, EVENT_POTION_PUFF);
            this.potion = Potions.EMPTY;
            this.effects.clear();
            this.entityData.set(ID_EFFECT_COLOR, NO_EFFECT_COLOR);
        }

        if (this.inGround && this.inGroundTime >= 100) this.discard(); // discard after 5 seconds (5 * 20 ticks per second)
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == EVENT_POTION_PUFF) {
            int i = this.getColor();
            if (i != NO_EFFECT_COLOR) {
                double d0 = (double)(i >> 16 & 255) / (double)255.0F;
                double d1 = (double)(i >> 8 & 255) / (double)255.0F;
                double d2 = (double)(i >> 0 & 255) / (double)255.0F;

                for(int j = 0; j < 20; ++j) {
                    this.level().addParticle(ParticleTypes.ENTITY_EFFECT, this.getRandomX((double)0.5F), this.getRandomY(), this.getRandomZ((double)0.5F), d0, d1, d2);
                }
            }
        }
        else {
            super.handleEntityEvent(id);
        }

    }

    private void makeParticle(int particleAmount) {
        int color = this.getColor();
        if (color != NO_EFFECT_COLOR && particleAmount > 0) {
            double d0 = (double)(color >> 16 & 255) / (double)255.0F;
            double d1 = (double)(color >> 8 & 255) / (double)255.0F;
            double d2 = (double)(color >> 0 & 255) / (double)255.0F;

            for(int j = 0; j < particleAmount; ++j) {
                this.level().addParticle(ParticleTypes.ENTITY_EFFECT, this.getRandomX((double)0.5F), this.getRandomY(), this.getRandomZ((double)0.5F), d0, d1, d2);
            }
        }
    }

    public int getColor() {
        return this.entityData.get(ID_EFFECT_COLOR);
    }

    public static int getCustomColor(ItemStack stack) {
        CompoundTag compoundtag = stack.getTag();
        return compoundtag != null && compoundtag.contains("CustomPotionColor", Tag.TAG_ANY_NUMERIC) ? compoundtag.getInt("CustomPotionColor") : NO_EFFECT_COLOR;
    }

    private void updateColor() {
        this.fixedColor = false;
        if (this.potion == Potions.EMPTY && this.effects.isEmpty()) {
            this.entityData.set(ID_EFFECT_COLOR, NO_EFFECT_COLOR);
        }
        else {
            this.entityData.set(ID_EFFECT_COLOR, PotionUtils.getColor(PotionUtils.getAllEffects(this.potion, this.effects)));
        }
    }

    private void setFixedColor(int fixedColor) {
        this.fixedColor = true;
        this.entityData.set(ID_EFFECT_COLOR, fixedColor);
    }

    public void addEffect(MobEffectInstance effectInstance) {
        this.effects.add(effectInstance);
        this.getEntityData().set(ID_EFFECT_COLOR, PotionUtils.getColor(PotionUtils.getAllEffects(this.potion, this.effects)));
    }

    public void setEffectsFromItem(ItemStack stack) {
        if (stack.is(ModItems.TIPPED_DART)) {
            this.potion = PotionUtils.getPotion(stack);
            Collection<MobEffectInstance> collection = PotionUtils.getCustomEffects(stack);
            if (!collection.isEmpty()) {
                for(MobEffectInstance mobeffectinstance : collection) {
                    this.effects.add(new MobEffectInstance(mobeffectinstance));
                }
            }

            int i = getCustomColor(stack);
            if (i == NO_EFFECT_COLOR) {
                this.updateColor();
            } else {
                this.setFixedColor(i);
            }
        }
        else if (stack.is(ModItems.DART)) {
            this.potion = Potions.EMPTY;
            this.effects.clear();
            this.entityData.set(ID_EFFECT_COLOR, NO_EFFECT_COLOR);
        }

    }
}
