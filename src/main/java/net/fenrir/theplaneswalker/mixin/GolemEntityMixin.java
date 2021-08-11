package net.fenrir.theplaneswalker.mixin;

import net.fenrir.theplaneswalker.origins.TCPowers;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GolemEntity.class)
public abstract class GolemEntityMixin extends MobEntity {

    protected GolemEntityMixin(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void addGoals(EntityType<? extends MobEntity> entityType, World world, CallbackInfo ci) {
        if (!this.world.isClient()) {
            this.targetSelector.add(3, new FollowTargetGoal<>((GolemEntity) (Object) this, PlayerEntity.class, 10, true, true, (plr) -> TCPowers.TARNISHED_REPUTATION.isActive(plr) && !(plr).hasStatusEffect(StatusEffects.HERO_OF_THE_VILLAGE)));
        }
    }

}
