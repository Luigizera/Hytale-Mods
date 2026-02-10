package com.ludas.plugin.clazz;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.lookup.CodecMapCodec;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.HashMap;
import java.util.Map;

public abstract class Perk {
    public static final BuilderCodec<Perk> BASE_CODEC;
    public static final CodecMapCodec<Status> MAP_CODEC = new CodecMapCodec<>();
Modifier
    private String id;
    protected boolean enabled;
    protected boolean unlocked;

    public Perk() {
    }

    public Perk(String id) {
        this.id = id;
        this.enabled = true;
        this.unlocked = false;
    }

    public String getId() {
        return id;
    }

    public boolean isEnabled() {
        return enabled;
    }
    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked() {
        unlocked = true;
    }
    public void setEnabled() {
        enabled = !this.isEnabled();
    }

    @Override
    public String toString() {
        return "Perk{id=" + id +
                ", enabled=" + enabled +
                ", unlocked=" + unlocked +
                "}";
    }

    public Map<Integer, StaticModifier> setupModifiers() {
        return new HashMap<>();
    }

    public void unlockCondition(float dt, int idx, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
                     @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {}


    public void tick(float dt, int idx, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
                     @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
    }

    static {
        BASE_CODEC =
        BuilderCodec.abstractBuilder(Perk.class)
                .append(new KeyedCodec<>("Id", Codec.STRING),
                        (data, value) -> data.id = value,
                        data -> data.id)
                .add()
                .append(
                        new KeyedCodec<>("Enabled", Codec.BOOLEAN),
                        (data, value) -> data.enabled = value,
                        data -> data.enabled
                )
                .add()
                .append(
                        new KeyedCodec<>("Unlocked", Codec.BOOLEAN),
                        (data, value) -> data.enabled = value,
                        data -> data.enabled
                )
                .add()
                .build();
    }
}