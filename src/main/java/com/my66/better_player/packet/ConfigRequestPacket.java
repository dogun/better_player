package com.my66.better_player.packet;

import com.my66.better_player.BetterPlayerConfig;
import com.my66.better_player.BetterPlayerMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.io.UnsupportedEncodingException;
import java.util.function.Supplier;

public class ConfigRequestPacket {
    private final boolean gui;
    public ConfigRequestPacket(boolean gui) { this.gui = gui; }

    public static void handle(ConfigRequestPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            String data = BetterPlayerMod.loadConfig(ctx.get().getSender(), false);
            BetterPlayerConfig config = BetterPlayerConfig.INSTANCE;
            config.sync(data);

            ModPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(()->ctx.get().getSender()),
                    new ConfigResponsePacket(pkt.gui, data));
        });
        ctx.get().setPacketHandled(true);
    }

    public static void encode(ConfigRequestPacket pkt, FriendlyByteBuf buf) {
        buf.writeBoolean(pkt.gui);
    }

    public static ConfigRequestPacket decode(FriendlyByteBuf buf) {
        return new ConfigRequestPacket(buf.readBoolean());
    }
}
