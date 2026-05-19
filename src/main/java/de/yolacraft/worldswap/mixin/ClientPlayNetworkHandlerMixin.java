package de.yolacraft.worldswap.mixin;

import de.yolacraft.worldswap.RunState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    private static final Identifier TIMER_SYNC = new Identifier("worldswap", "timer_sync");

    @Inject(method = "onCustomPayload(Lnet/minecraft/network/packet/s2c/play/CustomPayloadS2CPacket;)V",
            at = @At("HEAD"), cancellable = true)
    private void onCustomPayload(CustomPayloadS2CPacket packet, CallbackInfo ci) {
        if (packet.getChannel().equals(TIMER_SYNC)) {
            PacketByteBuf data = packet.getData();

            RunState.displayText = data.readString(32767);
            RunState.displayText2 = data.readString(32767);

            ci.cancel();
        }
    }
}
