package net.fenrir.theplaneswalker.helpers;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;

public interface ServerPlayerEntityInterface {

    LivingEntity getLastAttacked();

    void setLastAttacked(LivingEntity entity);


    BlockPos getLastInteracted();

    void setLastInteracted(BlockPos pos);


}
