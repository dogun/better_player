package com.my66.better_player.packet;

import net.minecraft.network.FriendlyByteBuf;

import java.nio.charset.StandardCharsets;

public class ResponseDataPacket {
    public final String data;
    public ResponseDataPacket(String data) { this.data = data; }

    public static void encode(ResponseDataPacket pkt, FriendlyByteBuf buf) {
        buf.writeByteArray(pkt.data.getBytes(StandardCharsets.UTF_8));
    }

    public static ResponseDataPacket decode(FriendlyByteBuf buf) {
        return new ResponseDataPacket(new String(buf.readByteArray(128), StandardCharsets.UTF_8));
    }
}
