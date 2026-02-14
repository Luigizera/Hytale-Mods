package com.ludas.plugin.systems;


import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.component.system.HolderSystem;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.component.system.tick.DelayedEntitySystem;
import com.hypixel.hytale.protocol.ColorLight;
import com.hypixel.hytale.server.core.asset.type.particle.config.WorldParticle;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.DynamicLight;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.*;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
import com.hypixel.hytale.server.core.universe.world.ParticleUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.ludas.plugin.TestPlugin;
import com.ludas.plugin.clazz.Perk;
import com.ludas.plugin.clazz.StrengthPerkId;
import com.ludas.plugin.components.entity.LevelComponent;
import com.ludas.plugin.components.entity.MainStatusComponent;
import com.ludas.plugin.handlers.StrengthExtraDamageHandler;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.crypto.dsig.Transform;
import java.util.Random;

public class NPCLevelSystems {
    public static final String MODIFIER_NAME = "LudasLevelModifier";
    private static final ColorLight LIGHT_DANGEROUS = new ColorLight((byte)0, (byte)8, (byte)0, (byte)0);
    private static final ColorLight LIGHT_NEUTRAL = new ColorLight((byte)0, (byte)5, (byte)5, (byte)0);
    private static final ColorLight LIGHT_WEAK = new ColorLight((byte)0, (byte)0, (byte)0, (byte)5);

    public static class NPCSpawnSystem extends RefSystem<EntityStore> {

        public NPCSpawnSystem() {
        }

        @NonNullDecl
        @Override
        public Query<EntityStore> getQuery() {
            return Query.and(NPCEntity.getComponentType(), Query.not(LevelComponent.getComponentType()));
        }

        @Override
        public void onEntityAdded(@NonNullDecl Ref<EntityStore> ref,
                                  @NonNullDecl AddReason addReason,
                                  @NonNullDecl Store<EntityStore> store,
                                  @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
            NPCEntity npc = store.getComponent(ref, NPCEntity.getComponentType());
            if(npc == null) return;
            EntityStatMap statMap = store.getComponent(ref, EntityStatMap.getComponentType());
            if(statMap == null) return;
            LevelComponent level = store.getComponent(ref, LevelComponent.getComponentType());
            if(level == null) {
                int randLevel = new Random().nextInt(1, 100);
                commandBuffer.putComponent(ref, LevelComponent.getComponentType(), new LevelComponent(randLevel));
                int healthIndex = DefaultEntityStatTypes.getHealth();
                EntityStatValue entityHealth = statMap.get(healthIndex);
                if (entityHealth == null) return;
                float additive = (randLevel * entityHealth.get()) / 10f;
                statMap.putModifier(healthIndex, MODIFIER_NAME,
                        new StaticModifier(
                                Modifier.ModifierTarget.MAX,
                                StaticModifier.CalculationType.ADDITIVE,
                                additive));
                statMap.addStatValue(healthIndex, additive);
                DynamicLight dynamicLight = store.getComponent(ref, DynamicLight.getComponentType());
                if(dynamicLight != null) return;
                if (randLevel >= 90) {
                    commandBuffer.putComponent(ref, DynamicLight.getComponentType(), new DynamicLight(LIGHT_DANGEROUS));
                }
                else if (randLevel >= 50) {
                    commandBuffer.putComponent(ref, DynamicLight.getComponentType(), new DynamicLight(LIGHT_NEUTRAL));
                }
                else if (randLevel >= 35) {
                    commandBuffer.putComponent(ref, DynamicLight.getComponentType(), new DynamicLight(LIGHT_WEAK));
                }
            }
        }

        @Override
        public void onEntityRemove(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl RemoveReason removeReason, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {

        }
    }

    public static class NPCDamageDealtSystem extends DamageEventSystem {
        public NPCDamageDealtSystem() {
        }

        @Nullable
        @Override
        public SystemGroup<EntityStore> getGroup() {
            return DamageModule.get().getInspectDamageGroup();
        }

        @Nonnull
        @Override
        public Query<EntityStore> getQuery() {
            return Archetype.empty();
        }

        public void handle(int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
                           @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer,
                           @Nonnull Damage damage) {
            if (damage.getSource() instanceof Damage.EntitySource entitySource) {
                Ref<EntityStore> sourceRef = entitySource.getRef();
                if (!sourceRef.isValid()) return;
                NPCEntity sourceNpcComponent = commandBuffer.getComponent(sourceRef, NPCEntity.getComponentType());
                if (sourceNpcComponent == null) return;
                LevelComponent level = commandBuffer.getComponent(sourceRef, LevelComponent.getComponentType());
                if(level != null && damage.getSource() != DamageCause.OUT_OF_WORLD) {
                    float dmg = damage.getAmount() * (level.getLevel() / 10f);
                    Damage levelExtraDamage = new Damage(damage.getSource(), DamageCause.OUT_OF_WORLD, dmg);
                    DamageSystems.executeDamage(archetypeChunk.getReferenceTo(index), commandBuffer, levelExtraDamage);
                }
            }
        }
    }

    public static class NPCEffect extends RefSystem<EntityStore> {

        public NPCEffect() {
            super();
        }
        @NullableDecl
        @Override
        public Query<EntityStore> getQuery() {
            return Query.and(LevelComponent.getComponentType(), NPCEntity.getComponentType());
        }

        @Override
        public void onEntityAdded(@NonNullDecl Ref<EntityStore> ref,
                                  @NonNullDecl AddReason addReason,
                                  @NonNullDecl Store<EntityStore> store,
                                  @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
            if(addReason != AddReason.LOAD) return;

            NPCEntity npc = store.getComponent(ref, NPCEntity.getComponentType());
            if(npc == null) return;
            LevelComponent level = store.getComponent(ref, LevelComponent.getComponentType());
            if(level == null) return;
            int npcLevel = level.getLevel();
            DynamicLight dynamicLight = store.getComponent(ref, DynamicLight.getComponentType());
            if(dynamicLight != null) return;
            if (npcLevel >= 80) {
                commandBuffer.putComponent(ref, DynamicLight.getComponentType(), new DynamicLight(LIGHT_DANGEROUS));
            }
            else if (npcLevel >= 50) {
                commandBuffer.putComponent(ref, DynamicLight.getComponentType(), new DynamicLight(LIGHT_NEUTRAL));
            }
            else if (npcLevel >= 35) {
                commandBuffer.putComponent(ref, DynamicLight.getComponentType(), new DynamicLight(LIGHT_WEAK));
            }
        }

        @Override
        public void onEntityRemove(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl RemoveReason removeReason, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {

        }
    }
}

