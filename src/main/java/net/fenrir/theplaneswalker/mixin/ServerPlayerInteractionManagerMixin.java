package net.fenrir.theplaneswalker.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.fenrir.theplaneswalker.helpers.ServerPlayerEntityInterface;
import net.fenrir.theplaneswalker.origins.AttackBlockPower;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {

    @Shadow
    @Final
    protected ServerPlayerEntity player;

    @Inject(method = "processBlockBreakingAction", at = @At("HEAD"))
    public void redirectedMethod(BlockPos pos, Action action, Direction direction, int worldHeight, CallbackInfo ci) {
        System.out.println("Attacked block.");
        ((ServerPlayerEntityInterface) player).setLastInteracted(pos);
        PowerHolderComponent.getPowers(player, AttackBlockPower.class).forEach(AttackBlockPower::onAttack);
    }

}
