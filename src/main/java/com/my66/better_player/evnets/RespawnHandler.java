package com.my66.better_player.evnets;

import com.my66.better_player.BetterPlayer;
import com.my66.better_player.BetterPlayerConfig;
import com.my66.better_player.BetterPlayerMod;
import com.my66.better_player.packet.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = "better_player")
public class RespawnHandler {
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player lplayer = event.getEntity();
        if (lplayer instanceof ServerPlayer player) {
            ServerLevel world = player.serverLevel();

            GameType gt = player.gameMode.getGameModeForPlayer();
            BetterPlayer bt = BetterPlayer.getBetterPlayer(player);
            bt.setGameType(gt);
            player.setGameMode(GameType.SPECTATOR);

            String data = BetterPlayerMod.loadSSData(player);

            ModPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new RespawnResponseDataPacket(data));

            if (BetterPlayerConfig.INSTANCE.isTeleport()) {
                int x;
                int y;
                int z;
                while (true) {
                    // 生成随机坐标（以主世界为例），比如 x/z 在 -1000~1000 范围
                    x = (int) ((world.random.nextDouble() - 0.5) * 2000);
                    z = (int) ((world.random.nextDouble() - 0.5) * 2000);
                    // 获取地表y坐标
                    BlockPos surface = world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(x, 0, z));
                    y = surface.getY() + 1;
                    // 传送玩家
                    System.out.println(x + " " + y + " " + z + "\n");
                    // 检查y和y+1都是空气
                    BlockPos pos1 = new BlockPos(x, y, z);
                    BlockPos pos2 = new BlockPos(x, y + 1, z);
                    if (world.isEmptyBlock(pos1) && world.isEmptyBlock(pos2)) {
                        break;
                    }
                }
                player.teleportTo(x, y, z);
            }
        }
    }
}

