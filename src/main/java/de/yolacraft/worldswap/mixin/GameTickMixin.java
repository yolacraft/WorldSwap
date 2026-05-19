package de.yolacraft.worldswap.mixin;

import de.yolacraft.worldswap.AutoClickCreateWorldScreen;
import de.yolacraft.worldswap.PlayerBackup;
import de.yolacraft.worldswap.RunState;
import me.contaria.speedrunapi.util.TextUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.SaveLevelScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mixin(MinecraftClient.class)
public class GameTickMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {

        MinecraftClient client = MinecraftClient.getInstance();

        if (client.world == null){if(RunState.fixlan){
            RunState.fixlan = false;
            client.openScreen(new AutoClickCreateWorldScreen(null));
        }
            return;
        }

        if(RunState.rem-5 == 0){
            if(RunState.coop && MinecraftClient.getInstance().getServer() != null && client.player != null) {
                MinecraftClient.getInstance().getServer().openToLan(GameMode.SURVIVAL, false, RunState.coopport);
                client.player.sendMessage(Text.method_30163("World opened to LAN on Port " + RunState.coopport + " (CO-OP Mode)"), false);
            }
        }

        ServerPlayerEntity serverPlayer = null;

        if (client.player != null && client.getServer() != null) {
            serverPlayer = client.getServer()
                    .getPlayerManager()
                    .getPlayer(client.player.getUuid());
        }

        if (serverPlayer == null)
            return;

        if (RunState.restore) {
            System.out.println("RESTORE");

            if (RunState.playerBackup != null && client.getServer() != null) {
                // Führe das Restore auf dem Server-Thread aus, um ConcurrentModificationException zu vermeiden
                final ServerPlayerEntity player = serverPlayer;
                final PlayerBackup backup = RunState.playerBackup;
                client.getServer().execute(() -> {
                    PlayerBackup.restore(player, backup);
                });
                RunState.playerBackup = null;
                RunState.restore = false;
            }
        }

        if(client.isPaused() && RunState.isIGT){
            return;
        }

        boolean inCredits = client.currentScreen instanceof net.minecraft.client.gui.screen.CreditsScreen;

        if(inCredits) {
            RunState.done = true;
        }

        if(!RunState.done){
            RunState.reCalcTimer(RunState.playtime += 1);
        }

        if (RunState.rem == 0) {

            RunState.continueNXT = true;
            RunState.restore = true;
            RunState.fixlan = true;
            RunState.done = false;

            System.out.println("SAVE");
            RunState.playerBackup = new PlayerBackup(serverPlayer);

            // Speichere playtime vor dem disconnect
            if (client.getServer() != null) {
                try {
                    java.nio.file.Path worldDir = client.getServer().getSavePath(net.minecraft.util.WorldSavePath.ROOT);
                    java.nio.file.Path playtimeFile = worldDir.resolve("playtime.txt");
                    String content = String.valueOf(RunState.playtime);
                    java.nio.file.Files.write(playtimeFile, content.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                    System.out.println("[WorldSwap] Playtime gespeichert: " + RunState.playtime);
                } catch (java.io.IOException e) {
                    System.err.println("[WorldSwap] konnte playtime.txt nicht speichern: " + e.getMessage());
                }
            }
            if (client.getServer() != null && client.player != null) {

                UUID hostUUID = client.player.getUuid();

                List<ServerPlayerEntity> players = new ArrayList<>(client.getServer().getPlayerManager().getPlayerList());
                for (ServerPlayerEntity player : players) {
                    if (!player.getUuid().equals(hostUUID)) {
                        player.networkHandler.disconnect(Text.method_30163("worldswap.reset"));
                    }
                }

            }

            if(FabricLoader.getInstance().isModLoaded("atum") && isAtumRunning() && RunState.AtumMode) {
                stopAtumIfPresent();
            }
            client.world.disconnect();

        }
    }

    private static void stopAtumIfPresent() {
        if (!FabricLoader.getInstance().isModLoaded("atum")) return;

        try {
            Class<?> atumClass = Class.forName("me.voidxwalker.autoreset.Atum");
            Method stopRunning = atumClass.getMethod("stopRunning");
            stopRunning.invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isAtumRunning() {
        if (!FabricLoader.getInstance().isModLoaded("atum")) {
            return false;
        }

        try {
            Class<?> atumClass = Class.forName("me.voidxwalker.autoreset.Atum");

            return (boolean) atumClass.getMethod("isRunning").invoke(null);

        } catch (ClassNotFoundException e) {
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
