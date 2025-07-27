package com.my66.better_player.packet;

import net.minecraft.network.FriendlyByteBuf;

public class ThirstResponsePacket {
    public final int thirst;
    public ThirstResponsePacket(int thirst) {
        this.thirst = thirst;
    }

    public static void encode(ThirstResponsePacket pkt, FriendlyByteBuf buf) {
        buf.writeInt(pkt.thirst);
    }

    public static ThirstResponsePacket decode(FriendlyByteBuf buf) {
        return new ThirstResponsePacket(buf.readInt());
    }
}
