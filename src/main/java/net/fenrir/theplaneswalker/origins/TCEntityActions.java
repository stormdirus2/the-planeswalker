package net.fenrir.theplaneswalker.origins;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.VariableIntPower;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableData;
import net.fenrir.theplaneswalker.ThePlaneswalker;
import net.fenrir.theplaneswalker.helpers.ServerPlayerEntityInterface;
import net.fenrir.theplaneswalker.helpers.TeleportHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class TCEntityActions {

    private static void register(ActionFactory<Entity> actionFactory) {
        Registry.register(ApoliRegistries.ENTITY_ACTION, actionFactory.getSerializerId(), actionFactory);
    }


    public static void initialization() {
        register(new ActionFactory<>(new Identifier(ThePlaneswalker.MODID, "switch_dimension"), new SerializableData().add("dimension", ApoliDataTypes.POWER_TYPE),
                (data, entity) -> {
                    if (entity instanceof ServerPlayerEntity player) {
                        PowerHolderComponent component = PowerHolderComponent.KEY.get(player);
                        DimensionPower power = (DimensionPower) component.getPower((PowerType<?>) data.get("dimension"));
                        power.updateWorld((ServerWorld) player.world);
                        double focusScale = power.worldFocus.getDimension().getCoordinateScale();
                        double lastScale = power.lastWorld.getDimension().getCoordinateScale();
                        double fraction;
                        if (entity.world == power.worldFocus) {
                            fraction = focusScale / lastScale;
                            Integer level = TeleportHelper.safeSpawn(power.lastWorld, (int) (player.getBlockX() * fraction), (int) (player.getBlockZ() * fraction));
                            if (level != null) {
                                player.teleport(power.lastWorld, (int) (player.getBlockX() * fraction) + 0.5, level, (int) (player.getBlockZ() * fraction) + 0.5, player.getYaw(), player.getPitch());
                                player.fallDistance = 0;
                                player.onTeleportationDone();
                            }
                        } else {
                            fraction = lastScale / focusScale;
                            Integer level = TeleportHelper.safeSpawn(power.worldFocus, (int) (player.getBlockX() * fraction), (int) (player.getBlockZ() * fraction));
                            if (level != null) {
                                player.teleport(power.worldFocus, (int) (player.getBlockX() * fraction) + 0.5, level, (int) (player.getBlockZ() * fraction) + 0.5, player.getYaw(), player.getPitch());
                                player.fallDistance = 0;
                                player.onTeleportationDone();
                            }
                        }
                    }
                }));

        register(new ActionFactory<>(new Identifier(ThePlaneswalker.MODID, "set_position"), new SerializableData().add("position", ApoliDataTypes.POWER_TYPE),
                (data, entity) -> {
                    PowerHolderComponent component = PowerHolderComponent.KEY.get(entity);
                    PositionPower power = (PositionPower) component.getPower((PowerType<?>) data.get("position"));
                    power.pos = entity.getBlockPos();
                    PowerHolderComponent.sync(entity);
                }));
        register(new ActionFactory<>(new Identifier(ThePlaneswalker.MODID, "sync_resource_position"), new SerializableData().add("position", ApoliDataTypes.POWER_TYPE).add("resource", ApoliDataTypes.POWER_TYPE),
                (data, entity) -> {
                    PowerHolderComponent component = PowerHolderComponent.KEY.get(entity);
                    PositionPower power = (PositionPower) component.getPower((PowerType<?>) data.get("position"));

                    int distance = (int) Math.sqrt(power.pos.getSquaredDistance(entity.getBlockX(), entity.getBlockY(), entity.getBlockZ(), true));

                    VariableIntPower resource = (VariableIntPower) component.getPower((PowerType<?>) data.get("resource"));
                    resource.setValue(distance);

                    PowerHolderComponent.sync(entity);

                }));
        register(new ActionFactory<>(new Identifier(ThePlaneswalker.MODID, "set_position_block"), new SerializableData().add("position", ApoliDataTypes.POWER_TYPE),
                (data, entity) -> {
                    if (entity instanceof ServerPlayerEntity player) {
                        PowerHolderComponent component = PowerHolderComponent.KEY.get(entity);
                        PositionPower power = (PositionPower) component.getPower((PowerType<?>) data.get("position"));
                        power.pos = ((ServerPlayerEntityInterface) player).getLastInteracted();
                        PowerHolderComponent.sync(entity);
                    }
                }));
        register(new ActionFactory<>(new Identifier(ThePlaneswalker.MODID, "sync_resource_position_inverse"), new SerializableData().add("position", ApoliDataTypes.POWER_TYPE).add("resource", ApoliDataTypes.POWER_TYPE),
                (data, entity) -> {
                    PowerHolderComponent component = PowerHolderComponent.KEY.get(entity);
                    PositionPower power = (PositionPower) component.getPower((PowerType<?>) data.get("position"));

                    int distance = (int) Math.sqrt(power.pos.getSquaredDistance(entity.getBlockX(), entity.getBlockY(), entity.getBlockZ(), true));

                    VariableIntPower resource = (VariableIntPower) component.getPower((PowerType<?>) data.get("resource"));
                    resource.setValue(resource.getMax() - distance);

                    PowerHolderComponent.sync(entity);

                }));
        register(new ActionFactory<>(new Identifier(ThePlaneswalker.MODID, "teleport_to_target"), new SerializableData(),
                (data, entity) -> {
                    if (entity instanceof ServerPlayerEntity player) {
                        LivingEntity lastAttacked = ((ServerPlayerEntityInterface) player).getLastAttacked();
                        if (lastAttacked != null) {
                            player.networkHandler.requestTeleport(lastAttacked.getX(), lastAttacked.getY(), lastAttacked.getZ(), lastAttacked.getYaw(), lastAttacked.getPitch());
                            player.fallDistance = 0;
                            player.onTeleportationDone();
                        }
                    }
                }));
        register(new ActionFactory<>(new Identifier(ThePlaneswalker.MODID, "teleport_target_to_position"), new SerializableData().add("position", ApoliDataTypes.POWER_TYPE),
                (data, entity) -> {
                    if (entity instanceof ServerPlayerEntity player) {
                        PowerHolderComponent component = PowerHolderComponent.KEY.get(entity);
                        PositionPower power = (PositionPower) component.getPower((PowerType<?>) data.get("position"));
                        LivingEntity lastAttacked = ((ServerPlayerEntityInterface) player).getLastAttacked();
                        if (lastAttacked != null) {
                            lastAttacked.requestTeleport(power.pos.getX(), power.pos.getY(), power.pos.getZ());
                        }
                    }
                }));
    }


}
