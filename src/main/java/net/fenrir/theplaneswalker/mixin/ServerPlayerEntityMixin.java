package net.fenrir.theplaneswalker.mixin;

import com.mojang.authlib.GameProfile;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.fenrir.theplaneswalker.helpers.ServerPlayerEntityInterface;
import net.fenrir.theplaneswalker.origins.DimensionChangedPower;
import net.fenrir.theplaneswalker.origins.TCPowers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements ServerPlayerEntityInterface {

    private LivingEntity lastAttacked;

    private BlockPos lastInteracted;

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    @Inject(method = "worldChanged", at = @At("HEAD"))
    public void worldChanged(ServerWorld world, CallbackInfo ci) {
        PowerHolderComponent.getPowers((ServerPlayerEntity) (Object) this, DimensionChangedPower.class).forEach(DimensionChangedPower::onChange);
    }

    @Inject(method = "sleep", at = @At("HEAD"), cancellable = true)
    public void noTrueRest(BlockPos pos, CallbackInfo ci) {
        if (TCPowers.INSOMNIAC.isActive((ServerPlayerEntity) (Object) this)) {
            super.sleep(pos);
            ci.cancel();
        }
    }

    @Override
    public LivingEntity getLastAttacked() {
        return lastAttacked;
    }

    @Override
    public void setLastAttacked(LivingEntity entity) {
        lastAttacked = entity;
    }

    @Override
    public BlockPos getLastInteracted() {
        return lastInteracted;
    }

    @Override
    public void setLastInteracted(BlockPos pos) {
        lastInteracted = pos;
    }

}
