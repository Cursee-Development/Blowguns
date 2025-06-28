package com.cursee.blowguns.core.tag;

import com.cursee.blowguns.Blowguns;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModItemTags {

    public static final TagKey<Item> DARTS = bind("darts");

    private static TagKey<Item> bind(String name) {
        return TagKey.create(Registries.ITEM, Blowguns.identifier(name));
    }
}
