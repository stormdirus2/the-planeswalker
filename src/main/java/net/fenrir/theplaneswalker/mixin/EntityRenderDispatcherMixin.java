package net.fenrir.theplaneswalker.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fenrir.theplaneswalker.origins.TCPowers;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(value = EntityRenderDispatcher.class, priority = 999)
public class EntityRenderDispatcherMixin {

    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void preventRenderingEntities(Entity entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof LivingEntity) {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (TCPowers.PHASESHIFT.isActive(player) || TCPowers.PHASESHIFT.isActive(entity)) {
                cir.setReturnValue(false);
            }
        }
    }
}
