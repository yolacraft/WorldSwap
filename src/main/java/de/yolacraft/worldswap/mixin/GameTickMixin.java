package de.yolacraft.worldswap.mixin;

import de.yolacraft.worldswap.AutoClickCreateWorldScreen;
import de.yolacraft.worldswap.PlayerBackup;
import de.yolacraft.worldswap.RunState;
import de.yolacraft.worldswap.WorldSwap;
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

            if (RunState.playerBackup != null) {
                PlayerBackup.restore(serverPlayer, RunState.playerBackup);
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

            System.out.println("SAVE");
            RunState.playerBackup = new PlayerBackup(serverPlayer);

            client.openScreen(new AutoClickCreateWorldScreen((Screen) null));

            client.world.disconnect();
        }
    }
}
