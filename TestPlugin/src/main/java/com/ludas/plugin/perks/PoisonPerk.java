package com.ludas.plugin.perks;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.ludas.plugin.clazz.Perk;
import com.ludas.plugin.components.PoisonComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.HashMap;
import java.util.Map;

public class PoisonPerk extends Perk {

    public PoisonPerk() {
        super("poison", false);
    }

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

    public void tick(float dt, int idx, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
                     @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        Player player = archetypeChunk.getComponent(idx, Player.getComponentType());
        if(player == null || player.getGameMode() == GameMode.Creative) return;
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
}
