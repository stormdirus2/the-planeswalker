package net.fenrir.theplaneswalker.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fenrir.theplaneswalker.origins.TCPowers;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.AnvilScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnvilScreenHandler.class)
public class AnvilScreenHandlerMixin {

    @Inject(
            method = "canTakeOutput",
            at = @At("HEAD"),
            cancellable = true
    )
    public void canAlwaysUse(PlayerEntity player, boolean present, CallbackInfoReturnable<Boolean> cir) {
        if (TCPowers.MALFORMED_SOUL.isActive(player)) {
            cir.setReturnValue(true);
        }
    }

    @Redirect(method = "onTakeOutput", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;addExperienceLevels(I)V"))
    private void noSubtractLevels(PlayerEntity playerEntity, int levels) {
        if (!TCPowers.MALFORMED_SOUL.isActive(playerEntity)) {
            playerEntity.addExperienceLevels(levels);
        }
    }

    @Environment(EnvType.CLIENT)
    @Inject(
            method = "getLevelCost",
            at = @At("HEAD"),
            cancellable = true
    )
    public void noCost(CallbackInfoReturnable<Integer> cir) {
        if (TCPowers.MALFORMED_SOUL.isActive(MinecraftClient.getInstance().player)) {
            cir.setReturnValue(0);
        }
    }
}
