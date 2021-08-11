package net.fenrir.theplaneswalker.mixin;

import net.fenrir.theplaneswalker.origins.TCPowers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.MilkBucketItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MilkBucketItem.class)
public class MilkBucketItemMixin {


    @Redirect(method = "finishUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;clearStatusEffects()Z"))
    private boolean noClearEffects(LivingEntity entity) {
        if (TCPowers.CORRUPTED_BLOOD.isActive(entity)) {
            return false;
        }
        return entity.clearStatusEffects();
    }

}
