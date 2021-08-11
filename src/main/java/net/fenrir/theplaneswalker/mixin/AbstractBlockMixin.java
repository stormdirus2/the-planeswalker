package net.fenrir.theplaneswalker.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.fenrir.theplaneswalker.origins.ActivatedPositionPower;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.EntityShapeContext;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AbstractBlock.class, priority = 999)
public class AbstractBlockMixin {
    @Inject(at = @At("RETURN"), method = "getOutlineShape", cancellable = true)
    private void modifyBlockOutline(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (context instanceof EntityShapeContext) {
            if (((EntityShapeContext) context).getEntity().isPresent()) {
                Entity entity = ((EntityShapeContext) context).getEntity().get();
                if (entity.world.getPlayers().stream().anyMatch(plr -> PowerHolderComponent.getPowers(plr, ActivatedPositionPower.class).stream().anyMatch(p -> p.pos.getManhattanDistance(pos) <= p.range))) {
                    cir.setReturnValue(VoxelShapes.empty());
                }
            }
        }
    }

    @Inject(method = "canPathfindThrough", at = @At("HEAD"), cancellable = true)
    private void allowPathfinding(BlockState state, BlockView world, BlockPos pos, NavigationType type, CallbackInfoReturnable<Boolean> cir) {
        if (world instanceof World) {
            if (((World) world).getPlayers().stream().anyMatch(plr -> PowerHolderComponent.getPowers(plr, ActivatedPositionPower.class).stream().anyMatch(p -> p.pos.getManhattanDistance(pos) <= p.range))) {
                cir.setReturnValue(true);
            }
        }
    }

}
