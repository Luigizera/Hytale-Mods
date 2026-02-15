package com.ludas.plugin.systems;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.DelayedEntitySystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.ludas.plugin.components.entity.MainStatusComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class AgilityStatusSystems {

    public static class PerkTick extends DelayedEntitySystem<EntityStore> {

        public PerkTick() {
            super(5f);
        }

        @NullableDecl
        @Override
        public Query<EntityStore> getQuery() {
            return Query.and(MainStatusComponent.getComponentType(), Player.getComponentType());
        }

        @Override
        public void tick(float dt, int idx, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
                         @NonNullDecl Store<EntityStore> store,
                         @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
            MainStatusComponent mainStatus = archetypeChunk.getComponent(idx, MainStatusComponent.getComponentType());
            if(mainStatus == null) return;
            /*
            for(int i = 0; i < StrengthPerkId.CURRENT_PERK_COUNT; ++i) {
                Perk perk = mainStatus.getStrength().getPerkById(i);
                if(perk == null) continue;
                if(!mainStatus.getStrength().isPerkUnlocked(i)) {
                    perk.unlockCondition(idx, archetypeChunk);
                }
                else if (!mainStatus.getStrength().isPerkEnabled(i)) {
                    perk.removeComponents(idx, archetypeChunk, commandBuffer);
                }
                else {
                    perk.tick(dt, idx, archetypeChunk, store, commandBuffer);
                }
            }*/
        }
    }
}