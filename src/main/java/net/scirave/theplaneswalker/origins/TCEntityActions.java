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

package net.scirave.theplaneswalker.origins;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.VariableIntPower;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.util.math.BlockPos;
import net.scirave.theplaneswalker.ThePlaneswalker;
import net.scirave.theplaneswalker.helpers.ServerPlayerEntityInterface;
import net.scirave.theplaneswalker.helpers.TeleportHelper;
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
        register(new ActionFactory<>(new Identifier(ThePlaneswalker.MODID, "switch_dimension"), new SerializableData().add("dimension", ApoliDataTypes.POWER_TYPE).add("position", ApoliDataTypes.POWER_TYPE),
                (data, entity) -> {
                    if (entity instanceof ServerPlayerEntity player) {
                        PowerHolderComponent component = PowerHolderComponent.KEY.get(player);
                        DimensionPower power = (DimensionPower) component.getPower((PowerType<?>) data.get("dimension"));
                        power.updateWorld((ServerWorld) player.world);
                        PositionPower position = (PositionPower) component.getPower((PowerType<?>) data.get("position"));
                        BlockPos pos = position.pos;
                        power.updateWorld((ServerWorld) player.world);
                        double focusScale = power.worldFocus.getDimension().getCoordinateScale();
                        double lastScale = power.lastWorld.getDimension().getCoordinateScale();
                        double fraction;
                        if (entity.world == power.worldFocus) {
                            fraction = focusScale / lastScale;
                            Integer level = TeleportHelper.safeSpawn(power.lastWorld, (int) (pos.getX() * fraction), (int) (pos.getZ() * fraction));
                            if (level != null) {
                                player.teleport(power.lastWorld, (int) (pos.getX() * fraction) + 0.5, level, (int) (pos.getZ() * fraction) + 0.5, player.getYaw(), player.getPitch());
                                player.fallDistance = 0;
                                player.onTeleportationDone();
                            }
                        } else {
                            fraction = lastScale / focusScale;
                            Integer level = TeleportHelper.safeSpawn(power.worldFocus, (int) (pos.getX() * fraction), (int) (pos.getZ() * fraction));
                            if (level != null) {
                                player.teleport(power.worldFocus, (int) (pos.getX() * fraction) + 0.5, level, (int) (pos.getZ() * fraction) + 0.5, player.getYaw(), player.getPitch());
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
