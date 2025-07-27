package com.my66.better_player.mixin;

import com.my66.better_player.BetterPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public class EntityMixin {
    @Shadow
    protected String stringUUID;

    /**
     * @author 6688
     * @reason 修改气泡上限
     */
    @Overwrite
    public int getMaxAirSupply() {
        if ((Object) this instanceof Player) {
            return BetterPlayer.getBetterPlayer(stringUUID).getAirSupply();
        }
        return 300;
    }
}
