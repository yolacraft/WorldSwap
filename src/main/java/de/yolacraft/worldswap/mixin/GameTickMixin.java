package de.yolacraft.worldswap.mixin;

import de.yolacraft.worldswap.AutoClickCreateWorldScreen;
import de.yolacraft.worldswap.PlayerBackup;
import de.yolacraft.worldswap.RunState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class GameTickMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {

        MinecraftClient client = MinecraftClient.getInstance();

        if (client.world == null)
            return;

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
            RunState.done = false; // Starte Timer wieder beim World-Swap

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

            client.openScreen(new AutoClickCreateWorldScreen((Screen) null));

            client.world.disconnect();
        }
    }
}
