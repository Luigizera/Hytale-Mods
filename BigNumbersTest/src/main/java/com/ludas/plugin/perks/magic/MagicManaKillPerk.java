package com.ludas.plugin.perks.magic;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.ludas.plugin.clazz.Config;
import com.ludas.plugin.clazz.Perk;
import com.ludas.plugin.clazz.PerkId;
import com.ludas.plugin.clazz.PerkType;
import com.ludas.plugin.components.effects.ManaKillEffect;
import com.ludas.plugin.components.entity.MainStatusComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class MagicManaKillPerk extends Perk {
    public static final String NAME = "manakill";

    @Override
    public Map<Integer, StaticModifier> setupModifiers() {
        Map<Integer, StaticModifier> modifiers = new HashMap<>();

        int manaIndex = DefaultEntityStatTypes.getMana();
        int healthIndex = DefaultEntityStatTypes.getHealth();

        StaticModifier modifier1 = new StaticModifier(
                Modifier.ModifierTarget.MAX,
                StaticModifier.CalculationType.ADDITIVE,
                20.0f
        );

        StaticModifier modifier2 = new StaticModifier(
                Modifier.ModifierTarget.MAX,
                StaticModifier.CalculationType.ADDITIVE,
                20.0f
        );

        modifiers.put(manaIndex, modifier1);
        modifiers.put(healthIndex, modifier2);

        return modifiers;
    }

    @Override
    public void unlockCondition(int idx, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk) {
        MainStatusComponent mainStatus = archetypeChunk.getComponent(idx, MainStatusComponent.getComponentType());
        if(mainStatus == null) return;

        if(mainStatus.getMagic().getLevelComponent().getLevel().compareTo(new BigInteger("20")) < 0) {
            return;
        }
        mainStatus.getMagic().setUnlocked(PerkId.MAGIC_MANA_KILL);
        PlayerRef playerRef = archetypeChunk.getComponent(idx, PlayerRef.getComponentType());
        if(playerRef == null) return;
        var packet = playerRef.getPacketHandler();

        Config.perkUnlockedNotification(packet, Config.ICON_PERK_MAGIC, NAME, PerkType.MAGIC);
    }

    @Override
    public void tick(float dt, int idx, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        Player player = archetypeChunk.getComponent(idx, Player.getComponentType());
        if(player == null) return;
        Ref<EntityStore> playerRef = player.getReference();
        if(playerRef == null) return;
        ManaKillEffect manaKillEffect = store.getComponent(playerRef, ManaKillEffect.getComponentType());

        if(manaKillEffect == null) {
            commandBuffer.putComponent(playerRef, ManaKillEffect.getComponentType(), new ManaKillEffect());
        }
    }

    @Override
    public void removeComponents(int idx, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        Player player = archetypeChunk.getComponent(idx, Player.getComponentType());
        if(player == null) return;
        Ref<EntityStore> playerRef = player.getReference();
        if(playerRef == null) return;
        ManaKillEffect manaKillEffect = store.getComponent(playerRef, ManaKillEffect.getComponentType());
        if(manaKillEffect != null) {
            commandBuffer.removeComponent(playerRef, ManaKillEffect.getComponentType());
        }
    }
}
