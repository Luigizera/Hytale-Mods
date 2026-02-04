package com.ludas.plugin.systems;

import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.ludas.plugin.TestPlugin;
import com.ludas.plugin.clazz.MagnumOpusStatTypes;
import com.ludas.plugin.clazz.MagnumOpus;
import com.ludas.plugin.clazz.Perk;
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
import com.ludas.plugin.events.RegisterPerksEvent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.awt.*;
import java.util.List;
import java.util.Random;


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
            if (limiter >= 5f) {
                limiter = 0;
                MagnumOpus magnumOpus = archetypeChunk.getComponent(idx, MagnumOpus.getComponentType());
                if(magnumOpus == null) return;
                Player player = archetypeChunk.getComponent(idx, Player.getComponentType());
                if(player == null) return;
                List<Perk> perks = magnumOpus.getStat(MagnumOpusStatTypes.STRENGTH.id).getPerksAsList();
                if(perks == null || perks.isEmpty()) return;
                for(Perk perk : perks) {
                    if(!perk.isUnlocked()) {
                        perk.unlockCondition(dt, idx, archetypeChunk, store, commandBuffer);
                    }
                    else {
                        if(!perk.isEnabled()) continue;
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
            MagnumOpus magnumOpus = holder.getComponent(MagnumOpus.getComponentType());
            if(magnumOpus == null) {
                int rand = new Random().nextInt(1, 100);
                holder.putComponent(MagnumOpus.getComponentType(), new MagnumOpus());
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

            PlayerRef player = store.getComponent(ref, PlayerRef.getComponentType());
            if(player == null) return;
            MagnumOpus magnumOpus = store.getComponent(ref, MagnumOpus.getComponentType());
            if(magnumOpus == null) {
                commandBuffer.putComponent(ref, MagnumOpus.getComponentType(), new MagnumOpus());
                player.sendMessage(Message.raw("Adicionado sistema Magnum Opus").color(Color.ORANGE).bold(true));
            }
            else {
                /*player.sendMessage(Message.raw("Level: %d (%.2f XP)"
                                .formatted(magnumOpus.getStat(MagnumOpusStatTypes.STRENGTH.id).getLevel().getCurrentLevel(),
                                        magnumOpus.getStat(MagnumOpusStatTypes.STRENGTH.id).getLevel().getCurrentExperience()))
                                .color(Color.ORANGE).bold(true));*/
            }
        }

        @Override
        public void onEntityRemove(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl RemoveReason removeReason, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
            if(removeReason != RemoveReason.UNLOAD) return;
            //MagnumOpus magnumOpus = store.getComponent(ref, component);
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
            NPCEntity npcComponent = (NPCEntity)commandBuffer.getComponent(ref, NPCEntity.getComponentType());
            if (npcComponent == null) return;
            Damage deathInfo = component.getDeathInfo();
            if(deathInfo == null) return;
            DamageCause deathCause = component.getDeathCause();
            if(!(deathCause == DamageCause.PHYSICAL || deathCause == DamageCause.PROJECTILE)) return;
            Damage.Source damageSource = deathInfo.getSource();
            if (!(damageSource instanceof Damage.EntitySource entitySource)) return;
            Ref<EntityStore> sourceRef = entitySource.getRef();
            Player sourcePlayer = store.getComponent(sourceRef, Player.getComponentType());
            if(sourcePlayer == null) return;
            Ref<EntityStore> playerRef = sourcePlayer.getReference();
            if(playerRef == null) return;
            MagnumOpus magnumOpus = store.getComponent(playerRef, MagnumOpus.getComponentType());
            if(magnumOpus == null) {
                commandBuffer.putComponent(playerRef, MagnumOpus.getComponentType(), new MagnumOpus());
            }
            else {
                MagnumOpus npcLevel = commandBuffer.getComponent(ref, MagnumOpus.getComponentType());
                //if(npcLevel != null) defaultXP = (float) npcLevel.getLevel();
                GiveXPEvent.dispatch(playerRef, defaultXP);
            }
        }
    }
}

