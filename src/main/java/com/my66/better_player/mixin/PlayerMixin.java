package com.my66.better_player.mixin;

import com.mojang.authlib.GameProfile;
import com.my66.better_player.FoodDataBridge;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void onConstructed(Level p_250508_, BlockPos p_250289_, float p_251702_, GameProfile p_252153_, CallbackInfo ci) {
        Player self = (Player)(Object)this;
        FoodData food = self.getFoodData();
        // 这里假设你给FoodData加了uuid字段
        ((FoodDataBridge)food).better_player$setUuid(self.getStringUUID());
        System.out.println("FoodData set UUID:" + self.getStringUUID());
    }
}
