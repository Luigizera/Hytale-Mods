package com.ludas.plugin.perks.general;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.EntityEffect;
import com.hypixel.hytale.server.core.entity.effect.ActiveEntityEffect;
import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.ludas.plugin.clazz.Config;
import com.ludas.plugin.clazz.Perk;
import com.ludas.plugin.clazz.PerkId;
import com.ludas.plugin.clazz.PerkType;
import com.ludas.plugin.components.entity.MainStatusComponent;
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
        MainStatusComponent mainStatus = archetypeChunk.getComponent(idx, MainStatusComponent.getComponentType());
        if(mainStatus == null) return;

        if(mainStatus.getLevelComponent().getLevel() <= 2) {
            return;
        }
        mainStatus.setUnlocked(PerkId.MAIN_POISON_PERK);
        PlayerRef playerRef = archetypeChunk.getComponent(idx, PlayerRef.getComponentType());
        if(playerRef == null) return;
        var packet = playerRef.getPacketHandler();

        Config.perkUnlockedNotification(packet, Config.ICON_PERK_POISON, NAME, PerkType.MAIN);

        //player.sendMessage(Message.translation("server.perks.ludas.unlocked").param("id", NAME));
    }

    public void tick(float dt, int idx, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
                     @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        Player player = archetypeChunk.getComponent(idx, Player.getComponentType());
        if(player == null) return;
        if(player.getGameMode() != GameMode.Adventure) return;
        Ref<EntityStore> playerRef = player.getReference();
        if(playerRef == null) return;
        EntityStatMap statMap = store.getComponent(playerRef, EntityStatMap.getComponentType());
        if(statMap == null) return;
        EntityStatValue entityHealth = statMap.get(DefaultEntityStatTypes.getHealth());
        if(entityHealth == null) return;

        float percentage =  entityHealth.asPercentage();
        if(percentage >= 0.7) {
            EntityEffect effect = EntityEffect.getAssetMap().getAsset("Poison_T1");
            if(effect == null) return;
            EffectControllerComponent controller =
                    store.getComponent(playerRef, EffectControllerComponent.getComponentType());
            if(controller == null) return;
            int poisonIndex = EntityEffect.getAssetMap().getIndex("Poison_T1");

            ActiveEntityEffect activeEffect = controller.getActiveEffects().get(poisonIndex);
            if (activeEffect == null) {
                controller.addEffect(playerRef, effect, commandBuffer);
            }

            /* EXEMPLO
            commandBuffer.addComponent(playerRef, DoTEffect.getComponentType(), new DoTEffect());
             */
        }
    }

    public void removeComponents(int idx, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk, @NonNullDecl Store<EntityStore> store,
                                 @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        /* EXEMPLO
        Player player = archetypeChunk.getComponent(idx, Player.getComponentType());
        if(player == null) return;
        Ref<EntityStore> playerRef = player.getReference();
        if(playerRef == null) return;
        DoTEffect DoT = store.getComponent(playerRef, DotEffect.getComponentType());
        if(DoT == null) return;
        commandBuffer.tryRemoveComponent(playerRef, DoTEffect.getComponentType());
        */
        return;
    }
}
