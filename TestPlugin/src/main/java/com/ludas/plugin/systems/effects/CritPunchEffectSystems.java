package com.ludas.plugin.systems.effects;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.damage.*;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.ludas.plugin.clazz.Config;
import com.ludas.plugin.components.effects.CritPunchEffect;
import com.ludas.plugin.components.effects.FrenzyEffect;
import com.ludas.plugin.events.damage.AgilityCritDamageEvent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CritPunchEffectSystems {

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
            CritPunchEffect critPunchEffect = store.getComponent(attackerRef, CritPunchEffect.getComponentType());
            if(critPunchEffect == null) return;
            Inventory inventory = attacker.getInventory();
            if (inventory == null) return;
            ItemStack itemStack = inventory.getActiveHotbarItem();
            if(itemStack == null && Config.isDamageCausePhysical(damage.getCause())) {
                AgilityCritDamageEvent.dispatch(attackerRef, targetRef, damage, commandBuffer);
            }
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
            CritPunchEffect critPunchEffect = store.getComponent(attackerRef, CritPunchEffect.getComponentType());
            if(critPunchEffect == null) return;
            Inventory inventory = attacker.getInventory();
            if (inventory == null) return;
            ItemStack itemStack = inventory.getActiveHotbarItem();
            if(itemStack == null && Config.isDamageCausePhysical(damage.getCause())) {
                AgilityCritDamageEvent.dispatch(attackerRef, targetRef, damage, commandBuffer);
            }
        }
    }
}
