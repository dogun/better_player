package com.my66.better_player.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.my66.better_player.BetterPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.common.ForgeMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = ForgeGui.class)
public class ForgeGuiMixin {
    /**
     * @author 6688
     * @reason 渲染鸡腿
     */
    @Overwrite(remap = false)
    public void renderFood(int width, int height, GuiGraphics guiGraphics) {
        Minecraft minecraft = Minecraft.getInstance();
        int rightHeight = 39;

        minecraft.getProfiler().push("food");

        Player player = (Player) minecraft.getCameraEntity();
        RenderSystem.enableBlend();
        int left = width / 2 + 91;
        int top = height - rightHeight;

        FoodData stats = null;
        if (player != null) {
            stats = player.getFoodData();
        }
        int level = 0;
        if (stats != null) {
            level = stats.getFoodLevel();
        }

        //System.out.println("start render food: " + BetterPlayer.getBetterPlayer(player).getEndurance()/2);

        for (int i = 0; i < (float) BetterPlayer.getBetterPlayer(player).getEndurance()/2; ++i)
        {
            int idx = i * 2 + 1;
            int x = left - i * 8 - 9;
            int y = top;
            if (i >= 10) {
                x = left - (i - 10) * 8 - 9;
                y = top - 10;
            }
            int icon = 16;
            byte background = 0;

            if (player.hasEffect(MobEffects.HUNGER))
            {
                icon += 36;
                background = 13;
            }

            ResourceLocation GUI_ICONS_LOCATION = ResourceLocation.parse("textures/gui/icons.png");

            guiGraphics.blit(GUI_ICONS_LOCATION, x, y, 16 + background * 9, 27, 9, 9);

            if (idx < level)
                guiGraphics.blit(GUI_ICONS_LOCATION, x, y, icon + 36, 27, 9, 9);
            else if (idx == level)
                guiGraphics.blit(GUI_ICONS_LOCATION, x, y, icon + 45, 27, 9, 9);
        }
        RenderSystem.disableBlend();
        minecraft.getProfiler().pop();
    }

    /**
     * @author 6688
     * @reason 渲染气泡
     */
    @Overwrite(remap = false)
    protected void renderAir(int width, int height, GuiGraphics guiGraphics)
    {
        Minecraft minecraft = Minecraft.getInstance();
        int rightHeight = 39;

        minecraft.getProfiler().push("air");
        Player player = (Player) minecraft.getCameraEntity();
        RenderSystem.enableBlend();
        int left = width / 2 - 91 + 9;
        int top = height - rightHeight - 10;
        //if (BetterPlayer.getBetterPlayer(player).getEndurance() > 20) top -= 10;
        AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null && maxHealth.getBaseValue() > 20) {
            top -= 11;
        }

        int air = player.getAirSupply();
        if (player.isEyeInFluidType(ForgeMod.WATER_TYPE.get()) || air < BetterPlayer.getBetterPlayer(player).getAirSupply())
        {
            int full = Mth.ceil((double) (air - 2) * 10.0D / 300);
            int partial = Mth.ceil((double) air * 10.0D / 300) - full;

            ResourceLocation GUI_ICONS_LOCATION = ResourceLocation.parse("textures/gui/icons.png");
            for (int i = 0; i < full + partial; ++i)
            {
                guiGraphics.blit(GUI_ICONS_LOCATION, left + i * 8 - 9, top, (i < full ? 16 : 25), 18, 9, 9);
            }
        }

        RenderSystem.disableBlend();
        minecraft.getProfiler().pop();
    }

}
