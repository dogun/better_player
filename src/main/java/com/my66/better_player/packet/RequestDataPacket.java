package com.my66.better_player.packet;

import com.my66.better_player.BetterPlayerMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

public class RequestDataPacket {
    private final String data;
    public RequestDataPacket(String data) { this.data = data; }

    public static void handle(RequestDataPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            String data = BetterPlayerMod.loadSSData(player);
            ModPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(()->player), new ResponseDataPacket(data));
        });
        ctx.get().setPacketHandled(true);
    }

    public static void encode(RequestDataPacket pkt, FriendlyByteBuf buf) {
        buf.writeByteArray(pkt.data.getBytes(StandardCharsets.UTF_8));
    }

    public static RequestDataPacket decode(FriendlyByteBuf buf) {
        return new RequestDataPacket(new String(buf.readByteArray(128), StandardCharsets.UTF_8));
    }
}
