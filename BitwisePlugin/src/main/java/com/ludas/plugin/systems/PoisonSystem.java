package com.ludas.plugin.systems;

import com.ludas.plugin.components.PoisonComponent;
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

public class PoisonSystem extends EntityTickingSystem<EntityStore> {

    private final ComponentType<EntityStore, PoisonComponent> poisonComponentType;

    public PoisonSystem(ComponentType<EntityStore, PoisonComponent> poisonComponentType) {
        this.poisonComponentType = poisonComponentType;
    }

    @Override
    public void tick(float dt, int idx, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
                     @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        PoisonComponent poison = archetypeChunk.getComponent(idx, poisonComponentType);
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(idx);

        poison.addElapsedTime(dt);

        if (poison.getElapsedTime() >= poison.getTickInterval()) {
            poison.resetElapsedTime();

            Damage damage = new Damage(Damage.NULL_SOURCE, DamageCause.OUT_OF_WORLD, poison.getDamagePerTick());
            DamageSystems.executeDamage(ref, commandBuffer, damage);

            poison.decrementRemainingTicks();
        }

        if (poison.isExpired()) {
            commandBuffer.removeComponent(ref, poisonComponentType);
        }
    }

    @NullableDecl
    @Override
    public SystemGroup<EntityStore> getGroup() {
        return DamageModule.get().getGatherDamageGroup();
    }

    @NonNullDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(this.poisonComponentType);
    }
}
