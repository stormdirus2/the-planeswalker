package net.fenrir.theplaneswalker.mixin;

import net.fenrir.theplaneswalker.origins.TCPowers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PiglinBrain.class)
public abstract class PiglinBrainMixin {

    @Inject(method = "wearsGoldArmor", at = @At("HEAD"), cancellable = true)
    private static void badReputation1(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if (TCPowers.TARNISHED_REPUTATION.isActive(entity)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "playerInteract", at = @At("HEAD"), cancellable = true)
    private static void badReputation2(PiglinEntity piglin, PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (TCPowers.TARNISHED_REPUTATION.isActive(player)) {
            cir.setReturnValue(ActionResult.PASS);
        }
    }


}
