package com.cursee.blowguns;

import com.cursee.blowguns.client.renderer.entity.DartRenderer;
import com.cursee.blowguns.core.registry.ModEntities;
import com.cursee.blowguns.platform.Services;

public class BlowgunsClient {

    public static void init() {
        Services.PLATFORM.registerTippedDartItemColoring();
    }

    public static void setup() {
        Services.PLATFORM.registerEntityRenderer(ModEntities.DART, DartRenderer::new);
    }
}
