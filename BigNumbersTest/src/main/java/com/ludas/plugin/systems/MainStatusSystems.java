package com.ludas.plugin.systems;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.component.system.tick.DelayedEntitySystem;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.asset.type.item.config.ItemWeapon;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.damage.*;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.systems.NPCSystems;
import com.ludas.plugin.TestPlugin;
import com.ludas.plugin.clazz.Config;
import com.ludas.plugin.clazz.Perk;
import com.ludas.plugin.clazz.PerkId;
import com.ludas.plugin.components.entity.LevelComponent;
import com.ludas.plugin.components.entity.MainStatusComponent;
import com.ludas.plugin.events.*;
import com.ludas.plugin.events.damage.AgilityCritDamageEvent;
import com.ludas.plugin.events.damage.MagicManaDamageEvent;
import com.ludas.plugin.events.damage.StrengthExtraDamageEvent;
import com.ludas.plugin.handlers.GiveMainStatusXPHandler;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.math.BigDecimal;
import java.util.Map;


public class MainStatusSystems {

    public static class PerkTick extends DelayedEntitySystem<EntityStore> {

        public PerkTick() {
            super(5f);
        }

        @NullableDecl
        @Override
        public Query<EntityStore> getQuery() {
            return Query.and(MainStatusComponent.getComponentType(), Player.getComponentType());
        }

        @Override
        public void tick(float dt, int idx, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
                         @NonNullDecl Store<EntityStore> store,
                         @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
            MainStatusComponent mainStatus = archetypeChunk.getComponent(idx, MainStatusComponent.getComponentType());
            if(mainStatus == null) return;

            for(int i = 0; i < PerkId.MAIN_CURRENT_PERK_COUNT; ++i) {
                Perk perk = mainStatus.getPerkById(i);
                if(perk == null) continue;
                if(!mainStatus.isPerkUnlocked(i)) {
                    perk.unlockCondition(idx, archetypeChunk);
                }
                else if(!mainStatus.isPerkEnabled(i)){
                    perk.removeComponents(idx, archetypeChunk, store, commandBuffer);
                }
                else{
                    perk.tick(dt, idx, archetypeChunk, store, commandBuffer);
                }
            }
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
            if (playerRef == null) return;

            var component = MainStatusComponent.getComponentType();
            MainStatusComponent status = store.getComponent(ref, component);
            if (status == null) {
                status = new MainStatusComponent();
                commandBuffer.putComponent(ref, component, status);
                playerRef.sendMessage(Message.raw("Adicionado sistema de Status").color(Color.ORANGE).bold(true));
            }
            else {
                playerRef.sendMessage(Message.raw("Main Level: %s (%s XP)".formatted(
                                status.getLevelComponent().getLevel(),
                                status.getLevelComponent().getCurrentExperience()))
                        .color(Color.ORANGE).bold(true));
            }
        }

        @Override
        public void onEntityRemove(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl RemoveReason removeReason, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {

        }
    }

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
            NPCEntity npcComponent = archetypeChunk.getComponent(idx, NPCEntity.getComponentType());
            if (npcComponent == null) return;
            if(damage.getAmount() <= 0) return;
            Damage.Source damageSource = damage.getSource();
            if (!(damageSource instanceof Damage.EntitySource entitySource)) return;
            Ref<EntityStore> sourceRef = entitySource.getRef();
            Player attacker = store.getComponent(sourceRef, Player.getComponentType());
            if (attacker == null) return;
            Ref<EntityStore> attackerRef = attacker.getReference();
            if (attackerRef == null) return;
            Inventory inventory = attacker.getInventory();
            if (inventory == null) return;
            ItemStack itemStack = inventory.getActiveHotbarItem();
            Ref<EntityStore> npcRef = npcComponent.getReference();
            if (npcRef == null) return;
            DamageCause damageCause = damage.getCause();
            if(itemStack == null) {
                if(Config.isDamageCausePhysical(damageCause)) {
                    GiveStrengthXPEvent.dispatch(attackerRef);
                }
                else {
                    GiveMagicXPEvent.dispatch(attackerRef);
                }
            }
            else {
                Item item = itemStack.getItem();
                ItemWeapon weapon = item.getWeapon();
                if (weapon != null) {
                    AssetExtraInfo.Data data = item.getData();
                    Map<String, String[]> tags = data.getRawTags();

                    if(Config.isItemAgilityRelated(tags)) {
                        GiveAgilityXPEvent.dispatch(attackerRef);
                        AgilityCritDamageEvent.dispatch(attackerRef, npcRef, damage, commandBuffer);
                    }
                    else if(Config.isDamageCausePhysical(damageCause)) {
                        GiveStrengthXPEvent.dispatch(attackerRef);
                    }
                    else {
                        GiveMagicXPEvent.dispatch(attackerRef);
                    }
                }
                else {
                    GiveStrengthXPEvent.dispatch(attackerRef);
                }
            }

            if(Config.isDamageCausePhysical(damageCause)) {
                StrengthExtraDamageEvent.dispatch(attackerRef, npcRef, damage, commandBuffer);
            }
            else {
                MagicManaDamageEvent.dispatch(attackerRef, npcRef, commandBuffer);
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
            if(target == null) return;
            Damage.Source damageSource = damage.getSource();
            if (!(damageSource instanceof Damage.EntitySource entitySource)) return;
            Ref<EntityStore> sourceRef = entitySource.getRef();
            Player attacker = store.getComponent(sourceRef, Player.getComponentType());
            if (attacker == null) return;
            Ref<EntityStore> attackerRef = attacker.getReference();
            if (attackerRef == null) return;
            Inventory inventory = attacker.getInventory();
            if (inventory == null) return;
            Ref<EntityStore> targetRef = target.getReference();
            if (targetRef == null) return;
            ItemStack itemStack = inventory.getActiveHotbarItem();
            DamageCause damageCause = damage.getCause();
            if(itemStack != null) {
                Item item = itemStack.getItem();
                ItemWeapon weapon = item.getWeapon();
                if(weapon != null) {
                    AssetExtraInfo.Data data = item.getData();
                    Map<String, String[]> tags = data.getRawTags();
                    if (Config.isItemAgilityRelated(tags)) {
                        AgilityCritDamageEvent.dispatch(attackerRef, targetRef, damage, commandBuffer);
                    }
                }
            }

            if(Config.isDamageCausePhysical(damageCause)) {
                StrengthExtraDamageEvent.dispatch(attackerRef, targetRef, damage, commandBuffer);
            }
            else {
                MagicManaDamageEvent.dispatch(attackerRef, targetRef, commandBuffer);
            }
        }
    }

    public static class DamagePlayerSystem extends DamageEventSystem {
        public DamagePlayerSystem() {
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
            if(target == null) return;
            Ref<EntityStore> targetRef = target.getReference();
            if(targetRef == null) return;
            Damage.Source damageSource = damage.getSource();
            DamageCause damageCause = damage.getCause();
            if (damageSource instanceof Damage.EntitySource entitySource) {
                Ref<EntityStore> sourceRef = entitySource.getRef();
                Player attackerPlayer = store.getComponent(sourceRef, Player.getComponentType());
                if (attackerPlayer == null) {
                    GiveVitalityXPEvent.dispatch(targetRef);
                }
            }
            else if (damageCause != DamageCause.COMMAND) {
                GiveVitalityXPEvent.dispatch(targetRef);
            }
        }
    }

    public static class NPCDeathSystem extends DeathSystems.OnDeathSystem {

        public NPCDeathSystem() {
            super();
        }

        @Nonnull
        public Query<EntityStore> getQuery() {
            return Query.and(NPCEntity.getComponentType());
        }

        @Override
        public void onComponentAdded(@NonNullDecl Ref<EntityStore> ref,
                                     @NonNullDecl DeathComponent deathComponent,
                                     @NonNullDecl Store<EntityStore> store,
                                     @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
            NPCEntity npcComponent = commandBuffer.getComponent(ref, NPCEntity.getComponentType());
            if (npcComponent == null) return;
            Damage deathInfo = deathComponent.getDeathInfo();
            if(deathInfo == null) return;
            Damage.Source damageSource = deathInfo.getSource();
            if (!(damageSource instanceof Damage.EntitySource entitySource)) return;
            Ref<EntityStore> attacker = entitySource.getRef();
            Player attackerPlayer = store.getComponent(attacker, Player.getComponentType());
            if(attackerPlayer == null) return;
            MainStatusComponent mainStatus = store.getComponent(attacker, MainStatusComponent.getComponentType());
            if(mainStatus != null) {
                LevelComponent level = store.getComponent(ref, LevelComponent.getComponentType());
                if(level == null) return;
                int l;
                try {
                    l = Integer.parseInt(level.getLevel().toString());
                }
                catch (NumberFormatException e) {
                    l = Integer.MAX_VALUE;
                }
                GiveMainStatusXPEvent.dispatch(attacker, new BigDecimal(l));
            }
        }
    }
}

