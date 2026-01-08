package de.yolacraft.worldswap;

import net.fabricmc.api.ModInitializer;

public class WorldSwap implements ModInitializer {

    public static ConfigClass config;

    @Override
    public void onInitialize() {
        config = new ConfigClass();
    }
}



