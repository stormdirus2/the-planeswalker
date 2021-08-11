package net.fenrir.theplaneswalker.mixin;

import net.fenrir.theplaneswalker.helpers.ServerPlayerEntityInterface;
import net.fenrir.theplaneswalker.origins.TCPowers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LivingEntity.class, priority = 999)
public abstract class LivingEntityMixin extends EntityMixin {

    @Inject(method = "damage", at = @At("HEAD"))
    public void onDamaged(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        Entity attacker = source.getAttacker();
        if (attacker instanceof ServerPlayerEntity && attacker != ((Object) this)) {
            ((ServerPlayerEntityInterface) attacker).setLastAttacked((LivingEntity) (Object) this);
        }
    }

    @Inject(method = "isFallFlying", at = @At("HEAD"), cancellable = true)
    public void noGliding(CallbackInfoReturnable<Boolean> cir) {
        //Overridden
    }

    @Inject(method = "canSee", at = @At("HEAD"), cancellable = true)
    public void noSee(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (TCPowers.PHASESHIFT.isActive(entity)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "pushAway", at = @At("HEAD"), cancellable = true)
    public void stopPushingAway(Entity entity, CallbackInfo ci) {
        //Overridden
    }

    @Inject(method = "pushAwayFrom", at = @At("HEAD"), cancellable = true)
    public void stopPushingAwayFrom(Entity entity, CallbackInfo ci) {
        //Overridden
    }


}
