package net.fenrir.theplaneswalker.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.fenrir.theplaneswalker.origins.ActivatedPositionPower;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow
    public World world;

    public FluidState method(World world, BlockPos pos) {
        if (world.getPlayers().stream().anyMatch(plr -> PowerHolderComponent.getPowers(plr, ActivatedPositionPower.class).stream().anyMatch(p -> p.pos.getManhattanDistance(pos) <= p.range))) {
            return Fluids.EMPTY.getDefaultState();
        }
        return world.getFluidState(pos);
    }

    @Redirect(method = "updateSwimming", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getFluidState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/fluid/FluidState;"))
    public FluidState redirectedMethod1(World world, BlockPos pos) {
        return method(world, pos);
    }

    @Redirect(method = "updateSubmergedInWaterState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getFluidState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/fluid/FluidState;"))
    public FluidState redirectedMethod2(World world, BlockPos pos) {
        return method(world, pos);
    }

    @Redirect(method = "updateMovementInFluid", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getFluidState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/fluid/FluidState;"))
    public FluidState redirectedMethod3(World world, BlockPos pos) {
        return method(world, pos);
    }

    @Inject(method = "isInvisible", at = @At("HEAD"), cancellable = true)
    public void invisible(CallbackInfoReturnable<Boolean> cir) {
        //Overridden
    }

    @Inject(method = "isInvisibleTo", at = @At("HEAD"), cancellable = true)
    public void invisibleTo(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        //Overridden
    }

}
