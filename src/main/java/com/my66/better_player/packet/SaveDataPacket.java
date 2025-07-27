package com.my66.better_player.packet;

import com.my66.better_player.BetterPlayer;
import com.my66.better_player.BetterPlayerMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraftforge.network.NetworkEvent;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

public class SaveDataPacket {
    private final boolean reset;
    private final String data;
    public SaveDataPacket(boolean reset, String data) {
        this.reset = reset;
        this.data = data;
    }

    public static void handle(SaveDataPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();

            BetterPlayer bp = BetterPlayer.getBetterPlayer(player);
            bp.setData(pkt.data);
            BetterPlayerMod.saveSSData(player, bp.getData());

            BetterPlayerMod.setPlayerDataLocal(player, bp, pkt.reset);

            player.setGameMode(bp.getGameType());

        });
        ctx.get().setPacketHandled(true);
    }

    public static void encode(SaveDataPacket pkt, FriendlyByteBuf buf) {
        buf.writeBoolean(pkt.reset);
        buf.writeByteArray(pkt.data.getBytes(StandardCharsets.UTF_8));
    }

    public static SaveDataPacket decode(FriendlyByteBuf buf) {
        return new SaveDataPacket(buf.readBoolean(), new String(buf.readByteArray(128), StandardCharsets.UTF_8));
    }
}
