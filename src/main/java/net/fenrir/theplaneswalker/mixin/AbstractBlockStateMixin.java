package net.fenrir.theplaneswalker.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.fenrir.theplaneswalker.origins.ActivatedPositionPower;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AbstractBlock.AbstractBlockState.class, priority = 999)
public abstract class AbstractBlockStateMixin {


    @Inject(at = @At("HEAD"), method = "getCollisionShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;", cancellable = true)
    private void phaseThroughBlocks(BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> info) {
        if (world instanceof World) {
            if (((World) world).getPlayers().stream().anyMatch(plr -> PowerHolderComponent.getPowers(plr, ActivatedPositionPower.class).stream().anyMatch(p -> p.pos.getManhattanDistance(pos) <= p.range))) {
                info.setReturnValue(VoxelShapes.empty());
            }
        }
    }

    @Inject(method = "onEntityCollision", at = @At("HEAD"), cancellable = true)
    private void preventCollisionWhenPhasing(World world, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (entity.world.getPlayers().stream().anyMatch(plr -> PowerHolderComponent.getPowers(plr, ActivatedPositionPower.class).stream().anyMatch(p -> p.pos.getManhattanDistance(pos) <= p.range))) {
            ci.cancel();
        }
    }

    @Inject(method = "shouldBlockVision", at = @At("HEAD"), cancellable = true)
    private void allowSight(BlockView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (world instanceof World) {
            if (((World) world).getPlayers().stream().anyMatch(plr -> PowerHolderComponent.getPowers(plr, ActivatedPositionPower.class).stream().anyMatch(p -> p.pos.getManhattanDistance(pos) <= p.range))) {
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "hasSolidTopSurface(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Direction;)Z", at = @At("HEAD"), cancellable = true)
    private void noSolidTop(BlockView world, BlockPos pos, Entity entity, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (entity.world.getPlayers().stream().anyMatch(plr -> PowerHolderComponent.getPowers(plr, ActivatedPositionPower.class).stream().anyMatch(p -> p.pos.getManhattanDistance(pos) <= p.range))) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "getRaycastShape", at = @At("HEAD"), cancellable = true)
    private void noRaycast(BlockView world, BlockPos pos, CallbackInfoReturnable<VoxelShape> cir) {
        if (world instanceof World) {
            if (((World) world).getPlayers().stream().anyMatch(plr -> PowerHolderComponent.getPowers(plr, ActivatedPositionPower.class).stream().anyMatch(p -> p.pos.getManhattanDistance(pos) <= p.range))) {
                cir.setReturnValue(VoxelShapes.empty());
            }
        }
    }

    @Inject(method = "canReplace", at = @At("HEAD"), cancellable = true)
    public void noReplace(ItemPlacementContext context, CallbackInfoReturnable<Boolean> cir) {
        if (context.getPlayer() != null) {
            if (context.getWorld().getPlayers().stream().anyMatch(plr -> PowerHolderComponent.getPowers(plr, ActivatedPositionPower.class).stream().anyMatch(p -> p.pos.getManhattanDistance(context.getBlockPos()) <= p.range))) {
                cir.setReturnValue(false);
            }
        }
    }

}
