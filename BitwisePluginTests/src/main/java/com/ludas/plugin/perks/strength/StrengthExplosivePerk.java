package com.ludas.plugin.perks.strength;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.ludas.plugin.clazz.Perk;
import com.ludas.plugin.clazz.StrengthPerkId;
import com.ludas.plugin.components.entity.StrengthComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.HashMap;
import java.util.Map;

public class StrengthExplosivePerk extends Perk {
    public static final String NAME = "explosive";

    public StrengthExplosivePerk() {
    }

    @Override
    public Map<Integer, StaticModifier> setupModifiers() {
        Map<Integer, StaticModifier> modifiers = new HashMap<>();

        int healthIndex = DefaultEntityStatTypes.getHealth();
        int staminaIndex = DefaultEntityStatTypes.getStamina();

        StaticModifier modifier1 = new StaticModifier(
                Modifier.ModifierTarget.MAX,
                StaticModifier.CalculationType.ADDITIVE,
                5.0f
        );

        StaticModifier modifier2 = new StaticModifier(
                Modifier.ModifierTarget.MAX,
                StaticModifier.CalculationType.ADDITIVE,
                5.0f
        );

        modifiers.put(healthIndex, modifier1);
        modifiers.put(staminaIndex, modifier2);

        return modifiers;
    }

    @Override
    public void unlockCondition(int idx, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk) {
        StrengthComponent strength = archetypeChunk.getComponent(idx, StrengthComponent.getComponentType());
        if(strength == null) return;

        if(strength.getLevelComponent().getLevel() <= 2) {
            return;
        }
        Player player = archetypeChunk.getComponent(idx, Player.getComponentType());
        if(player == null) return;
        strength.setUnlocked(StrengthPerkId.EXPLOSIVE_PERK);
        player.sendMessage(Message.translation("server.perks.ludas.unlocked").param("id", NAME));
    }
}
