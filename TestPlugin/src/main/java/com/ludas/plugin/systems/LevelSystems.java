package com.ludas.plugin.systems;

import com.ludas.plugin.TestPlugin;
import com.ludas.plugin.components.LevelComponent;
import com.ludas.plugin.events.AddLevelSystemEvent;
import com.ludas.plugin.events.GiveXPEvent;
import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.HolderSystem;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.awt.*;
import java.util.Random;


public class LevelSystems {

    public static class NPCSpawnSystem extends HolderSystem<EntityStore> {

        public NPCSpawnSystem() {
        }

        @NonNullDecl
        @Override
        public Query<EntityStore> getQuery() {
            return Query.and(new Query[]{NPCEntity.getComponentType(), Query.not(LevelComponent.getComponentType())});
        }

        @Override
        public void onEntityAdd(@NonNullDecl Holder<EntityStore> holder,
                                @NonNullDecl AddReason addReason, @NonNullDecl Store<EntityStore> store) {
            NPCEntity npc = holder.getComponent(NPCEntity.getComponentType());
            if (npc == null) return;
            LevelComponent level = holder.getComponent(LevelComponent.getComponentType());
            if(level == null) {
                int rand = new Random().nextInt(1, 100);
                holder.putComponent(LevelComponent.getComponentType(), new LevelComponent(rand));
                level = holder.getComponent(LevelComponent.getComponentType());
                TestPlugin.LOGGER.atInfo().log(npc.getRoleName() + " " + level.toString());
            }

        }

        @Override
        public void onEntityRemoved(@NonNullDecl Holder<EntityStore> holder, @NonNullDecl RemoveReason removeReason,
                                    @NonNullDecl Store<EntityStore> store) {

        }
    }

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

            Player player = store.getComponent(ref, Player.getComponentType());
            if(player == null) return;
            AddLevelSystemEvent.dispatch(player.getReference());
            LevelComponent level = store.getComponent(ref, LevelComponent.getComponentType());

            if (level != null) {
                player.sendMessage(
                        Message.raw("Level: %d (%.2f XP)".formatted(level.getLevel(), level.getCurrentExperience()))
                                .color(Color.ORANGE).bold(true));
            }
        }

        @Override
        public void onEntityRemove(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl RemoveReason removeReason, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {

        }
    }


    public static class GetExpFromNpcSystem extends DeathSystems.OnDeathSystem {
        private float defaultXP = 1f;

        public GetExpFromNpcSystem() {
            super();
        }

        @NullableDecl
        @Override
        public Query<EntityStore> getQuery() {
            return Query.and(new Query[]{NPCEntity.getComponentType(), Archetype.of(DeathComponent.getComponentType())});
        }

        @Override
        public void onComponentAdded(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl DeathComponent component,
                                     @NonNullDecl Store<EntityStore> store,
                                     @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
            NPCEntity npcComponent = (NPCEntity)commandBuffer.getComponent(ref, NPCEntity.getComponentType());
            if (npcComponent == null) return;
            Damage deathInfo = component.getDeathInfo();
            if(deathInfo == null) return;
            DamageCause deathCause = component.getDeathCause();
            if(!(deathCause == DamageCause.PHYSICAL || deathCause == DamageCause.PROJECTILE)) return;
            Damage.Source damageSource = deathInfo.getSource();
            if (!(damageSource instanceof Damage.EntitySource)) return;
            Damage.EntitySource entitySource = (Damage.EntitySource) damageSource;
            Ref sourceRef = entitySource.getRef();
            Player sourcePlayer = (Player) store.getComponent(sourceRef, Player.getComponentType());
            Ref<EntityStore> playerRef = sourcePlayer.getReference();
            if(playerRef == null) return;
            AddLevelSystemEvent.dispatch(playerRef);
            LevelComponent npcLevel = commandBuffer.getComponent(ref, LevelComponent.getComponentType());
            if(npcLevel != null) defaultXP = (float) npcLevel.getLevel();
            GiveXPEvent.dispatch(playerRef, defaultXP);
        }
    }
}

