package com.ludas.plugin.perks.strength;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.EntityEffect;
import com.hypixel.hytale.server.core.entity.effect.ActiveEntityEffect;
import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.ludas.plugin.clazz.Config;
import com.ludas.plugin.clazz.Perk;
import com.ludas.plugin.clazz.PerkId;
import com.ludas.plugin.components.entity.MainStatusComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.HashMap;
import java.util.Map;

public class StrengthFrenzyPerk extends Perk {
    public static final String NAME = "Strength_Frenzy";

    public StrengthFrenzyPerk() {
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

        if(mainStatus.getStrength().getLevelComponent().getLevel() <= 2) {
            return;
        }
        mainStatus.getStrength().setUnlocked(PerkId.STRENGTH_FRENZY);
        PlayerRef playerRef = archetypeChunk.getComponent(idx, PlayerRef.getComponentType());
        if(playerRef == null) return;
        var packet = playerRef.getPacketHandler();

        Config.perkUnlockedNotification(packet, Config.ICON_PERK_STRENGTH, "Frenzy Perk");
        //player.sendMessage(Message.translation("server.perks.ludas.unlocked").param("id", NAME));
    }

    @Override
    public void tick(float dt, int idx, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        Player player = archetypeChunk.getComponent(idx, Player.getComponentType());
        if(player == null) return;
        if(player.getGameMode() != GameMode.Adventure) return;
        Ref<EntityStore> playerRef = player.getReference();
        if(playerRef == null) return;

        EntityEffect frenzy = EntityEffect.getAssetMap().getAsset("Frenzy");
        if(frenzy == null) return;
        EffectControllerComponent controller =
                store.getComponent(playerRef, EffectControllerComponent.getComponentType());
        if(controller == null) return;
        int frenzyIndex = EntityEffect.getAssetMap().getIndex("Frenzy");

        ActiveEntityEffect activeFrenzy = controller.getActiveEffects().get(frenzyIndex);
        if (activeFrenzy == null) {
            controller.addEffect(playerRef, frenzy, commandBuffer);
        }
    }

    @Override
    public void removeComponents(int idx, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        Player player = archetypeChunk.getComponent(idx, Player.getComponentType());
        if(player == null) return;
        Ref<EntityStore> playerRef = player.getReference();
        if(playerRef == null) return;

        EntityEffect frenzy = EntityEffect.getAssetMap().getAsset("Frenzy");
        if(frenzy == null) return;
        EffectControllerComponent controller = store.getComponent(playerRef, EffectControllerComponent.getComponentType());
        if(controller == null) return;
        int frenzyIndex = EntityEffect.getAssetMap().getIndex("Frenzy");

        ActiveEntityEffect activeFrenzy = controller.getActiveEffects().get(frenzyIndex);
        if (activeFrenzy != null) {
            controller.removeEffect(playerRef, frenzyIndex, commandBuffer);
        }
    }
}
