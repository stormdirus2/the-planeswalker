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

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.scirave.theplaneswalker.origins.ActivatedPositionPower;
import net.scirave.theplaneswalker.origins.TCPowers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AbstractBlock.AbstractBlockState.class, priority = 999)
public abstract class AbstractBlockStateMixin {


    @Inject(at = @At("HEAD"), method = "getCollisionShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;", cancellable = true)
    private void phaseThroughBlocks(BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> info) {
        if (world instanceof World actualWorld) {
            for (PlayerEntity plr : actualWorld.getPlayers()) {
                ActivatedPositionPower power = (ActivatedPositionPower) TCPowers.DIMENSIONAL_RIFT.get(plr);
                if (power != null && power.isActive() && pos.getManhattanDistance(power.pos) <= power.range) {
                    info.setReturnValue(VoxelShapes.empty());
                    return;
                }
            }
        }
    }

    @Inject(method = "onEntityCollision", at = @At("HEAD"), cancellable = true)
    private void preventCollisionWhenPhasing(World world, BlockPos pos, Entity entity, CallbackInfo ci) {
        for (PlayerEntity plr : entity.world.getPlayers()) {
            ActivatedPositionPower power = (ActivatedPositionPower) TCPowers.DIMENSIONAL_RIFT.get(plr);
            if (power != null && power.isActive() && pos.getManhattanDistance(power.pos) <= power.range) {
                ci.cancel();
                return;
            }
        }
    }

    @Inject(method = "shouldBlockVision", at = @At("HEAD"), cancellable = true)
    private void allowSight(BlockView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (world instanceof World actualWorld) {
            for (PlayerEntity plr : actualWorld.getPlayers()) {
                ActivatedPositionPower power = (ActivatedPositionPower) TCPowers.DIMENSIONAL_RIFT.get(plr);
                if (power != null && power.isActive() && pos.getManhattanDistance(power.pos) <= power.range) {
                    cir.setReturnValue(false);
                    return;
                }
            }
        }
    }

    @Inject(method = "hasSolidTopSurface(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Direction;)Z", at = @At("HEAD"), cancellable = true)
    private void noSolidTop(BlockView world, BlockPos pos, Entity entity, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        for (PlayerEntity plr : entity.world.getPlayers()) {
            ActivatedPositionPower power = (ActivatedPositionPower) TCPowers.DIMENSIONAL_RIFT.get(plr);
            if (power != null && power.isActive() && pos.getManhattanDistance(power.pos) <= power.range) {
                cir.setReturnValue(false);
                return;
            }
        }
    }

    @Inject(method = "getRaycastShape", at = @At("HEAD"), cancellable = true)
    private void noRaycast(BlockView world, BlockPos pos, CallbackInfoReturnable<VoxelShape> cir) {
        if (world instanceof World actualWorld) {
            for (PlayerEntity plr : actualWorld.getPlayers()) {
                ActivatedPositionPower power = (ActivatedPositionPower) TCPowers.DIMENSIONAL_RIFT.get(plr);
                if (power != null && power.isActive() && pos.getManhattanDistance(power.pos) <= power.range) {
                    cir.setReturnValue(VoxelShapes.empty());
                    return;
                }
            }
        }
    }

    @Inject(method = "canReplace", at = @At("HEAD"), cancellable = true)
    public void noReplace(ItemPlacementContext context, CallbackInfoReturnable<Boolean> cir) {
        if (context.getPlayer() != null) {
            for (PlayerEntity plr : context.getWorld().getPlayers()) {
                ActivatedPositionPower power = (ActivatedPositionPower) TCPowers.DIMENSIONAL_RIFT.get(plr);
                if (power != null && power.isActive() && context.getBlockPos().getManhattanDistance(power.pos) <= power.range) {
                    cir.setReturnValue(false);
                    return;
                }
            }
        }
    }

}
