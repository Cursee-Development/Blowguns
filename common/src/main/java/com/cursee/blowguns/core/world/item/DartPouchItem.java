package com.cursee.blowguns.core.world.item;

import com.cursee.blowguns.core.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class DartPouchItem extends Item {

    private static final String TAG_ITEMS = "Items";
    public static final int MAX_WEIGHT = 256;
    private static final int POUCH_IN_POUCH_WEIGHT = 4;
    private static final int BAR_COLOR = Mth.color(0.4F, 0.4F, 1.0F);

    public DartPouchItem(Item.Properties properties) {
        super(properties);
    }

    public static float getFullnessDisplay(ItemStack stack) {
        return (float) getContentWeight(stack) / MAX_WEIGHT;
    }

    public boolean overrideStackedOnOther(ItemStack pouchStack, Slot slot, ClickAction action, Player player) {
        if (pouchStack.getCount() == 1 && action == ClickAction.SECONDARY) {
            ItemStack itemstack = slot.getItem();
            if (itemstack.isEmpty()) {
                this.playRemoveOneSound(player);
                removeOne(pouchStack).ifPresent((itemStack) -> add(pouchStack, slot.safeInsert(itemStack)));
            }
            else if (itemstack.getItem().canFitInsideContainerItems()) {
                int i = (MAX_WEIGHT - getContentWeight(pouchStack)) / getWeight(itemstack);
                int j = add(pouchStack, slot.safeTake(itemstack.getCount(), i, player));
                if (j > 0) {
                    this.playInsertSound(player);
                }
            }

            return true;
        }
        else {
            return false;
        }
    }

    public boolean overrideOtherStackedOnMe(ItemStack pouchStack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if (pouchStack.getCount() != 1) {
            return false;
        }
        else if (action == ClickAction.SECONDARY && slot.allowModification(player)) {
            if (other.isEmpty()) {
                removeOne(pouchStack).ifPresent((itemStack) -> {
                    this.playRemoveOneSound(player);
                    access.set(itemStack);
                });
            }
            else {
                int i = add(pouchStack, other);
                if (i > 0) {
                    this.playInsertSound(player);
                    other.shrink(i);
                }
            }

            return true;
        }
        else {
            return false;
        }
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemstack = player.getItemInHand(usedHand);
        if (dropContents(itemstack, player)) {
            this.playDropContentsSound(player);
            player.awardStat(Stats.ITEM_USED.get(this));
            return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
        }
        else {
            return InteractionResultHolder.fail(itemstack);
        }
    }

    public boolean isBarVisible(ItemStack stack) {
        return getContentWeight(stack) > 0;
    }

    public int getBarWidth(ItemStack stack) {
        return Math.min(1 + 12 * getContentWeight(stack) / MAX_WEIGHT, 13);
    }

    public int getBarColor(ItemStack stack) {
        return BAR_COLOR;
    }

    public static int add(ItemStack bundleStack, ItemStack insertedStack) {

        if (!(insertedStack.getItem() instanceof DartItem)) return 0;

        if (!insertedStack.isEmpty() && insertedStack.getItem().canFitInsideContainerItems()) {
            CompoundTag compoundtag = bundleStack.getOrCreateTag();
            if (!compoundtag.contains(TAG_ITEMS)) {
                compoundtag.put(TAG_ITEMS, new ListTag());
            }

            int i = getContentWeight(bundleStack);
            int j = getWeight(insertedStack);
            int k = Math.min(insertedStack.getCount(), (MAX_WEIGHT - i) / j);
            if (k == 0) {
                return 0;
            }
            else {
                ListTag listtag = compoundtag.getList(TAG_ITEMS, Tag.TAG_COMPOUND);
                Optional<CompoundTag> optional = getMatchingItem(insertedStack, listtag);
                if (optional.isPresent()) {
                    CompoundTag compoundtag1 = (CompoundTag)optional.get();
                    ItemStack itemstack = ItemStack.of(compoundtag1);
                    itemstack.grow(k);
                    itemstack.save(compoundtag1);
                    listtag.remove(compoundtag1);
                    listtag.add(0, compoundtag1);
                } else {
                    ItemStack itemstack1 = insertedStack.copyWithCount(k);
                    CompoundTag compoundtag2 = new CompoundTag();
                    itemstack1.save(compoundtag2);
                    listtag.add(0, compoundtag2);
                }

                return k;
            }
        }
        else {
            return 0;
        }
    }

    private static Optional<CompoundTag> getMatchingItem(ItemStack stack, ListTag list) {

        Optional<CompoundTag> optional;

        if (stack.getItem() instanceof DartPouchItem) return Optional.empty();
        else {
            Stream<Tag> tags = list.stream();
            tags = tags.filter(CompoundTag.class::isInstance);
            optional = tags.map(CompoundTag.class::cast).filter(tag -> ItemStack.isSameItemSameTags(ItemStack.of(tag), stack)).findFirst();
        }

        return optional;
    }

    private static int getWeight(ItemStack stack) {
        if (stack.is(ModItems.DART_POUCH)) {
            return POUCH_IN_POUCH_WEIGHT + getContentWeight(stack);
        }
        else {
            if ((stack.is(Items.BEEHIVE) || stack.is(Items.BEE_NEST)) && stack.hasTag()) {
                CompoundTag compoundtag = BlockItem.getBlockEntityData(stack);
                if (compoundtag != null && !compoundtag.getList("Bees", Tag.TAG_COMPOUND).isEmpty()) {
                    return MAX_WEIGHT;
                }
            }

            return MAX_WEIGHT / stack.getMaxStackSize();
        }
    }

    private static int getContentWeight(ItemStack stack) {
        return getContents(stack).mapToInt((itemStack) -> getWeight(itemStack) * itemStack.getCount()).sum();
    }

    public static Optional<ItemStack> removeOne(ItemStack stack) {
        CompoundTag compoundtag = stack.getOrCreateTag();
        if (!compoundtag.contains(TAG_ITEMS)) {
            return Optional.empty();
        }
        else {
            ListTag listtag = compoundtag.getList(TAG_ITEMS, Tag.TAG_COMPOUND);
            if (listtag.isEmpty()) {
                return Optional.empty();
            }
            else {
                int i = 0;
                CompoundTag compoundtag1 = listtag.getCompound(i);
                ItemStack itemstack = ItemStack.of(compoundtag1);
                listtag.remove(i);
                if (listtag.isEmpty()) {
                    stack.removeTagKey(TAG_ITEMS);
                }

                return Optional.of(itemstack);
            }
        }
    }

    private static boolean dropContents(ItemStack stack, Player player) {
        CompoundTag compoundtag = stack.getOrCreateTag();
        if (!compoundtag.contains(TAG_ITEMS)) {
            return false;
        }
        else {
            if (player instanceof ServerPlayer) {
                ListTag listtag = compoundtag.getList(TAG_ITEMS, Tag.TAG_COMPOUND);

                for(int i = 0; i < listtag.size(); ++i) {
                    CompoundTag compoundtag1 = listtag.getCompound(i);
                    ItemStack itemstack = ItemStack.of(compoundtag1);
                    player.drop(itemstack, true);
                }
            }

            stack.removeTagKey(TAG_ITEMS);
            return true;
        }
    }

    private static Stream<ItemStack> getContents(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) return Stream.empty();
        else {
            ListTag list = tag.getList(TAG_ITEMS, Tag.TAG_COMPOUND);
            Stream<Tag> tagStream = list.stream();
            return tagStream.map(CompoundTag.class::cast).map(ItemStack::of);
        }
    }

    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        NonNullList<ItemStack> filledList = NonNullList.create();
        Stream<ItemStack> stackContents = getContents(stack);
        stackContents.forEach(filledList::add);
        return Optional.of(new BundleTooltip(filledList, getContentWeight(stack)));
    }

    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        tooltipComponents.add(Component.translatable("item.minecraft.bundle.fullness", getContentWeight(stack), MAX_WEIGHT).withStyle(ChatFormatting.GRAY));
    }

    public void onDestroyed(ItemEntity itemEntity) {
        ItemUtils.onContainerDestroyed(itemEntity, getContents(itemEntity.getItem()));
    }

    private void playRemoveOneSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    private void playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    private void playDropContentsSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_DROP_CONTENTS, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }
}
