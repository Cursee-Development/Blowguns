package com.cursee.blowguns;

import com.cursee.blowguns.core.registry.ModRegistryFabric;
import net.fabricmc.api.ModInitializer;

public class BlowgunsFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        Blowguns.init();
        ModRegistryFabric.register();
    }
}
