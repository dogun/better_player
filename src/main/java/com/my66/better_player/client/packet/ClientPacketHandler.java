package com.my66.better_player.client.packet;

import com.my66.better_player.BetterPlayer;
import com.my66.better_player.BetterPlayerConfig;
import com.my66.better_player.BetterPlayerMod;
import com.my66.better_player.client.gui.PlayerConfigurationGui;
import com.my66.better_player.packet.*;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientPacketHandler {
    public static void register() {
        ModPacketHandler.INSTANCE.registerMessage(
                ModPacketHandler.index++, ResponseDataPacket.class,
                ResponseDataPacket::encode,
                ResponseDataPacket::decode,
                (pkt, ctx) -> {
                    if ("".equals(pkt.data)) { //需要捏人
                        ctx.get().enqueueWork(() -> Minecraft.getInstance().setScreen(new PlayerConfigurationGui()));
                    }else { //加载数据
                        BetterPlayer bp = BetterPlayer.getBetterPlayer(Minecraft.getInstance().player);
                        bp.setData(pkt.data);
                        BetterPlayerMod.setPlayerDataLocal(Minecraft.getInstance().player, bp, false);
                        BetterPlayerMod.setPlayerDataToServer(bp, false);
                    }
                    ctx.get().setPacketHandled(true);
                }
        );
        ModPacketHandler.INSTANCE.registerMessage(
                ModPacketHandler.index++, RespawnResponseDataPacket.class,
                RespawnResponseDataPacket::encode,
                RespawnResponseDataPacket::decode,
                (pkt, ctx) -> {
                    ctx.get().enqueueWork(() -> {
                        String data = pkt.data;
                        PlayerConfigurationGui gui = new PlayerConfigurationGui();
                        Minecraft.getInstance().setScreen(gui);
                        gui.renderData(data, 1);
                        Minecraft.getInstance().setScreen(gui);
                        gui.renderData(data, 2);
                    });
                    ctx.get().setPacketHandled(true);
                }
        );
        ModPacketHandler.INSTANCE.registerMessage(
                ModPacketHandler.index++, AgeUpdatePacket.class,
                AgeUpdatePacket::encode,
                AgeUpdatePacket::decode,
                (pkt, ctx) -> {
                    ctx.get().enqueueWork(() -> {
                        BetterPlayer bp = BetterPlayer.getBetterPlayer(Minecraft.getInstance().player);
                        bp.setAge(pkt.age);

                        System.out.println("receive age update packet:" + pkt.age);
                    });
                    ctx.get().setPacketHandled(true);
                }
        );
        ModPacketHandler.INSTANCE.registerMessage(
                ModPacketHandler.index++, ConfigResponsePacket.class,
                ConfigResponsePacket::encode,
                ConfigResponsePacket::decode,
                (pkt, ctx) -> {
                    ctx.get().enqueueWork(() -> {
                        System.out.println("receive config: " + pkt.data + ", gui: " + pkt.gui);
                        BetterPlayerConfig.INSTANCE.sync(pkt.data);
                        if (pkt.gui) {
                            ModPacketHandler.INSTANCE.sendToServer(new RequestDataPacket(""));
                        }
                    });
                    ctx.get().setPacketHandled(true);
                }
        );
        ModPacketHandler.INSTANCE.registerMessage(
                ModPacketHandler.index++, ThirstResponsePacket.class,
                ThirstResponsePacket::encode,
                ThirstResponsePacket::decode,
                (pkt, ctx) -> {
                    ctx.get().enqueueWork(() -> {
                        System.out.println("receive thirst: " + pkt.thirst);
                        BetterPlayer.getBetterPlayer(Minecraft.getInstance().player).setThirst(pkt.thirst);
                    });
                    ctx.get().setPacketHandled(true);
                }
        );
    }
}
