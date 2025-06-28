package com.cursee.blowguns.core.world.item.crafting;

import com.cursee.blowguns.core.registry.ModItems;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class TippedDartRecipe extends CustomRecipe {

    public TippedDartRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    public boolean matches(CraftingContainer inv, Level level) {
        if (inv.getWidth() == 3 && inv.getHeight() == 3) {
            for(int i = 0; i < inv.getWidth(); ++i) {
                for(int j = 0; j < inv.getHeight(); ++j) {
                    ItemStack itemstack = inv.getItem(i + j * inv.getWidth());
                    if (itemstack.isEmpty()) {
                        return false;
                    }

                    if (i == 1 && j == 1) {
                        if (!itemstack.is(Items.LINGERING_POTION)) {
                            return false;
                        }
                    } else if (!itemstack.is(ModItems.DART)) {
                        return false;
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        ItemStack itemstack = container.getItem(1 + container.getWidth());
        if (!itemstack.is(Items.LINGERING_POTION)) {
            return ItemStack.EMPTY;
        } else {
            ItemStack itemstack1 = new ItemStack(ModItems.TIPPED_DART, 8);
            PotionUtils.setPotion(itemstack1, PotionUtils.getPotion(itemstack));
            PotionUtils.setCustomEffects(itemstack1, PotionUtils.getCustomEffects(itemstack));
            return itemstack1;
        }
    }

    public boolean canCraftInDimensions(int width, int height) {
        return width >= 2 && height >= 2;
    }

    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.TIPPED_ARROW;
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeType.CRAFTING;
    }

    //    // $FF: synthetic method
//    // $FF: bridge method
//    public ItemStack assemble(Container var1, RegistryAccess var2) {
//        return this.assemble((CraftingContainer)var1, var2);
//    }
//
//    // $FF: synthetic method
//    // $FF: bridge method
//    public boolean matches(Container var1, Level var2) {
//        return this.matches((CraftingContainer)var1, var2);
//    }
}

