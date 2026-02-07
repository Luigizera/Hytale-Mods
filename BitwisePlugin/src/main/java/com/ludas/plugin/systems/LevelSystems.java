package com.ludas.plugin.systems;

import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.protocol.packets.inventory.SetActiveSlot;
import com.hypixel.hytale.server.core.io.PacketHandler;
import com.ludas.plugin.TestPlugin;
import com.ludas.plugin.clazz.Perk;
import com.ludas.plugin.clazz.PerkId;
import com.ludas.plugin.components.LevelComponent;
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
import java.util.List;
import java.util.Random;


public class LevelSystems {

    public static class PerkTick extends EntityTickingSystem<EntityStore> {
        float limiter = 0f;
        @NullableDecl
        @Override
        public Query<EntityStore> getQuery() {
            return Query.and(new Query[]{LevelComponent.getComponentType(), Player.getComponentType()});
        }

        @Override
        public void tick(float dt, int idx, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
                         @NonNullDecl Store<EntityStore> store,
                         @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
            limiter += dt;
            if (limiter >= 5f) {
                limiter = 0;
                LevelComponent level = archetypeChunk.getComponent(idx, LevelComponent.getComponentType());
                if(level == null) return;
                Player player = archetypeChunk.getComponent(idx, Player.getComponentType());
                if(player == null) return;

                TestPlugin.LOGGER.atInfo().log("Perk id values: " +PerkId.values().length);
                for(int i = 0; i < PerkId.values().length-1; ++i) {
                    if(!level.isPerkUnlocked(i)) {
                        Perk perk = level.getPerkById(i);
                        if(perk != null) perk.unlockCondition(dt, idx, archetypeChunk, store, commandBuffer);
                    }
                    else {
                        if(!level.isPerkEnabled(i)) continue;
                        Perk perk = level.getPerkById(i);
                        if(perk != null) perk.tick(dt, idx, archetypeChunk, store, commandBuffer);
                    }
                }
            }
        }
    }

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
            PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
            if(playerRef == null) return;
            PacketHandler playerConnection = playerRef.getPacketHandler();
            //playerConnection.writeNoCache(new SetActiveSlot(-5, playerInventory.getActiveUtilitySlot()));

            var component = LevelComponent.getComponentType();
            LevelComponent level = store.getComponent(ref, component);
            if(level == null) {
                level = new LevelComponent();
                commandBuffer.putComponent(ref, component, level);
                playerRef.sendMessage(Message.raw("Adicionado sistema de Nível").color(Color.ORANGE).bold(true));
            }
            else {
                playerRef.sendMessage(Message.raw("Level: %d (%.2f XP)".formatted(
                                level.getLevel(), level.getCurrentExperience()))
                        .color(Color.ORANGE).bold(true));

                /*for(int i = 0; i < PerkId.values().length; ++i) {
                        if(!level.isPerkUnlocked(i)) continue;
                        if(!level.isPerkEnabled(i)) continue;
                        Perk perk = level.getPerkById(i);
                        perk.tick(dt, idx, archetypeChunk, store, commandBuffer);
                }*/
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
            Damage.Source damageSource = deathInfo.getSource();
            if (!(damageSource instanceof Damage.EntitySource)) return;
            Damage.EntitySource entitySource = (Damage.EntitySource) damageSource;
            Ref sourceRef = entitySource.getRef();
            Player sourcePlayer = (Player) store.getComponent(sourceRef, Player.getComponentType());
            if(sourcePlayer == null) return;
            Ref<EntityStore> playerRef = sourcePlayer.getReference();
            if(playerRef == null) return;
            LevelComponent level = store.getComponent(playerRef, LevelComponent.getComponentType());
            if(level == null) {
                commandBuffer.putComponent(playerRef, LevelComponent.getComponentType(), new LevelComponent());
            }
            else {
                LevelComponent npcLevel = commandBuffer.getComponent(ref, LevelComponent.getComponentType());
                if(npcLevel != null) {
                    defaultXP = (float) npcLevel.getLevel();
                }
                GiveXPEvent.dispatch(playerRef, defaultXP);
            }
        }
    }
}

