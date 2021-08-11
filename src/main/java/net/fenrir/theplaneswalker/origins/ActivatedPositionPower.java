package net.fenrir.theplaneswalker.origins;

import io.github.apace100.apoli.power.PowerType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;

public class ActivatedPositionPower extends PositionPower {

    public int range;

    public ActivatedPositionPower(PowerType<?> type, LivingEntity entity, BlockPos pos, int range) {
        super(type, entity, pos);
        this.range = range;
    }

}
