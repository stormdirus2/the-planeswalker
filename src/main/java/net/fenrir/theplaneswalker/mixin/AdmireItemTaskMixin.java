package net.fenrir.theplaneswalker.mixin;

import net.fenrir.theplaneswalker.origins.TCPowers;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.AdmireItemTask;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(AdmireItemTask.class)
public class AdmireItemTaskMixin {

    @Inject(method = "shouldRun", at = @At("HEAD"), cancellable = true)
    public <E extends PiglinEntity> void badReputation(ServerWorld serverWorld, E piglinEntity, CallbackInfoReturnable<Boolean> cir) {
        Optional<PlayerEntity> optional = piglinEntity.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER);
        if (optional.isPresent()) {
            if (TCPowers.TARNISHED_REPUTATION.isActive(optional.get())) {
                cir.setReturnValue(false);
            }
        }
    }

}
