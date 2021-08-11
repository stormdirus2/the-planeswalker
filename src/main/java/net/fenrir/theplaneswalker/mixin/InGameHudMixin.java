package net.fenrir.theplaneswalker.mixin;

import net.fenrir.theplaneswalker.origins.TCPowers;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = InGameHud.class)
public abstract class InGameHudMixin {


    @Shadow
    protected abstract PlayerEntity getCameraPlayer();

    @Redirect(
            method = "renderCrosshair",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;isAlive()Z"
            )
    )
    public boolean isAlive(Entity entity) {
        if (TCPowers.PHASESHIFT.isActive(entity) || TCPowers.PHASESHIFT.isActive(this.getCameraPlayer())) {
            return false;
        }
        return entity.isAlive();
    }


}