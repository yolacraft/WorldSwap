package de.yolacraft.worldswap.mixin;

import de.yolacraft.worldswap.RunState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.WorldSavePath; // Import hinzugefügt
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Imports für Dateioperationen
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Mixin(MinecraftClient.class)
public class GameLoadMixin {

    private ClientWorld previousWorld = null;

    @Inject(method = "joinWorld", at = @At("HEAD"))
    private void beforeJoinWorld(ClientWorld world, CallbackInfo ci) {
        previousWorld = MinecraftClient.getInstance().world;
    }

    @Inject(method = "joinWorld", at = @At("TAIL"))
    private void afterJoinWorld(ClientWorld world, CallbackInfo ci) {

        if (previousWorld == null) {



            MinecraftClient client = MinecraftClient.getInstance();

            if (client.getServer() != null) {
                try {
                    Path worldDir = client.getServer().getSavePath(WorldSavePath.ROOT);
                    Path playtimeFile = worldDir.resolve("playtime.txt");

                    if (Files.exists(playtimeFile) && Files.isReadable(playtimeFile)) {
                        String content = new String(Files.readAllBytes(playtimeFile), StandardCharsets.UTF_8).trim();
                        try {
                            RunState.playtime = Math.toIntExact(Long.parseLong(content));
                        } catch (NumberFormatException e) {
                            System.err.println("[WorldSwap] playtime.txt ist korrumpiert. Setze auf 0.");
                            if(RunState.continueNXT){
                                RunState.continueNXT = false;
                                return;
                            }
                            RunState.playtime = 0;
                        }
                    } else {
                        if(RunState.continueNXT){
                            RunState.continueNXT = false;
                            return;
                        }
                        RunState.playtime = 0;
                    }
                } catch (IOException e) {
                    if(RunState.continueNXT){
                        RunState.continueNXT = false;
                        return;
                    }
                    System.err.println("[WorldSwap] konnte playtime.txt nicht lesen: " + e.getMessage());
                    RunState.playtime = 0;

                }
            } else {
                RunState.playtime = 0;



            }
        }
    }
}