package net.fenrir.theplaneswalker.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.fenrir.theplaneswalker.origins.OnTeleportPower;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayerNetworkHandlerMixin {

    @Shadow
    public ServerPlayerEntity player;

    @Inject(method = "requestTeleport(DDDFFLjava/util/Set;Z)V", at = @At("HEAD"))
    public void teleporting(double x, double y, double z, float yaw, float pitch, Set<PlayerPositionLookS2CPacket.Flag> flags, boolean shouldDismount, CallbackInfo ci) {
        PowerHolderComponent.getPowers(this.player, OnTeleportPower.class).forEach(OnTeleportPower::onTeleport);
    }

}
