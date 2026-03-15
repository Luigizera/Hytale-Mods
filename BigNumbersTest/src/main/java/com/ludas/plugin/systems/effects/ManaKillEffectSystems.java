package com.ludas.plugin.systems.effects;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.ludas.plugin.components.effects.ManaKillEffect;
import com.ludas.plugin.components.entity.LevelComponent;
import com.ludas.plugin.components.entity.MagicComponent;
import com.ludas.plugin.components.entity.MainStatusComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;

public class ManaKillEffectSystems {
    public static class NPCorPlayerDeathSystem extends DeathSystems.OnDeathSystem {
        public NPCorPlayerDeathSystem() {
            super();
        }

        @Nonnull
        public Query<EntityStore> getQuery() {
            return Query.or(Player.getComponentType(), NPCEntity.getComponentType());
        }

        @Override
        public void onComponentAdded(@NonNullDecl Ref<EntityStore> ref,
                                     @NonNullDecl DeathComponent deathComponent,
                                     @NonNullDecl Store<EntityStore> store,
                                     @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
            Damage deathInfo = deathComponent.getDeathInfo();
            if (deathInfo == null) return;
            Damage.Source damageSource = deathInfo.getSource();
            if (!(damageSource instanceof Damage.EntitySource entitySource)) return;
            Ref<EntityStore> attacker = entitySource.getRef();
            Player attackerPlayer = store.getComponent(attacker, Player.getComponentType());
            if (attackerPlayer == null) return;
            EntityStatMap statMap = store.getComponent(attacker, EntityStatMap.getComponentType());
            if (statMap == null) return;
            ManaKillEffect manaKillEffect = store.getComponent(attacker, ManaKillEffect.getComponentType());
            if (manaKillEffect != null) {
                MainStatusComponent mainStatus = store.getComponent(attacker, MainStatusComponent.getComponentType());
                if(mainStatus == null) return;
                MagicComponent magic = mainStatus.getMagic();
                if(magic == null) return;
                LevelComponent level = magic.getLevelComponent();
                if(level == null) return;
                int l;
                try {
                    l = Integer.parseInt(level.getLevel().toString());
                }
                catch (NumberFormatException e) {
                    l = Integer.MAX_VALUE;
                }
                statMap.addStatValue(DefaultEntityStatTypes.getMana(), l);
            }
        }
    }
}
