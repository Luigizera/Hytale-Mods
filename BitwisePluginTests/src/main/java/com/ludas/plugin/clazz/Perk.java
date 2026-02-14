package com.ludas.plugin.clazz;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.Map;

public abstract class Perk {
    public abstract Map<Integer, StaticModifier> setupModifiers();
    public abstract void unlockCondition(int idx, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk);
    public abstract void tick(float dt, int idx, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
                              @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer);
    public abstract void removeComponents(int idx, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
                                          @NonNullDecl CommandBuffer<EntityStore> commandBuffer);
}