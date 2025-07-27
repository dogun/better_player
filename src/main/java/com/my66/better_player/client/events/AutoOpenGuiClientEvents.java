package com.my66.better_player.client.events;

import com.my66.better_player.packet.ConfigRequestPacket;
import com.my66.better_player.packet.ModPacketHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.client.Minecraft;

@Mod.EventBusSubscriber(modid = "better_player", value = Dist.CLIENT)
public class AutoOpenGuiClientEvents {
    @SubscribeEvent
    public static void onPlayerLoggedIn(ClientPlayerNetworkEvent.LoggingIn event) {
        Minecraft.getInstance().tell(() -> ModPacketHandler.INSTANCE.sendToServer(new ConfigRequestPacket(true)));
    }
}