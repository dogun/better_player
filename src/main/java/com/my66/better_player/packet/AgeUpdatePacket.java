package com.my66.better_player.packet;

import net.minecraft.network.FriendlyByteBuf;

public class AgeUpdatePacket {
    public final int age;
    public AgeUpdatePacket(int age) {
        this.age = age;
    }

    public static void encode(AgeUpdatePacket pkt, FriendlyByteBuf buf) {
        buf.writeInt(pkt.age);
    }

    public static AgeUpdatePacket decode(FriendlyByteBuf buf) {
        return new AgeUpdatePacket(buf.readInt());
    }
}
