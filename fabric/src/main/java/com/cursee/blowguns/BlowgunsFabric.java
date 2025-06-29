package com.cursee.blowguns;

import com.cursee.blowguns.core.registry.ModRegistryFabric;
import com.cursee.blowguns.core.compat.FabricTrinketsHelper;
import com.cursee.blowguns.platform.Services;
import net.fabricmc.api.ModInitializer;

public class BlowgunsFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        Blowguns.init();
        ModRegistryFabric.register();

        if (Services.PLATFORM.additionalSlotToCheck()) FabricTrinketsHelper.registerTrinkets();
    }
}
