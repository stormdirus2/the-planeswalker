package net.fenrir.theplaneswalker.mixin;

import net.fenrir.theplaneswalker.origins.TCPowers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ProjectileEntity.class)
public class ProjectileEntityMixin {

    @Shadow
    @Nullable
    private Entity owner;

    @Inject(method = "canHit", at = @At("HEAD"), cancellable = true)
    public void noHit(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (TCPowers.PHASESHIFT.isActive(entity) || (this.owner != null && TCPowers.PHASESHIFT.isActive(this.owner))) {
            cir.setReturnValue(false);
        }
    }

}
