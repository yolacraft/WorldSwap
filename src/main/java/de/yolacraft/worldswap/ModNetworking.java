package de.yolacraft.worldswap;

import de.yolacraft.worldswap.RunState;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.server.network.ServerPlayerEntity;

public class ModNetworking {

    public static final Identifier TIMER_SYNC = new Identifier("worldswap", "timer_sync");

    public static void sendTimerData(ServerPlayerEntity player, String str1, String str2) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeString(str1);
        buf.writeString(str2);

        CustomPayloadS2CPacket packet = new CustomPayloadS2CPacket(TIMER_SYNC, buf);
        player.networkHandler.sendPacket(packet);
    }
}
