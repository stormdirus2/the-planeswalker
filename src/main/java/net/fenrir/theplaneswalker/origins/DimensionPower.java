package net.fenrir.theplaneswalker.origins;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class DimensionPower extends Power {

    public ServerWorld worldFocus;

    public ServerWorld lastWorld;

    public DimensionPower(PowerType<?> type, LivingEntity entity, RegistryKey<World> key) {
        super(type, entity);
        if (entity.world.isClient) {
            return;
        }
        MinecraftServer server = entity.world.getServer();
        if (server != null) {
            worldFocus = server.getWorld(key);
        }
        updateWorld((ServerWorld) entity.world);
    }

    public void updateWorld(ServerWorld world) {
        if (world != worldFocus) {
            lastWorld = world;
        }
    }

    @Override
    public NbtElement toTag() {
        return NbtString.of(lastWorld.getRegistryKey().getValue().toString());
    }

    @Override
    public void fromTag(NbtElement tag) {
        RegistryKey<World> key = RegistryKey.of(Registry.WORLD_KEY, new Identifier(tag.asString()));
        if (key != null) {
            MinecraftServer server = entity.world.getServer();
            if (server != null) {
                updateWorld(server.getWorld(key));
            }
        }
    }

}
