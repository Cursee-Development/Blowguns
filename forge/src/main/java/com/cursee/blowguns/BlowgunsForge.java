package com.cursee.blowguns;

import com.cursee.blowguns.core.registry.ModRegistryForge;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(Constants.MOD_ID)
public class BlowgunsForge {

    public static IEventBus EVENT_BUS;

    public BlowgunsForge(FMLJavaModLoadingContext context) {
        EVENT_BUS = context.getModEventBus();
        Blowguns.init();
        ModRegistryForge.register(EVENT_BUS);
        if (FMLEnvironment.dist == Dist.CLIENT) new BlowgunsClientForge(EVENT_BUS);
    }

    @SuppressWarnings("removal")
    public BlowgunsForge() {
        this(FMLJavaModLoadingContext.get());
    }
}