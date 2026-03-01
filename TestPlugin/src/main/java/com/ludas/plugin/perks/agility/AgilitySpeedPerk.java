package com.ludas.plugin.perks.agility;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.ludas.plugin.clazz.Perk;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.Map;

//TODO
public class AgilitySpeedPerk extends Perk {
    @Override
    public Map<Integer, StaticModifier> setupModifiers() {
        return Map.of();
    }

    @Override
    public void unlockCondition(int idx, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk) {

    }

    @Override
    public void tick(float dt, int idx, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {

    }

    @Override
    public void removeComponents(int idx, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {

    }
}
