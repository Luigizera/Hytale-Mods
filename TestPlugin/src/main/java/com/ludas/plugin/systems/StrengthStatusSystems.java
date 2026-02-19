package com.ludas.plugin.systems;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.DelayedEntitySystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.*;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.ludas.plugin.clazz.Perk;
import com.ludas.plugin.clazz.PerkId;
import com.ludas.plugin.components.entity.MainStatusComponent;
import com.ludas.plugin.components.entity.StrengthComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;


public class StrengthStatusSystems {

    public static class PerkTick extends DelayedEntitySystem<EntityStore> {

        public PerkTick() {
            super(5f);
        }

        @NullableDecl
        @Override
        public Query<EntityStore> getQuery() {
            return Query.and(new Query[]{MainStatusComponent.getComponentType(), Player.getComponentType()});
        }

        @Override
        public void tick(float dt, int idx, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
                         @NonNullDecl Store<EntityStore> store,
                         @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
            MainStatusComponent mainStatus = archetypeChunk.getComponent(idx, MainStatusComponent.getComponentType());
            if(mainStatus == null) return;
            StrengthComponent strength = mainStatus.getStrength();
            if(strength == null) return;

            for(int i = 0; i < PerkId.STRENGTH_CURRENT_PERK_COUNT; ++i) {
                Perk perk = strength.getPerkById(i);
                if(perk == null) continue;
                if(!strength.isPerkUnlocked(i)) {
                    perk.unlockCondition(idx, archetypeChunk);
                }
                else if (!strength.isPerkEnabled(i)) {
                    perk.removeComponents(idx, archetypeChunk, store, commandBuffer);
                }
                else {
                    perk.tick(dt, idx, archetypeChunk, store, commandBuffer);
                }
            }
        }
    }
}