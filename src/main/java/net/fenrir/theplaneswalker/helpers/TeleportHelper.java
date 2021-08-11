package net.fenrir.theplaneswalker.helpers;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;

public class TeleportHelper {

    public static boolean viableNonCollide(BlockState state) {
        return state.getMaterial() == Material.WATER || (!state.getMaterial().isLiquid() && !state.getMaterial().blocksMovement());
    }

    public static boolean viableNonCollideNoWater(BlockState state) {
        return !state.getMaterial().isLiquid() && !state.getMaterial().blocksMovement();
    }

    public static boolean viableCollide(BlockState state) {
        return state.getMaterial() == Material.WATER || state.isIn(BlockTags.VALID_SPAWN) || state.getMaterial().blocksMovement();
    }

    public static Integer platformUp(ServerWorld world, int x, int y, int z) {
        int level = y;

        BlockState one;
        BlockState two;
        BlockState three;

        do {
            one = world.getBlockState(new BlockPos(x, level, z));
            two = world.getBlockState(new BlockPos(x, level + 1, z));
            three = world.getBlockState(new BlockPos(x, level + 2, z));
            level++;
            if (world.isOutOfHeightLimit(level + 2) || ((level + 2) > world.getLogicalHeight())) {
                return null;
            }
        } while (!viableNonCollide(one) || !viableNonCollide(two) || !viableNonCollideNoWater(three));

        world.setBlockState(new BlockPos(x, level, z), Blocks.COBBLESTONE.getDefaultState());

        return level + 2;
    }

    public static Integer up(ServerWorld world, int x, int y, int z) {
        int level = y;

        BlockState one;
        BlockState two;
        BlockState three;

        do {
            if (world.isOutOfHeightLimit(level + 2) || ((level + 2) > world.getLogicalHeight())) {
                return null;
            }
            one = world.getBlockState(new BlockPos(x, level, z));
            two = world.getBlockState(new BlockPos(x, level + 1, z));
            three = world.getBlockState(new BlockPos(x, level + 2, z));
            level++;
        } while (!viableCollide(one) || !viableNonCollide(two) || !viableNonCollideNoWater(three));

        return level + 2;
    }

    public static Integer down(ServerWorld world, int x, int y, int z) {
        int level = y;


        BlockState one;
        BlockState two;
        BlockState three;

        do {
            if (world.isOutOfHeightLimit(level)) {
                return null;
            }
            one = world.getBlockState(new BlockPos(x, level, z));
            two = world.getBlockState(new BlockPos(x, level + 1, z));
            three = world.getBlockState(new BlockPos(x, level + 2, z));
            level--;
        } while (!viableCollide(one) || !viableNonCollide(two) || !viableNonCollideNoWater(three));


        return level + 2;
    }

    public static Integer safeSpawn(ServerWorld world, int x, int z) {

        int minimum = world.getBottomY();
        int maximum = world.getLogicalHeight();
        int middle = Math.floorDiv(Math.abs(minimum) + maximum, 2) + minimum;

        BlockState one = world.getBlockState(new BlockPos(x, middle, z));
        BlockState two = world.getBlockState(new BlockPos(x, middle + 1, z));
        BlockState three = world.getBlockState(new BlockPos(x, middle + 2, z));

        if (viableNonCollide(one) && viableNonCollide(two) && viableNonCollideNoWater(three)) {
            Integer level = down(world, x, middle, z);
            if (level == null) {
                level = up(world, x, middle, z);
                if (level != null) {
                    return level;
                }
            } else {
                return level;
            }
        } else {
            Integer level = up(world, x, middle, z);
            if (level == null) {
                level = down(world, x, middle, z);
                if (level != null) {
                    return level;
                }
            } else {
                return level;
            }
        }

        return platformUp(world, x, middle, z);

    }


}
