package net.fenrir.theplaneswalker.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.fenrir.theplaneswalker.origins.ActivatedPositionPower;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PersistentProjectileEntity.class)
public class PersistentProjectileEntityMixin {

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
    public BlockState redirectedMethod(World world, BlockPos pos) {
        if (world.getPlayers().stream().anyMatch(plr -> PowerHolderComponent.getPowers(plr, ActivatedPositionPower.class).stream().anyMatch(p -> p.pos.getManhattanDistance(pos) <= p.range))) {
            return Blocks.AIR.getDefaultState();
        }
        return world.getBlockState(pos);
    }

}
