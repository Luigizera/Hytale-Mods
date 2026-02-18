package com.ludas.plugin.perks.general;

import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
import com.hypixel.hytale.component.*;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.EntityEffect;
import com.hypixel.hytale.server.core.entity.effect.ActiveEntityEffect;
import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.NotificationUtil;
import com.ludas.plugin.TestPlugin;
import com.ludas.plugin.clazz.Perk;
import com.ludas.plugin.clazz.PerkId;
import com.ludas.plugin.components.effects.PoisonComponent;
import com.ludas.plugin.components.entity.MainStatusComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.HashMap;
import java.util.Map;

public class PoisonPerk extends Perk{
    public static final String NAME = "Main_Poison";

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
        mainStatus.setUnlocked(PerkId.POISON_PERK);
        Player player = archetypeChunk.getComponent(idx, Player.getComponentType());
        if(player == null) return;
        PlayerRef playerRef = archetypeChunk.getComponent(idx, PlayerRef.getComponentType());
        if(playerRef == null) return;
        var packetHandler = playerRef.getPacketHandler();

        var primaryMessage = Message.raw("Poison Perk").color("#00FF00");
        var secondaryMessage = Message.raw("Is now unlocked!").color("#228B22");
        var icon = new ItemStack("Weapon_Sword_Mithril", 1).toPacket();

        NotificationUtil.sendNotification(
                packetHandler,
                primaryMessage,
                secondaryMessage,
                icon);
        player.sendMessage(Message.translation("server.perks.ludas.unlocked").param("id", NAME));
    }

    public void tick(float dt, int idx, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
                     @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        Player player = archetypeChunk.getComponent(idx, Player.getComponentType());
        if(player == null) return;
        if(player.getGameMode() != GameMode.Adventure) return;
        Ref<EntityStore> playerRef = player.getReference();
        if(playerRef == null) return;
        PoisonComponent poison = store.getComponent(playerRef, PoisonComponent.getComponentType());
        if(poison != null) return;
        EntityStatMap statMap = archetypeChunk.getComponent(idx, EntityStatMap.getComponentType());
        if(statMap == null) return;
        EntityStatValue entityHealth = statMap.get(DefaultEntityStatTypes.getHealth());
        if(entityHealth == null) return;

        float percentage =  entityHealth.asPercentage();
        if(percentage >= 0.7) {
            EntityEffect effect = EntityEffect.getAssetMap().getAsset("Poison_T1");
            /*IndexedLookupTableAssetMap<String, EntityEffect> assetMap = EntityEffect.getAssetMap();
            for(int i = 0; i < 20; ++i) {
                TestPlugin.LOGGER.atInfo().log("Asset: "+assetMap.getAsset(i));
            }*/
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
            commandBuffer.addComponent(playerRef, PoisonComponent.getComponentType(), new PoisonComponent());
             */
        }
    }

    public void removeComponents(int idx, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
                                 @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        /* EXEMPLO
        PoisonComponent poison = archetypeChunk.getComponent(idx, PoisonComponent.getComponentType());
        if(poison == null) return;
        Player player = archetypeChunk.getComponent(idx, Player.getComponentType());
        if(player == null) return;
        Ref<EntityStore> playerRef = player.getReference();
        if(playerRef == null) return;
        commandBuffer.tryRemoveComponent(playerRef, PoisonComponent.getComponentType());
        */
        return;
    }
}
