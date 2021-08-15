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

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.scirave.theplaneswalker.origins.ActivatedPositionPower;
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
