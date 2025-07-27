package com.my66.better_player.server.packet;

import com.my66.better_player.packet.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.DEDICATED_SERVER)
public class ServerPacketHandler {
    public static void register() {
        ModPacketHandler.INSTANCE.registerMessage(
                ModPacketHandler.index++, ResponseDataPacket.class,
                ResponseDataPacket::encode,
                ResponseDataPacket::decode,
                (pkt, ctx) -> ctx.get().setPacketHandled(true)
        );
        ModPacketHandler.INSTANCE.registerMessage(
                ModPacketHandler.index++, RespawnResponseDataPacket.class,
                RespawnResponseDataPacket::encode,
                RespawnResponseDataPacket::decode,
                (pkt, ctx) -> ctx.get().setPacketHandled(true)
        );
        ModPacketHandler.INSTANCE.registerMessage(
                ModPacketHandler.index++, AgeUpdatePacket.class,
                AgeUpdatePacket::encode,
                AgeUpdatePacket::decode,
                (pkt, ctx) -> ctx.get().setPacketHandled(true)
        );
        ModPacketHandler.INSTANCE.registerMessage(
                ModPacketHandler.index++, ConfigResponsePacket.class,
                ConfigResponsePacket::encode,
                ConfigResponsePacket::decode,
                (pkt, ctx) -> ctx.get().setPacketHandled(true)
        );
        ModPacketHandler.INSTANCE.registerMessage(
                ModPacketHandler.index++, ThirstResponsePacket.class,
                ThirstResponsePacket::encode,
                ThirstResponsePacket::decode,
                (pkt, ctx) -> ctx.get().setPacketHandled(true)
        );
    }
}

