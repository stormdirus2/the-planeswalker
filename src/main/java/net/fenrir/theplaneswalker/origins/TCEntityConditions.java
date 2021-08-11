package net.fenrir.theplaneswalker.origins;

import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableData;
import net.fenrir.theplaneswalker.ThePlaneswalker;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class TCEntityConditions {

    private static void register(ConditionFactory<LivingEntity> conditionFactory) {
        Registry.register(ApoliRegistries.ENTITY_CONDITION, conditionFactory.getSerializerId(), conditionFactory);
    }

    public static void initialization() {
        register(new ConditionFactory<>(new Identifier(ThePlaneswalker.MODID, "is_flying"), new SerializableData(),
                (data, entity) -> {
                    if (entity instanceof PlayerEntity) {
                        return ((PlayerEntity) entity).getAbilities().flying;
                    }
                    return false;
                }));
    }


}
