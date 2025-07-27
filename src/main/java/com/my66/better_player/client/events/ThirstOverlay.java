package com.my66.better_player.client.events;

import com.my66.better_player.BetterPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "better_player", value = Dist.CLIENT)
public class ThirstOverlay {
    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        // 检查类型为 FOOD_LEVEL（饥饿条）
        String id = event.getOverlay().id().toString();
        if (id.equals("minecraft:food_level")) {
            Minecraft mc = Minecraft.getInstance();
            LocalPlayer player = mc.player;
            if (player == null) return;

            BetterPlayer bp = BetterPlayer.getBetterPlayer(player);

            int rightHeight = 39 + 11;
            if (bp.getEndurance() > 20) rightHeight += 11;

            int width = event.getWindow().getGuiScaledWidth();
            int height = event.getWindow().getGuiScaledHeight();
            int left = width / 2 + 91;
            int top = height - rightHeight;
            for (int i = 0; i < (float) bp.getWater()/2; ++i)
            {
                int x = left - i * 8 - 9;
                int y = top;
                if (i >= 10) {
                    x = left - (i - 10) * 8 - 9;
                    y = top - 10;
                }

                ResourceLocation GUI_ICONS_LOCATION = ResourceLocation.fromNamespaceAndPath("better_player", "textures/gui/thirst_icons.png");
                GuiGraphics guiGraphics = event.getGuiGraphics();
                guiGraphics.blit(GUI_ICONS_LOCATION, x, y, 0, 0, 9, 9, 25, 9);

                if (i < (float)bp.getThirst() / 2)
                    guiGraphics.blit(GUI_ICONS_LOCATION, x, y, 16, 0, 9, 9, 25, 9);
                else if (i == (float)bp.getThirst() / 2)
                    guiGraphics.blit(GUI_ICONS_LOCATION, x, y, 8, 0, 9, 9, 25, 9);
            }
        }
    }
}