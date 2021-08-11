package net.fenrir.theplaneswalker.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.fenrir.theplaneswalker.origins.ActivatedPositionPower;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;

@Mixin(value = WorldRenderer.class, priority = 999)
public class WorldRendererMixin {
    private final HashMap<BlockPos, BlockState> savedStates = new HashMap<>();
    @Shadow
    private ClientWorld world;

    @Inject(at = @At(value = "HEAD"), method = "render")
    private void beforeRender(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo ci) {
        world.getPlayers().forEach((plr) -> PowerHolderComponent.getPowers(plr, ActivatedPositionPower.class).forEach((power) -> {
            int range = power.range;
            for (int a = -range; a <= range; a++) {
                int range2 = Math.abs(a);
                for (int b = -range + range2; b <= (range - range2); b++) {
                    int range3 = Math.abs(b);
                    for (int c = -range + range2 + range3; c <= (range - range2 - range3); c++) {
                        BlockPos pos = new BlockPos(power.pos.getX() + a, power.pos.getY() + b, power.pos.getZ() + c);
                        BlockState state = world.getBlockState(pos);
                        if (!state.isAir()) {
                            savedStates.put(pos, state);
                            world.setBlockStateWithoutNeighborUpdates(pos, Blocks.AIR.getDefaultState());
                        }
                    }
                }
            }
        }));
    }

    @Inject(at = @At("TAIL"), method = "render")
    public void afterRender(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo ci) {
        savedStates.forEach((pos, state) -> world.setBlockStateWithoutNeighborUpdates(pos, state));
        savedStates.clear();
    }
}
