package com.ludas.plugin.systems;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.component.system.tick.DelayedEntitySystem;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.*;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.ludas.plugin.TestPlugin;
import com.ludas.plugin.clazz.Perk;
import com.ludas.plugin.clazz.StrengthPerkId;
import com.ludas.plugin.components.entity.LevelComponent;
import com.ludas.plugin.components.entity.MainStatusComponent;
import com.ludas.plugin.events.GiveStrengthXPEvent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;


public class MainStatusSystems {
/*
    public static class PerkTick extends DelayedEntitySystem<EntityStore> {

        public PerkTick() {
            super(5f);
        }

        @NullableDecl
        @Override
        public Query<EntityStore> getQuery() {
            return Query.and(new Query[]{LevelComponent.getComponentType(), Player.getComponentType()});
        }

        @Override
        public void tick(float dt, int idx, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
                         @NonNullDecl Store<EntityStore> store,
                         @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
            LevelComponent level = archetypeChunk.getComponent(idx, LevelComponent.getComponentType());
            if(level == null) return;
            Player player = archetypeChunk.getComponent(idx, Player.getComponentType());
            if(player == null) return;

            for(int i = 0; i < PerkId.CURRENT_PERK_COUNT; ++i) {
                Perk perk = level.getPerkById(i);
                if(perk == null) continue;
                if(!level.isPerkUnlocked(i)) {
                    perk.unlockCondition(idx, archetypeChunk);
                }
                else {
                    if(!level.isPerkEnabled(i)) {
                        perk.removeComponents(idx, archetypeChunk, commandBuffer);
                    }
                    else{
                        perk.tick(dt, idx, archetypeChunk, store, commandBuffer);
                    }
                }
            }
        }
    }*/

    public static class PlayerSpawnSystem extends RefSystem<EntityStore> {
        public PlayerSpawnSystem() {
        }

        @NonNullDecl
        @Override
        public Query<EntityStore> getQuery() {
            return Archetype.of(PlayerRef.getComponentType());
        }

        @Override
        public void onEntityAdded(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl AddReason addReason,
                                  @NonNullDecl Store<EntityStore> store,
                                  @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
            if (addReason != AddReason.LOAD) return;
            PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
            if (playerRef == null) return;

            var component = MainStatusComponent.getComponentType();
            MainStatusComponent status = store.getComponent(ref, component);
            if (status == null) {
                status = new MainStatusComponent();
                commandBuffer.putComponent(ref, component, status);
                playerRef.sendMessage(Message.raw("Adicionado sistema de Status").color(Color.ORANGE).bold(true));
            } else {
                playerRef.sendMessage(Message.raw("Strength Level: %d (%.2f XP)".formatted(
                                status.getStrength().getLevelComponent().getLevel(),
                                status.getStrength().getLevelComponent().getCurrentExperience()))
                        .color(Color.ORANGE).bold(true));
            }
        }

        @Override
        public void onEntityRemove(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl RemoveReason removeReason, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {

        }
    }

    public static class StrengthStatusSystems {

        public static class PlayerHitNPCSystem extends DamageEventSystem {
            private float defaultXP = 1f;

            public PlayerHitNPCSystem() {
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
                if (damage.getCause() != DamageCause.PHYSICAL) return;
                Damage.Source damageSource = damage.getSource();
                if (!(damageSource instanceof Damage.EntitySource)) return;
                Damage.EntitySource entitySource = (Damage.EntitySource) damageSource;
                Ref sourceRef = entitySource.getRef();
                Player sourcePlayer = store.getComponent(sourceRef, Player.getComponentType());
                if (sourcePlayer == null) return;
                Ref<EntityStore> playerRef = sourcePlayer.getReference();
                if (playerRef == null) return;
                MainStatusComponent mainStatus = store.getComponent(playerRef, MainStatusComponent.getComponentType());
                if (mainStatus == null) {
                    commandBuffer.putComponent(playerRef, MainStatusComponent.getComponentType(), new MainStatusComponent());
                }
                else {
                    LevelComponent npcLevel = archetypeChunk.getComponent(index, LevelComponent.getComponentType());
                    if (npcLevel != null) {
                        defaultXP = (float) npcLevel.getLevel();
                    }
                    float dmg = damage.getAmount() * mainStatus.getStrength().getMultiplier();
                    Damage strExtraDamage = new Damage(damageSource, DamageCause.OUT_OF_WORLD, dmg);
                    Ref<EntityStore> npcRef = npcComponent.getReference();
                    if(npcRef == null) return;
                    DamageSystems.executeDamage(npcRef, commandBuffer, strExtraDamage);
                    GiveStrengthXPEvent.dispatch(playerRef, defaultXP / 10f);
                }
            }
        }

        public static class PlayerHitPlayerSystem extends DamageEventSystem {
            public PlayerHitPlayerSystem() {
                super();
            }

            @Nonnull
            private final Query<EntityStore> query = Player.getComponentType();

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
                Player player = archetypeChunk.getComponent(index, Player.getComponentType());
                if(player == null) return;
                if (damage.getCause() != DamageCause.PHYSICAL) return;
                Damage.Source damageSource = damage.getSource();
                if (!(damageSource instanceof Damage.EntitySource entitySource)) return;
                Ref sourceRef = entitySource.getRef();
                Player sourceAttacker = store.getComponent(sourceRef, Player.getComponentType());
                if (sourceAttacker == null) return;
                Ref<EntityStore> attacker = sourceAttacker.getReference();
                if (attacker == null) return;
                MainStatusComponent mainStatus = store.getComponent(attacker, MainStatusComponent.getComponentType());
                if (mainStatus == null) {
                    commandBuffer.putComponent(attacker, MainStatusComponent.getComponentType(), new MainStatusComponent());
                }
                else {
                    float dmg = damage.getAmount() * mainStatus.getStrength().getMultiplier();
                    Damage strExtraDamage = new Damage(damageSource, DamageCause.OUT_OF_WORLD, dmg);
                    DamageSystems.executeDamage(player.getReference(), commandBuffer, strExtraDamage);
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
}

