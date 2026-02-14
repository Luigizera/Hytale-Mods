package com.ludas.plugin.systems;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.DelayedEntitySystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.*;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.ludas.plugin.clazz.Perk;
import com.ludas.plugin.clazz.StrengthPerkId;
import com.ludas.plugin.components.entity.LevelComponent;
import com.ludas.plugin.components.entity.MainStatusComponent;
import com.ludas.plugin.events.GiveStrengthXPEvent;
import com.ludas.plugin.events.StrengthExtraDamageEvent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class StrengthStatusSystems {
    public static class PlayerHitNPCSystem extends DamageEventSystem {
        private float defaultXP = 1f;

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
        public void handle(int index, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
                           @NonNullDecl Store<EntityStore> store,
                           @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl Damage damage) {
            NPCEntity npcComponent = (NPCEntity) archetypeChunk.getComponent(index, NPCEntity.getComponentType());
            if (npcComponent == null) return;
            //TestPlugin.LOGGER.atInfo().log(""+damage.getCause().getId());
            if (damage.getCause() != DamageCause.PHYSICAL || damage.getCause().getInherits() != DamageCause.PHYSICAL.getId()) return;
            Damage.Source damageSource = damage.getSource();
            if (!(damageSource instanceof Damage.EntitySource)) return;
            Damage.EntitySource entitySource = (Damage.EntitySource) damageSource;
            Ref sourceRef = entitySource.getRef();
            Player attacker = store.getComponent(sourceRef, Player.getComponentType());
            if (attacker == null) return;
            Ref<EntityStore> attackerRef = attacker.getReference();
            if (attackerRef == null) return;
            MainStatusComponent mainStatus = store.getComponent(attackerRef, MainStatusComponent.getComponentType());
            if (mainStatus == null) {
                commandBuffer.putComponent(attackerRef, MainStatusComponent.getComponentType(), new MainStatusComponent());
            }
            else {
                LevelComponent npcLevel = archetypeChunk.getComponent(index, LevelComponent.getComponentType());
                if (npcLevel != null) {
                    defaultXP = (float) npcLevel.getLevel();
                }
                Ref<EntityStore> npcRef = npcComponent.getReference();
                if(npcRef == null) return;
                GiveStrengthXPEvent.dispatch(attackerRef, defaultXP);
                StrengthExtraDamageEvent.dispatch(attackerRef, npcRef, damage, commandBuffer);
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
        public void handle(int index, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
                           @NonNullDecl Store<EntityStore> store,
                           @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl Damage damage) {
            Player player = archetypeChunk.getComponent(index, Player.getComponentType());
            if(player == null) return;
            if (damage.getCause() != DamageCause.PHYSICAL || damage.getCause().getInherits() != DamageCause.PHYSICAL.getId()) return;
            Damage.Source damageSource = damage.getSource();
            if (!(damageSource instanceof Damage.EntitySource entitySource)) return;
            Ref sourceRef = entitySource.getRef();
            Player sourcePlayer = store.getComponent(sourceRef, Player.getComponentType());
            if (sourcePlayer == null) return;
            Ref<EntityStore> attacker = sourcePlayer.getReference();
            if (attacker == null) return;
            MainStatusComponent mainStatus = store.getComponent(attacker, MainStatusComponent.getComponentType());
            if (mainStatus == null) {
                commandBuffer.putComponent(attacker, MainStatusComponent.getComponentType(), new MainStatusComponent());
            }
            else {
                Ref<EntityStore> playerRef = player.getReference();
                if(playerRef == null) return;
                StrengthExtraDamageEvent.dispatch(attacker, playerRef, damage, commandBuffer);
            }
        }
    }
        /* UNNARMED
        public static class GetStrengthExpFromHittingNpcSystem extends DamageEventSystem {
            private float defaultXP = 1f;

            public GetStrengthExpFromHittingNpcSystem() {
                super();
            }

            @Nonnull
            private final Query<EntityStore> query = NPCEntity.getComponentType();

            @Nullable
            public SystemGroup<EntityStore> getGroup() {
                return DamageModule.get().getInspectDamageGroup();
            }

            @Nonnull
            public Query<EntityStore> getQuery() {
                return this.query;
            }

            @Override
            public void handle(int index, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
                               @NonNullDecl Store<EntityStore> store,
                               @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl Damage damage) {
                NPCEntity npcComponent = (NPCEntity) archetypeChunk.getComponent(index, NPCEntity.getComponentType());
                if (npcComponent == null) return;
                TestPlugin.LOGGER.atInfo().log("Damage Cause: " + damage.getCause());
                if (damage.getCause() != DamageCause.PHYSICAL) return;
                Damage.Source damageSource = damage.getSource();
                if (!(damageSource instanceof Damage.EntitySource)) return;
                Damage.EntitySource entitySource = (Damage.EntitySource) damageSource;
                Ref sourceRef = entitySource.getRef();
                Player sourcePlayer = (Player) store.getComponent(sourceRef, Player.getComponentType());
                if (sourcePlayer == null) return;
                Inventory inventory = sourcePlayer.getInventory();
                if (inventory == null) return;
                ItemStack item = inventory.getActiveHotbarItem();
                TestPlugin.LOGGER.atInfo().log("Item: " + item);
                if (item != null) return;
                Ref<EntityStore> playerRef = sourcePlayer.getReference();
                if (playerRef == null) return;
                MainStatusComponent mainStatus = store.getComponent(playerRef, MainStatusComponent.getComponentType());
                if (mainStatus == null) {
                    commandBuffer.putComponent(playerRef, MainStatusComponent.getComponentType(), new MainStatusComponent());
                } else {
                    LevelComponent npcLevel = archetypeChunk.getComponent(index, LevelComponent.getComponentType());
                    if (npcLevel != null) {
                        defaultXP = (float) npcLevel.getLevel();
                    }
                }
            }
        }
        */

    public static class PerkTick extends DelayedEntitySystem<EntityStore> {

        public PerkTick() {
            super(5f);
        }

        @NullableDecl
        @Override
        public Query<EntityStore> getQuery() {
            return Query.and(new Query[]{MainStatusComponent.getComponentType(), Player.getComponentType()});
        }

        @Override
        public void tick(float dt, int idx, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
                         @NonNullDecl Store<EntityStore> store,
                         @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
            MainStatusComponent mainStatus = archetypeChunk.getComponent(idx, MainStatusComponent.getComponentType());
            if(mainStatus == null) return;
            Player player = archetypeChunk.getComponent(idx, Player.getComponentType());
            if(player == null) return;

            for(int i = 0; i < StrengthPerkId.CURRENT_PERK_COUNT; ++i) {
                Perk perk = mainStatus.getStrength().getPerkById(i);
                if(perk == null) continue;
                if(!mainStatus.getStrength().isPerkUnlocked(i)) {
                    perk.unlockCondition(idx, archetypeChunk);
                }
            }
        }
    }
}