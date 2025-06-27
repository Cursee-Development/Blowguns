package com.cursee.blowguns;

import net.minecraft.resources.ResourceLocation;

public class Blowguns {

    public static void init() {}

    public static ResourceLocation identifier(String path) {
        return new ResourceLocation(Constants.MOD_ID, path);
    }
}