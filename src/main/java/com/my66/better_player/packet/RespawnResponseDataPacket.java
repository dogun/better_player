package com.my66.better_player.packet;

import net.minecraft.network.FriendlyByteBuf;

import java.nio.charset.StandardCharsets;

public class RespawnResponseDataPacket {
    public final String data;
    public RespawnResponseDataPacket(String data) { this.data = data; }

    public static void encode(RespawnResponseDataPacket pkt, FriendlyByteBuf buf) {
        buf.writeByteArray(pkt.data.getBytes(StandardCharsets.UTF_8));
    }

    public static RespawnResponseDataPacket decode(FriendlyByteBuf buf) {
        return new RespawnResponseDataPacket(new String(buf.readByteArray(128), StandardCharsets.UTF_8));
    }
}
