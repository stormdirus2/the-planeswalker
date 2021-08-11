package net.fenrir.theplaneswalker.mixin;

import net.fenrir.theplaneswalker.ThePlaneswalker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.sound.MusicSound;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MinecraftClient.class, priority = 999)
public class MinecraftClientMixin {


    @Shadow
    @Nullable
    public ClientWorld world;

    @Inject(method = "getMusicType", at = @At("HEAD"), cancellable = true)
    public void replaceMusic(CallbackInfoReturnable<MusicSound> cir) {
        if (this.world != null) {
            if (this.world.getRegistryKey().getValue().equals(ThePlaneswalker.DIMENSION)) {
                cir.setReturnValue(ThePlaneswalker.MUSIC);
            }
        }
    }

}
