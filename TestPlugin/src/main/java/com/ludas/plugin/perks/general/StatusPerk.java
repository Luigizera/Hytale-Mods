package com.ludas.plugin.perks.general;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.ludas.plugin.clazz.Perk;
import com.ludas.plugin.clazz.PerkId;
import com.ludas.plugin.components.entity.MainStatusComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.HashMap;
import java.util.Map;

public class StatusPerk extends Perk {
    public static final String NAME = "Main_Status";

    public StatusPerk() {
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
        MainStatusComponent mainStatus = archetypeChunk.getComponent(idx, MainStatusComponent.getComponentType());
        if(mainStatus == null) return;

        if(mainStatus.getLevelComponent().getLevel() <= 2) {
            return;
        }
        Player player = archetypeChunk.getComponent(idx, Player.getComponentType());
        if(player == null) return;
        mainStatus.setUnlocked(PerkId.MAIN_STATUS_PERK);
        player.sendMessage(Message.translation("server.perks.ludas.unlocked").param("id", NAME));
    }
    public void tick(float dt, int idx, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
                     @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        return;
    }

    public void removeComponents(int idx, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk, @NonNullDecl Store<EntityStore> store,
                                 @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        return;
    }

}
