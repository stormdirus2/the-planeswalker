package net.fenrir.theplaneswalker.origins;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.PowerTypeReference;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.fenrir.theplaneswalker.ThePlaneswalker;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.function.Consumer;


public class TCPowers {

    public static final PowerType<?> FLYING = new PowerTypeReference<>(new Identifier(ThePlaneswalker.MODID, "spatial_stride"));

    public static final PowerType<?> PHASESHIFT = new PowerTypeReference<>(new Identifier(ThePlaneswalker.MODID, "phaseshift_drain"));

    public static final PowerType<?> MALFORMED_SOUL = new PowerTypeReference<>(new Identifier(ThePlaneswalker.MODID, "malformed_soul"));

    public static final PowerType<?> OVERSPECIALIZATION = new PowerTypeReference<>(new Identifier(ThePlaneswalker.MODID, "overspecialization"));

    public static final PowerType<?> INSOMNIAC = new PowerTypeReference<>(new Identifier(ThePlaneswalker.MODID, "insomniac"));

    public static final PowerType<?> CORRUPTED_BLOOD = new PowerTypeReference<>(new Identifier(ThePlaneswalker.MODID, "corrupted_blood"));

    public static final PowerType<?> TARNISHED_REPUTATION = new PowerTypeReference<>(new Identifier(ThePlaneswalker.MODID, "tarnished_reputation"));

    private static void register(PowerFactory<?> serializer) {
        Registry.register(ApoliRegistries.POWER_FACTORY, serializer.getSerializerId(), serializer);
    }

    public static void initialization() {

        register(new PowerFactory<>(new Identifier(ThePlaneswalker.MODID, "dimension"),

                        new SerializableData().add("dimension", SerializableDataTypes.DIMENSION),

                        data ->
                                (type, player) -> new DimensionPower(type, player, (RegistryKey<World>) data.get("dimension"))
                )
        );

        register(new PowerFactory<>(new Identifier(ThePlaneswalker.MODID, "position"), new SerializableData(),

                        data ->
                                (type, player) -> new PositionPower(type, player, BlockPos.ORIGIN)
                )
        );

        register(new PowerFactory<>(new Identifier(ThePlaneswalker.MODID, "activated_position"), new SerializableData().add("range", SerializableDataTypes.INT),

                        data ->
                                (type, player) -> new ActivatedPositionPower(type, player, BlockPos.ORIGIN, data.getInt("range"))
                ).allowCondition()
        );

        register(new PowerFactory<>(new Identifier(ThePlaneswalker.MODID, "dimension_changed"),

                        new SerializableData().add("entity_action", ApoliDataTypes.ENTITY_ACTION),

                        data ->
                                (type, player) -> new DimensionChangedPower(type, player, (Consumer<Entity>) data.get("entity_action"))
                )
        );

        register(new PowerFactory<>(new Identifier(ThePlaneswalker.MODID, "on_teleport"),

                        new SerializableData().add("entity_action", ApoliDataTypes.ENTITY_ACTION),

                        data ->
                                (type, player) -> new OnTeleportPower(type, player, (Consumer<Entity>) data.get("entity_action"))
                )
        );

        register(new PowerFactory<>(new Identifier(ThePlaneswalker.MODID, "attack_block"),

                        new SerializableData().add("entity_action", ApoliDataTypes.ENTITY_ACTION),

                        data ->
                                (type, player) -> new AttackBlockPower(type, player, (Consumer<Entity>) data.get("entity_action"))
                ).allowCondition()
        );


    }

}
