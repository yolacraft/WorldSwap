package de.yolacraft.worldswap;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ModServer {

    private static int tickCounter = 0;

    // Merkt sich Spieler, die im letzten Tick existierten
    private static final Map<UUID, ServerPlayerEntity> lastSeenPlayers = new HashMap<>();

    public static void registerTick() {
        ServerTickEvents.END_SERVER_TICK.register(ModServer::onServerTick);
    }

    private static void onServerTick(MinecraftServer server) {
        tickCounter++;
        if (tickCounter % 10 != 0) return;

        Set<UUID> currentPlayers = new HashSet<>();

        // === AKTUELLE SPIELER ===
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            UUID uuid = player.getUuid();
            currentPlayers.add(uuid);

            // JOIN
            if (!lastSeenPlayers.containsKey(uuid)) {
                onPlayerJoin(server, player);
            }

            // Spieler IMMER merken (wichtig!)
            lastSeenPlayers.put(uuid, player);

            // Dein bestehender Tick-Code
            if (!server.isHost(player.getGameProfile())) {
                ModNetworking.sendTimerData(
                        player,
                        RunState.displayText,
                        RunState.displayText2
                );
            }
        }

        // === LEAVE ===
        Set<UUID> leftPlayers = new HashSet<>(lastSeenPlayers.keySet());
        leftPlayers.removeAll(currentPlayers);

        for (UUID uuid : leftPlayers) {
            ServerPlayerEntity player = lastSeenPlayers.get(uuid);
            onPlayerLeave(player);
            lastSeenPlayers.remove(uuid);
        }
    }

    // =======================
    // JOIN
    // =======================
    private static void onPlayerJoin(MinecraftServer server, ServerPlayerEntity player) {
        if (server.isHost(player.getGameProfile())) return;

        String uuid = player.getUuid().toString();

        // Backup wiederherstellen
        if (RunState.coopUserBackup.containsKey(uuid)) {
            PlayerBackup backup = RunState.coopUserBackup.get(uuid);
            PlayerBackup.restore(player, backup);
        }
    }

    // =======================
    // LEAVE
    // =======================
    private static void onPlayerLeave(ServerPlayerEntity player) {
        if (player == null) return;

        String uuid = player.getUuid().toString();

        // Backup erstellen
        RunState.coopUserBackup.put(
                uuid,
                new PlayerBackup(player)
        );
    }
}
