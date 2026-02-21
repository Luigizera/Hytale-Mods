package com.ludas.plugin.systems.effects;

import com.ludas.plugin.components.effects.DoTEffect;
import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageModule;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageSystems;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class DoTEffectSystem extends EntityTickingSystem<EntityStore> {

    public DoTEffectSystem() {
    }

    @NonNullDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(DoTEffect.getComponentType());
    }

    @NullableDecl
    @Override
    public SystemGroup<EntityStore> getGroup() {
        return DamageModule.get().getGatherDamageGroup();
    }

    @Override
    public void tick(float dt, int idx, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
                     @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        DoTEffect damageOverTime = archetypeChunk.getComponent(idx, DoTEffect.getComponentType());
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(idx);

        damageOverTime.addElapsedTime(dt);

        if (damageOverTime.getElapsedTime() >= damageOverTime.getTickInterval()) {
            damageOverTime.resetElapsedTime();

            Damage damage = new Damage(Damage.NULL_SOURCE, DamageCause.OUT_OF_WORLD, damageOverTime.getDamagePerTick());
            DamageSystems.executeDamage(ref, commandBuffer, damage);

            damageOverTime.decrementRemainingTicks();
        }

        if (damageOverTime.isExpired()) {
            commandBuffer.removeComponent(ref, DoTEffect.getComponentType());
        }
    }
}
