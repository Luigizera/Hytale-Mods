package com.ludas.plugin.systems;

import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.ludas.plugin.TestPlugin;
import com.ludas.plugin.clazz.*;
import com.ludas.plugin.events.GiveXPEvent;
import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.HolderSystem;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.ludas.plugin.perks.PoisonPerk;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.awt.*;
import java.util.Map;


public class MagnumOpusSystems {

    public static class PerkTick extends EntityTickingSystem<EntityStore> {
        private float limiter = 0f;

        @NullableDecl
        @Override
        public Query<EntityStore> getQuery() {
            return Query.and(new Query[]{MagnumOpus.getComponentType(), Player.getComponentType()});
        }

        @Override
        public void tick(float dt, int idx, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
                         @NonNullDecl Store<EntityStore> store,
                         @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
            limiter += dt;
            if (limiter >= 30f) {
                limiter = 0;
                MagnumOpus magnumOpus = archetypeChunk.getComponent(idx, MagnumOpus.getComponentType());
                if (magnumOpus == null) return;
                Player player = archetypeChunk.getComponent(idx, Player.getComponentType());
                if (player == null) return;
                TestPlugin.LOGGER.atInfo().log(magnumOpus.toString());
                Status status = magnumOpus.getStat(MagnumOpusStatTypes.UNKNOWN.id);
                TestPlugin.LOGGER.atInfo().log(status.toString());
                java.util.List<Perk> perks = status.getPerksAsList();
                TestPlugin.LOGGER.atInfo().log(String.valueOf(perks.size()));
                if (perks == null || perks.isEmpty()) return;
                for (Perk perk : perks) {
                    if (!perk.isUnlocked()) {
                        perk.unlockCondition(dt, idx, archetypeChunk, store, commandBuffer);
                    } else {
                        if (!perk.isEnabled()) continue;
                        perk.tick(dt, idx, archetypeChunk, store, commandBuffer);
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
            return Query.and(new Query[]{NPCEntity.getComponentType(), Query.not(MagnumOpus.getComponentType())});
        }

        @Override
        public void onEntityAdd(@NonNullDecl Holder<EntityStore> holder,
                                @NonNullDecl AddReason addReason, @NonNullDecl Store<EntityStore> store) {
            NPCEntity npc = holder.getComponent(NPCEntity.getComponentType());
            if (npc == null) return;
            /*MagnumOpus magnumOpus = holder.getComponent(MagnumOpus.getComponentType());
            if(magnumOpus == null) {
                int rand = new Random().nextInt(1, 100);
                holder.putComponent(MagnumOpus.getComponentType(), new MagnumOpus());
            }*/
        }

        @Override
        public void onEntityRemoved(@NonNullDecl Holder<EntityStore> holder, @NonNullDecl RemoveReason removeReason,
                                    @NonNullDecl Store<EntityStore> store) {

        }
    }

    public static class PlayerSpawnSystem extends HolderSystem<EntityStore> {
        public PlayerSpawnSystem() {
        }

        @NonNullDecl
        @Override
        public Query<EntityStore> getQuery() {
            return Query.and(new Query[]{PlayerRef.getComponentType(), Query.not(MagnumOpus.getComponentType())});
        }


        @Override
        public void onEntityAdd(@NonNullDecl Holder<EntityStore> holder,
                                @NonNullDecl AddReason addReason,
                                @NonNullDecl Store<EntityStore> store) {
            if (addReason != AddReason.LOAD) return;

            PlayerRef player = holder.getComponent(PlayerRef.getComponentType());
            if (player == null) return;
            MagnumOpus magnumOpus = holder.getComponent(MagnumOpus.getComponentType());
            if (magnumOpus == null) {
                magnumOpus = new MagnumOpus();
                TestPlugin.LOGGER.atInfo().log("unknown magnumopus: "+ magnumOpus);
                UnknownStatus unknownStatus = new UnknownStatus();
                TestPlugin.LOGGER.atInfo().log("unknown created: "+ unknownStatus);
                unknownStatus.registerPerks();
                TestPlugin.LOGGER.atInfo().log("unknown register: "+ unknownStatus);
                Map<String, Status> s = magnumOpus.putStatus(unknownStatus);
                TestPlugin.LOGGER.atInfo().log("put: " +s.values());
                holder.putComponent(MagnumOpus.getComponentType(), magnumOpus);
                player.sendMessage(Message.raw("Adicionado sistema Magnum Opus").color(Color.ORANGE).bold(true));
            }
            else  {
                for(int i = 0; i < 1 /*trocar*/; ++i) {
                    Status status = new Status(MagnumOpusStatTypes.fromValue(i).toString());
                    status.registerPerks();
                    magnumOpus.putStatus(status);
                }
                player.sendMessage(Message.raw(magnumOpus.getStat("Unknown").toString()).color(Color.ORANGE).bold(true));
            }
        }

        @Override
        public void onEntityRemoved(@NonNullDecl Holder<EntityStore> holder,
                                    @NonNullDecl RemoveReason removeReason,
                                    @NonNullDecl Store<EntityStore> store) {
        }
    }

    public static class GetExpFromNpcSystem extends DeathSystems.OnDeathSystem {
        private float defaultXP = 10f;

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
            NPCEntity npcComponent = (NPCEntity) commandBuffer.getComponent(ref, NPCEntity.getComponentType());
            if (npcComponent == null) return;
            Damage deathInfo = component.getDeathInfo();
            if (deathInfo == null) return;
            DamageCause deathCause = component.getDeathCause();
            if (!(deathCause == DamageCause.PHYSICAL || deathCause == DamageCause.PROJECTILE)) return;
            Damage.Source damageSource = deathInfo.getSource();
            if (!(damageSource instanceof Damage.EntitySource entitySource)) return;
            Ref<EntityStore> sourceRef = entitySource.getRef();
            Player sourcePlayer = store.getComponent(sourceRef, Player.getComponentType());
            if (sourcePlayer == null) return;
            Ref<EntityStore> playerRef = sourcePlayer.getReference();
            if (playerRef == null) return;
            MagnumOpus magnumOpus = store.getComponent(playerRef, MagnumOpus.getComponentType());
            if (magnumOpus == null) {
                commandBuffer.putComponent(playerRef, MagnumOpus.getComponentType(), new MagnumOpus());
            } else {
                MagnumOpus npcLevel = commandBuffer.getComponent(ref, MagnumOpus.getComponentType());
                //if(npcLevel != null) defaultXP = (float) npcLevel.getLevel();
                GiveXPEvent.dispatch(playerRef, defaultXP);
            }
        }
    }
}


