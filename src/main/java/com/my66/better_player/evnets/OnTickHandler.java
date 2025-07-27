package com.my66.better_player.evnets;

import com.my66.better_player.BetterPlayer;
import com.my66.better_player.BetterPlayerConfig;
import com.my66.better_player.BetterPlayerMod;
import com.my66.better_player.packet.AgeUpdatePacket;
import com.my66.better_player.packet.ConfigResponsePacket;
import com.my66.better_player.packet.ModPacketHandler;
import com.my66.better_player.packet.ThirstResponsePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = "better_player")
public class OnTickHandler {
    private static final Map<String, Integer> lastData = new HashMap<>();

    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent event) {
        // 只在服务端主世界执行
        if (event.level instanceof ServerLevel serverLevel && !event.level.isClientSide
                && event.phase == TickEvent.Phase.END) {
            if (serverLevel.getDayTime() % 100 == 0) { //读取配置并分发
                String data = BetterPlayerMod.loadConfig(serverLevel.getRandomPlayer(), true);
                if (null != data) { //有更新
                    BetterPlayerConfig config = BetterPlayerConfig.INSTANCE;
                    config.sync(data);

                    for (ServerPlayer player : serverLevel.getPlayers(serverPlayer -> true)) {
                        ModPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                                new ConfigResponsePacket(false, data));
                    }
                }
            }

            if (serverLevel.getDayTime() % 10 == 0) { //处理口渴问题
                for (ServerPlayer player : serverLevel.getPlayers(serverPlayer -> true)) {
                    BetterPlayer bp = BetterPlayer.getBetterPlayer(player);
                    int food = player.getFoodData().getFoodLevel();
                    int last = lastData.computeIfAbsent(player.getStringUUID(), k -> 0);
                    if (food < last) { //鸡腿数量在降
                        int num = last - food;
                        System.out.println("food now: " + food + ", last: " + last);
                        bp.setThirst(bp.getThirst() - num);
                        BetterPlayerMod.saveSSData(player, bp.getData());
                        //通知客户端
                        ModPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new ThirstResponsePacket(bp.getThirst()));
                    }
                    lastData.put(player.getStringUUID(), food);
                }
            }

            if (serverLevel.getDayTime() % (int)(BetterPlayerConfig.INSTANCE.getDays() * 24000) == 0) { //增长了一岁
                for (ServerPlayer player : serverLevel.getPlayers(serverPlayer -> true)) {
                    System.out.println("player: " + player.getScoreboardName() + " update: " + serverLevel.getDayTime());
                    //年龄加1
                    String data = BetterPlayerMod.loadSSData(player);
                    BetterPlayer bp = BetterPlayer.getBetterPlayer(player);
                    bp.setData(data);
                    bp.setAge(bp.getAge() + 1);
                    BetterPlayerMod.setPlayerDataLocal(player, bp, false);
                    BetterPlayerMod.saveSSData(player, bp.getData());

                    //通知客户端
                    ModPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                            new AgeUpdatePacket(bp.getAge()));
                }
            }
        }
    }
}