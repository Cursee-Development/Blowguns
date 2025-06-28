package com.cursee.blowguns;

import com.cursee.blowguns.platform.Services;
import net.minecraft.resources.ResourceLocation;

public class Blowguns {

    public static void init() {
        Services.PLATFORM.registerTippedDartRecipeSerializer();
    }

    public static ResourceLocation identifier(String path) {
        return new ResourceLocation(Constants.MOD_ID, path);
    }
}