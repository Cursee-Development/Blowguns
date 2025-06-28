package com.cursee.blowguns.core.registry;

import com.cursee.blowguns.Blowguns;
import com.cursee.blowguns.core.world.item.BlowgunItem;
import com.cursee.blowguns.core.world.item.DartItem;
import com.cursee.blowguns.core.world.item.DartPouchItem;
import com.cursee.blowguns.core.world.item.TippedDartItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.function.BiConsumer;

public class ModItems {

    public static final Item BLOWGUN = new BlowgunItem(new Item.Properties().defaultDurability(136));

    public static final Item DART_POUCH = new DartPouchItem(new Item.Properties().stacksTo(1));

    public static final Item DART = new DartItem(new Item.Properties().stacksTo(64));
    public static final Item TIPPED_DART = new TippedDartItem(new Item.Properties().stacksTo(64));

    public static void register(BiConsumer<Item, ResourceLocation> consumer) {
        consumer.accept(BLOWGUN, Blowguns.identifier("blowgun"));
        consumer.accept(DART_POUCH, Blowguns.identifier("dart_pouch"));
        consumer.accept(DART, Blowguns.identifier("dart"));
        consumer.accept(TIPPED_DART, Blowguns.identifier("tipped_dart"));
    }
}
