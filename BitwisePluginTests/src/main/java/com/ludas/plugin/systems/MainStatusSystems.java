package com.ludas.plugin.systems;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.component.system.tick.DelayedEntitySystem;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.damage.*;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.ludas.plugin.TestPlugin;
import com.ludas.plugin.clazz.Perk;
import com.ludas.plugin.clazz.PerkId;
import com.ludas.plugin.components.entity.LevelComponent;
import com.ludas.plugin.components.entity.MainStatusComponent;
import com.ludas.plugin.events.*;
import com.ludas.plugin.events.damage.AgilityCritDamageEvent;
import com.ludas.plugin.events.damage.MagicManaDamageEvent;
import com.ludas.plugin.events.damage.StrengthExtraDamageEvent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;


public class MainStatusSystems {

    private static boolean isWeapon(ItemStack item) {
        String itemId = item.getItemId();
        if(itemId.isEmpty()) return false;
        return itemId.startsWith("Weapon");
    }

    private static boolean isItemAgilityRelated(ItemStack item) {
        String itemId = item.getItemId();
        if(itemId.isEmpty()) return false;
        return itemId.startsWith("Weapon_Sword")
                || itemId.startsWith("Weapon_Axe")
                || itemId.startsWith("Weapon_Daggers")
                || itemId.startsWith("Weapon_Kunai")
                || itemId.startsWith("Weapon_Spear")
                || itemId.startsWith("Weapon_Crossbow")
                || itemId.startsWith("Weapon_Shortbow")
                || itemId.startsWith("Weapon_Claws");
    }

    private static boolean isItemStrengthRelated(ItemStack item) {
        String itemId = item.getItemId();
        if(itemId.isEmpty()) return false;
        return itemId.startsWith("Weapon_Battleaxe")
                || itemId.startsWith("Weapon_Club")
                || itemId.startsWith("Weapon_Longsword")
                || itemId.startsWith("Weapon_Mace")
                || itemId.startsWith("Weapon_Shield");
    }

    private static boolean isItemMagicRelated(ItemStack item) {
        String itemId = item.getItemId();
        if(itemId.isEmpty()) return false;
        return itemId.startsWith("Weapon_Wand")
                || itemId.startsWith("Weapon_Staff")
                || itemId.startsWith("Weapon_Spellbook");
    }

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

            for(int i = 0; i < PerkId.CURRENT_PERK_COUNT; ++i) {
                Perk perk = mainStatus.getPerkById(i);
                if(perk == null) continue;
                if(!mainStatus.isPerkUnlocked(i)) {
                    perk.unlockCondition(idx, archetypeChunk);
                }
                else if(!mainStatus.isPerkEnabled(i)){
                    perk.removeComponents(idx, archetypeChunk, commandBuffer);
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
                playerRef.sendMessage(Message.raw("Main Level: %d (%.2f XP)".formatted(
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
            Damage.Source damageSource = damage.getSource();
            if (!(damageSource instanceof Damage.EntitySource entitySource)) return;
            Ref<EntityStore> sourceRef = entitySource.getRef();
            Player attacker = store.getComponent(sourceRef, Player.getComponentType());
            if (attacker == null) return;
            Ref<EntityStore> attackerRef = attacker.getReference();
            if (attackerRef == null) return;
            Inventory inventory = attacker.getInventory();
            if (inventory == null) return;
            ItemStack item = inventory.getActiveHotbarItem();
            TestPlugin.LOGGER.atInfo().log("Item: " + item);
            LevelComponent npcLevel = archetypeChunk.getComponent(index, LevelComponent.getComponentType());
            if (npcLevel != null) {
                defaultXP = (float) npcLevel.getLevel();
            }
            Ref<EntityStore> npcRef = npcComponent.getReference();
            if (npcRef == null) return;
            if(item == null) {
                if(damage.getCause() != DamageCause.PHYSICAL && damage.getCause().getInherits() != DamageCause.PHYSICAL.getId()) {
                    GiveMagicXPEvent.dispatch(attackerRef, defaultXP);
                }
                else {
                    GiveStrengthXPEvent.dispatch(attackerRef, defaultXP);
                }
            }
            else {
                if(isWeapon(item)) {
                    if(isItemAgilityRelated(item)) {
                        TestPlugin.LOGGER.atInfo().log("Agility Related");
                        GiveAgilityXPEvent.dispatch(attackerRef, defaultXP);
                        AgilityCritDamageEvent.dispatch(attackerRef, npcRef, damage, commandBuffer);
                    }
                    else if(isItemStrengthRelated(item)) {
                        TestPlugin.LOGGER.atInfo().log("Strength Related");
                        GiveStrengthXPEvent.dispatch(attackerRef, defaultXP);
                    }
                    else {
                        TestPlugin.LOGGER.atInfo().log("Unknown/Magic Related");
                        GiveMagicXPEvent.dispatch(attackerRef, defaultXP);
                    }
                }
                else {
                    GiveStrengthXPEvent.dispatch(attackerRef, defaultXP);
                }
            }

            if(damage.getCause() != DamageCause.PHYSICAL && damage.getCause().getInherits() != DamageCause.PHYSICAL.getId()) {
                MagicManaDamageEvent.dispatch(attackerRef, npcRef, commandBuffer);
            }
            else {
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
            Player target = archetypeChunk.getComponent(index, Player.getComponentType());
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
            ItemStack item = inventory.getActiveHotbarItem();
            Ref<EntityStore> targetRef = target.getReference();
            if (targetRef == null) return;
            if(isWeapon(item) && isItemAgilityRelated(item)) {
                AgilityCritDamageEvent.dispatch(attackerRef, targetRef, damage, commandBuffer);
            }

            if(damage.getCause() != DamageCause.PHYSICAL && damage.getCause().getInherits() != DamageCause.PHYSICAL.getId()) {
                MagicManaDamageEvent.dispatch(attackerRef, targetRef, commandBuffer);
            }
            else {
                StrengthExtraDamageEvent.dispatch(attackerRef, targetRef, damage, commandBuffer);
            }
        }

    }
}

