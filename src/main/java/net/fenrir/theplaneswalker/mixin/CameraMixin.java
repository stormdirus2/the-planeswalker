package net.fenrir.theplaneswalker.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.fenrir.theplaneswalker.origins.ActivatedPositionPower;
import net.minecraft.client.render.Camera;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Camera.class)
public class CameraMixin {

    public FluidState method(World world, BlockPos pos) {
        if (world.getPlayers().stream().anyMatch(plr -> PowerHolderComponent.getPowers(plr, ActivatedPositionPower.class).stream().anyMatch(p -> p.pos.getManhattanDistance(pos) <= p.range))) {
            return Fluids.EMPTY.getDefaultState();
        }
        return world.getFluidState(pos);
    }

    @Redirect(method = "getSubmersionType", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/BlockView;getFluidState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/fluid/FluidState;", ordinal = 0))
    public FluidState redirectedMethod1(BlockView blockView, BlockPos pos) {
        if (blockView instanceof World) {
            return method((World) blockView, pos);
        }
        return blockView.getFluidState(pos);
    }

    @Redirect(method = "getSubmersionType", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/BlockView;getFluidState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/fluid/FluidState;", ordinal = 1))
    public FluidState redirectedMethod2(BlockView blockView, BlockPos pos) {
        if (blockView instanceof World) {
            return method((World) blockView, pos);
        }
        return blockView.getFluidState(pos);
    }

}
