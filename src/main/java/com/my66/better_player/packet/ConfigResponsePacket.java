package com.my66.better_player.packet;

import net.minecraft.network.FriendlyByteBuf;

import java.nio.charset.StandardCharsets;

public class ConfigResponsePacket {
    public final String data;
    public final boolean gui;
    public ConfigResponsePacket(boolean gui, String data) {
        this.data = data;
        this.gui = gui;
    }

    public static void encode(ConfigResponsePacket pkt, FriendlyByteBuf buf) {
        buf.writeBoolean(pkt.gui);
        buf.writeByteArray(pkt.data.getBytes(StandardCharsets.UTF_8));
    }

    public static ConfigResponsePacket decode(FriendlyByteBuf buf) {
        return new ConfigResponsePacket(buf.readBoolean(), new String(buf.readByteArray(128), StandardCharsets.UTF_8));
    }
}
