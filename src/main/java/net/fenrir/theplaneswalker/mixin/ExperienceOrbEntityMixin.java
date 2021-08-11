package net.fenrir.theplaneswalker.mixin;

import net.fenrir.theplaneswalker.origins.TCPowers;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ExperienceOrbEntity.class)
public class ExperienceOrbEntityMixin {


    @Shadow
    private PlayerEntity target;

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ExperienceOrbEntity;expensiveUpdate()V", shift = At.Shift.AFTER))
    public void noTarget(CallbackInfo ci) {
        if (this.target != null && TCPowers.MALFORMED_SOUL.isActive(this.target)) {
            Map.Entry<EquipmentSlot, ItemStack> entry = EnchantmentHelper.chooseEquipmentWith(Enchantments.MENDING, this.target, ItemStack::isDamaged);
            if (entry == null) {
                this.target = null;
            }
        }
    }


    @Inject(method = "onPlayerCollision", at = @At("HEAD"), cancellable = true)
    public void repairCheck(PlayerEntity player, CallbackInfo ci) {
        if (!player.world.isClient()) {
            if (TCPowers.MALFORMED_SOUL.isActive(player)) {
                Map.Entry<EquipmentSlot, ItemStack> entry = EnchantmentHelper.chooseEquipmentWith(Enchantments.MENDING, player, ItemStack::isDamaged);
                if (entry == null) {
                    ci.cancel();
                }
            }
        }
    }

}
