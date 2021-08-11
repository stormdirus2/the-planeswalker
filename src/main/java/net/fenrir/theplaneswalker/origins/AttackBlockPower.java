package net.fenrir.theplaneswalker.origins;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

import java.util.function.Consumer;

public class AttackBlockPower extends Power {

    private final Consumer<Entity> entityAction;

    public AttackBlockPower(PowerType<?> type, LivingEntity entity, Consumer<Entity> entityAction) {
        super(type, entity);
        this.entityAction = entityAction;
    }

    public void onAttack() {
        entityAction.accept(this.entity);
    }
}
