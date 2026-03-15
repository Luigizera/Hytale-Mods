package com.ludas.plugin.perks.strength;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.ludas.plugin.clazz.Config;
import com.ludas.plugin.clazz.Perk;
import com.ludas.plugin.clazz.PerkId;
import com.ludas.plugin.clazz.PerkType;
import com.ludas.plugin.components.effects.CritPunchEffect;
import com.ludas.plugin.components.entity.MainStatusComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.math.BigInteger;
import java.util.Map;

public class StrengthCritPunchPerk extends Perk {
    public static final String NAME = "criticalpunch";
    @Override
    public Map<Integer, StaticModifier> setupModifiers() {
        return null;
    }

    @Override
    public void unlockCondition(int idx, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk) {
        MainStatusComponent mainStatus = archetypeChunk.getComponent(idx, MainStatusComponent.getComponentType());
        if(mainStatus == null) return;

        if(mainStatus.getStrength().getLevelComponent().getLevel().compareTo(new BigInteger("20")) < 0) {
            return;
        }
        mainStatus.getStrength().setUnlocked(PerkId.STRENGTH_CRITICAL_PUNCH);
        PlayerRef playerRef = archetypeChunk.getComponent(idx, PlayerRef.getComponentType());
        if(playerRef == null) return;
        var packet = playerRef.getPacketHandler();

        Config.perkUnlockedNotification(packet, Config.ICON_PERK_STRENGTH, NAME, PerkType.STRENGTH);
    }

    @Override
    public void tick(float dt, int idx, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        Player player = archetypeChunk.getComponent(idx, Player.getComponentType());
        if(player == null) return;
        Ref<EntityStore> playerRef = player.getReference();
        if(playerRef == null) return;
        CritPunchEffect critPunchEffect = store.getComponent(playerRef, CritPunchEffect.getComponentType());

        if(critPunchEffect == null) {
            commandBuffer.putComponent(playerRef, CritPunchEffect.getComponentType(), new CritPunchEffect());
        }
    }

    @Override
    public void removeComponents(int idx, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        Player player = archetypeChunk.getComponent(idx, Player.getComponentType());
        if(player == null) return;
        Ref<EntityStore> playerRef = player.getReference();
        if(playerRef == null) return;
        CritPunchEffect critPunchEffect = store.getComponent(playerRef, CritPunchEffect.getComponentType());
        if(critPunchEffect != null) {
            commandBuffer.removeComponent(playerRef, CritPunchEffect.getComponentType());
        }
    }
}
