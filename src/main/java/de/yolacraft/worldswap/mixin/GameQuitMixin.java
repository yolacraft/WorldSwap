package de.yolacraft.worldswap.mixin;

import de.yolacraft.worldswap.RunState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.WorldSavePath;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Mixin(MinecraftClient.class)
public class GameQuitMixin {

    @Inject(method = "disconnect*", at = @At("HEAD"))
    private void disconnect(CallbackInfo ci) {
        MinecraftClient client = (MinecraftClient) (Object) this;

        if (client.getServer() != null) {
            try {
                Path worldDir = client.getServer().getSavePath(WorldSavePath.ROOT);
                Path playtimeFile = worldDir.resolve("playtime.txt");

                String content = String.valueOf(RunState.playtime);
                Files.write(playtimeFile, content.getBytes(StandardCharsets.UTF_8));

            } catch (IOException e) {
                System.err.println("[WorldSwap] konnte playtime.txt nicht speichern: " + e.getMessage());
            }
        }
    }
}
