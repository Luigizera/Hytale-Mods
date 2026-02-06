package com.ludas.plugin.clazz;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.lookup.CodecMapCodec;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.io.NetworkSerializable;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.HashMap;
import java.util.Map;

public abstract class Perk implements NetworkSerializable<PerkProtocol> {
    public static final BuilderCodec<Perk> BASE_CODEC;
    public static final CodecMapCodec<Perk> MAP_CODEC = new CodecMapCodec<>();
    public static final String DEFAULT_ID = "Unknown";
    public static final boolean DEFAULT_ENABLED = true;
    public static final boolean DEFAULT_UNLOCKED = false;
    private String id;
    private boolean enabled;
    private boolean unlocked;

    public Perk() {
        this(DEFAULT_ID, DEFAULT_ENABLED, DEFAULT_UNLOCKED);
    }


    public Perk(String id) {
        this.id = id;
        this.enabled = true;
        this.unlocked = false;
    }

    public Perk(String id, boolean enabled, boolean unlocked) {
        this.id = id;
        this.enabled = enabled;
        this.unlocked = unlocked;
    }

    @Override
    public PerkProtocol toPacket() {
        PerkProtocol packet = new PerkProtocol();
        packet.id = this.id;
        packet.enabled = this.enabled;
        packet.unlocked = this.unlocked;
        return packet;
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


    public Map<Integer, StaticModifier> setupModifiers() {
        return new HashMap<>();
    }

    public abstract void unlockCondition(float dt, int idx, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
                     @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer);


    public abstract void tick(float dt, int idx, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
                     @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer);

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

    static {
        BASE_CODEC = BuilderCodec.abstractBuilder(Perk.class)
                        .append(
                                new KeyedCodec<>("Id", Codec.STRING),
                                (data, value) -> data.id = value,
                                data -> data.id
                        )
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