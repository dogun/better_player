package com.my66.better_player.packet;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModPacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath("better_player", "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    public static int index = 0;

    public static void register() {
        INSTANCE.registerMessage(
                index++, SaveDataPacket.class,
                SaveDataPacket::encode,
                SaveDataPacket::decode,
                SaveDataPacket::handle
        );
        INSTANCE.registerMessage(
                index++, RequestDataPacket.class,
                RequestDataPacket::encode,
                RequestDataPacket::decode,
                RequestDataPacket::handle
        );
        INSTANCE.registerMessage(
                index++, ConfigRequestPacket.class,
                ConfigRequestPacket::encode,
                ConfigRequestPacket::decode,
                ConfigRequestPacket::handle
        );
    }
}
