package com.ludas.plugin.systems.effects;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.*;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.ludas.plugin.components.effects.FrenzyEffect;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FrenzyEffectSystems {

    public static class PlayerHitNPCSystem extends DamageEventSystem {

        public PlayerHitNPCSystem() {
            super();
        }

        @Nullable
        public SystemGroup<EntityStore> getGroup() {
            return DamageModule.get().getInspectDamageGroup();
        }

        @Nonnull
        public Query<EntityStore> getQuery() {
            return Query.and(NPCEntity.getComponentType());
        }

        @Override
        public void handle(int idx, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
                           @NonNullDecl Store<EntityStore> store,
                           @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl Damage damage) {
            NPCEntity target = archetypeChunk.getComponent(idx, NPCEntity.getComponentType());
            if (target == null) return;
            if (damage.getAmount() <= 0) return;
            Ref<EntityStore> targetRef = target.getReference();
            if (targetRef == null) return;
            Damage.Source damageSource = damage.getSource();
            if (!(damageSource instanceof Damage.EntitySource entitySource)) return;
            Ref<EntityStore> attackerRef = entitySource.getRef();
            Player attacker = store.getComponent(attackerRef, Player.getComponentType());
            if (attacker == null) return;
            FrenzyEffect frenzyEffect = store.getComponent(attackerRef, FrenzyEffect.getComponentType());
            if(frenzyEffect == null) return;
            EntityStatMap attackerStatMap = store.getComponent(attackerRef, EntityStatMap.getComponentType());
            if (attackerStatMap == null) return;
            EntityStatValue healthStat = attackerStatMap.get(DefaultEntityStatTypes.getHealth());
            if (healthStat == null) return;
            if (frenzyEffect.getMultiplier() > healthStat.asPercentage()) return;

            float healthAsDamage = healthStat.getMax() * frenzyEffect.getMultiplier();
            Damage extraDmg = new Damage(Damage.NULL_SOURCE, DamageCause.OUT_OF_WORLD, healthAsDamage);
            DamageSystems.executeDamage(targetRef, commandBuffer, extraDmg);
            DamageSystems.executeDamage(attackerRef, commandBuffer, extraDmg);
        }

    }

    public static class PlayerHitPlayerSystem extends DamageEventSystem {
        public PlayerHitPlayerSystem() {
            super();
        }

        @Nullable
        public SystemGroup<EntityStore> getGroup() {
            return DamageModule.get().getInspectDamageGroup();
        }

        @Nonnull
        public Query<EntityStore> getQuery() {
            return Query.and(Player.getComponentType());
        }

        @Override
        public void handle(int idx, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
                           @NonNullDecl Store<EntityStore> store,
                           @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl Damage damage) {
            Player target = archetypeChunk.getComponent(idx, Player.getComponentType());
            if (target == null) return;
            if(damage.getAmount() <= 0) return;
            Ref<EntityStore> targetRef = target.getReference();
            if(targetRef == null) return;
            Damage.Source damageSource = damage.getSource();
            if (!(damageSource instanceof Damage.EntitySource entitySource)) return;
            Ref<EntityStore> attackerRef = entitySource.getRef();
            Player attacker = store.getComponent(attackerRef, Player.getComponentType());
            if (attacker == null) return;
            FrenzyEffect frenzyEffect = store.getComponent(attackerRef, FrenzyEffect.getComponentType());
            if(frenzyEffect == null) return;
            EntityStatMap attackerStatMap = store.getComponent(attackerRef, EntityStatMap.getComponentType());
            if(attackerStatMap == null) return;
            EntityStatValue healthStat = attackerStatMap.get(DefaultEntityStatTypes.getHealth());
            if(healthStat == null) return;
            if(frenzyEffect.getMultiplier() > healthStat.asPercentage()) return;

            float healthAsDamage =  healthStat.getMax() * frenzyEffect.getMultiplier();
            Damage extraDmg = new Damage(Damage.NULL_SOURCE, DamageCause.OUT_OF_WORLD, healthAsDamage);
            DamageSystems.executeDamage(targetRef, commandBuffer, extraDmg);
            DamageSystems.executeDamage(attackerRef, commandBuffer, extraDmg);
        }
    }
}
