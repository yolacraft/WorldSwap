package de.yolacraft.worldswap;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class LANServerInit {
    public static void register() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            ModServer.registerTick();
        });
    }
}
