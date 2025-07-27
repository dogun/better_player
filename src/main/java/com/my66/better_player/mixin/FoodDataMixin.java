package com.my66.better_player.mixin;

import com.my66.better_player.BetterPlayer;
import com.my66.better_player.FoodDataBridge;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(FoodData.class)
public class FoodDataMixin implements FoodDataBridge {
    @Shadow private int foodLevel;
    @Shadow private float saturationLevel;

    @Unique
    public String better_player$uuid;

    /**
     * @author 6688
     * @reason 修改鸡腿上限
     */
    @Overwrite
    public void eat(int p_38708_, float p_38709_) {
        this.foodLevel = Math.min(p_38708_ + this.foodLevel, BetterPlayer.getBetterPlayer(better_player$getUuid()).getEndurance());
        this.saturationLevel = Math.min(this.saturationLevel + (float) p_38708_ * p_38709_ * 2.0F, (float) this.foodLevel);
    }

    /**
     * @author 6688
     * @reason 修改鸡腿上限
     */
    @Overwrite
    public boolean needsFood() {
        if (better_player$getUuid() != null) {
            return this.foodLevel < BetterPlayer.getBetterPlayer(better_player$getUuid()).getEndurance();
        }
        return this.foodLevel < 20;
    }

    /**
     * @author 6688
     * @reason 日志
     */
    @Overwrite
    public void setFoodLevel(int p_38706_) {
        this.foodLevel = p_38706_;
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        StackTraceElement caller = stack[2];
        System.out.println("caller:" + caller.getClassName() + " set food level: " + p_38706_);
    }

    /**
     * 将 FoodData#tick(Player) 里的 foodLevel >= 18 改为 foodLevel >= 8
     */
    @ModifyConstant(
            method = "tick(Lnet/minecraft/world/entity/player/Player;)V",
            constant = @Constant(intValue = 18),
            require = 1
    )
    private int modifyHealThreshold(int original) {
        return 8;
    }

    @Override
    public void better_player$setUuid(String uuid) {
        this.better_player$uuid = uuid;
    }

    @Override
    public String better_player$getUuid() {
        return this.better_player$uuid;
    }
}
