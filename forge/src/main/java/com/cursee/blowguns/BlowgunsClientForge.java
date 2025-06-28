package com.cursee.blowguns;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.function.Consumer;

public class BlowgunsClientForge {

    public BlowgunsClientForge(final IEventBus modEventBus) {

        BlowgunsClient.init();

        modEventBus.addListener((Consumer<FMLClientSetupEvent>) event -> {
            BlowgunsClient.setup();
        });
    }
}
