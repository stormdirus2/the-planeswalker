package net.fenrir.theplaneswalker.mixin;

import net.fenrir.theplaneswalker.origins.TCPowers;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin {

    @Shadow
    protected abstract void sayNo();

    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    public void noTrade(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (TCPowers.TARNISHED_REPUTATION.isActive(player) && !player.hasStatusEffect(StatusEffects.HERO_OF_THE_VILLAGE)) {
            this.sayNo();
            cir.setReturnValue(ActionResult.success(player.world.isClient));
        }
    }

}
