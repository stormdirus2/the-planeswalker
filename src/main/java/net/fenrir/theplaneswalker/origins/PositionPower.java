package net.fenrir.theplaneswalker.origins;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;

public class PositionPower extends Power {

    public BlockPos pos;


    public PositionPower(PowerType<?> type, LivingEntity entity, BlockPos pos) {
        super(type, entity);
        this.pos = pos;
    }

    @Override
    public NbtElement toTag() {
        return NbtHelper.fromBlockPos(pos);
    }

    @Override
    public void fromTag(NbtElement tag) {
        pos = NbtHelper.toBlockPos((NbtCompound) tag);
    }


}
