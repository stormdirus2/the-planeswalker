package net.fenrir.theplaneswalker.origins;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

import java.util.function.Consumer;

public class DimensionChangedPower extends Power {

    private final Consumer<Entity> entityAction;

    public DimensionChangedPower(PowerType<?> type, LivingEntity entity, Consumer<Entity> entityAction) {
        super(type, entity);
        this.entityAction = entityAction;
    }

    public void onChange() {
        this.entityAction.accept(this.entity);
    }

}
