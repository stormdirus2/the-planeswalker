/*
 * The Planeswalker
 * Copyright (c) 2021 SciRave
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package net.scirave.theplaneswalker.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.scirave.theplaneswalker.origins.TCPowers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ExperienceOrbEntity.class)
public abstract class ExperienceOrbEntityMixin {


    @Shadow
    private PlayerEntity target;

    @Shadow
    public abstract int getExperienceAmount();

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ExperienceOrbEntity;expensiveUpdate()V", shift = At.Shift.AFTER))
    public void noTarget(CallbackInfo ci) {
        if (this.target != null && TCPowers.SOULFOOD.isActive(this.target)) {
            if (TCPowers.SOULFOOD.isActive(target)) {
                HungerManager hungerManager = target.getHungerManager();
                if (hungerManager.getFoodLevel() == 20 && hungerManager.getSaturationLevel() == 20) {
                    Map.Entry<EquipmentSlot, ItemStack> entry = EnchantmentHelper.chooseEquipmentWith(Enchantments.MENDING, this.target, ItemStack::isDamaged);
                    if (entry == null) {
                        this.target = null;
                    }
                }
            }
        }
    }


    @Inject(method = "onPlayerCollision", at = @At("HEAD"), cancellable = true)
    public void repairCheck(PlayerEntity player, CallbackInfo ci) {
        if (!player.world.isClient()) {
            if (TCPowers.SOULFOOD.isActive(player)) {
                HungerManager hungerManager = player.getHungerManager();
                if (hungerManager.getFoodLevel() == 20 && hungerManager.getSaturationLevel() == 20) {
                    Map.Entry<EquipmentSlot, ItemStack> entry = EnchantmentHelper.chooseEquipmentWith(Enchantments.MENDING, player, ItemStack::isDamaged);
                    if (entry == null) {
                        ci.cancel();
                        return;
                    }
                }
                hungerManager.add(getExperienceAmount(), 0.6F);
            }
        }
    }

}
