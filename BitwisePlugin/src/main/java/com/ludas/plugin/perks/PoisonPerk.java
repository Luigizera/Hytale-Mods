package com.ludas.plugin.perks;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.ludas.plugin.clazz.Perk;
import com.ludas.plugin.clazz.PerkId;
import com.ludas.plugin.components.LevelComponent;
import com.ludas.plugin.components.PoisonComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.HashMap;
import java.util.Map;

public class PoisonPerk extends Perk{
    public static final String NAME = "poison";

    public PoisonPerk() {
    }

    @Override
    public Map<Integer, StaticModifier> setupModifiers() {
        Map<Integer, StaticModifier> modifiers = new HashMap<>();

        int healthIndex = DefaultEntityStatTypes.getHealth();
        int staminaIndex = DefaultEntityStatTypes.getStamina();

        StaticModifier modifier1 = new StaticModifier(
                Modifier.ModifierTarget.MAX,
                StaticModifier.CalculationType.ADDITIVE,
                50.0f
        );

        StaticModifier modifier2 = new StaticModifier(
                Modifier.ModifierTarget.MAX,
                StaticModifier.CalculationType.ADDITIVE,
                50.0f
        );

        modifiers.put(healthIndex, modifier1);
        modifiers.put(staminaIndex, modifier2);

        return modifiers;
    }

    @Override
    public void unlockCondition(int idx, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk) {
        LevelComponent level = archetypeChunk.getComponent(idx, LevelComponent.getComponentType());
        if(level == null) return;

        if(level.getLevel() <= 1) {
            return;
        }
        Player player = archetypeChunk.getComponent(idx, Player.getComponentType());
        if(player == null) return;
        level.setUnlocked(PerkId.POISON_PERK);
        player.sendMessage(Message.translation("server.perks.ludas.unlocked").param("id", NAME));
    }

    @Override
    public void tick(float dt, int idx, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
                     @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        Player player = archetypeChunk.getComponent(idx, Player.getComponentType());
        if(player == null) return;

        if(player.getGameMode() != GameMode.Adventure) return;
        Ref<EntityStore> playerRef = player.getReference();
        if(playerRef == null) return;
        EntityStatMap statMap = archetypeChunk.getComponent(idx, EntityStatMap.getComponentType());
        if(statMap == null) return;
        EntityStatValue entityHealth = statMap.get(DefaultEntityStatTypes.getHealth());
        if(entityHealth == null) return;

        float percentage =  entityHealth.get() / entityHealth.getMax();
        if(percentage >= 0.7) {
            PoisonComponent poison = archetypeChunk.getComponent(idx, PoisonComponent.getComponentType());
            if(poison != null) return;
            commandBuffer.addComponent(playerRef, PoisonComponent.getComponentType(), new PoisonComponent());
        }
    }

    @Override
    public void removeComponents(int idx, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
                                 @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        Player player = archetypeChunk.getComponent(idx, Player.getComponentType());
        if(player == null) return;
        Ref<EntityStore> playerRef = player.getReference();
        if(playerRef == null) return;
        commandBuffer.tryRemoveComponent(playerRef, PoisonComponent.getComponentType());
    }
}
