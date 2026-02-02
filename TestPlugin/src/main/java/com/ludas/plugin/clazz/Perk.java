package com.ludas.plugin.clazz;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageSystems;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.ludas.plugin.components.PoisonComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.HashMap;
import java.util.Map;

public class Perk {
    public static final BuilderCodec<Perk> CODEC;
    private String id;
    private boolean enabled;

    public Perk() {
        this.id = "unknown";
        this.enabled = false;
    }

    public Perk(String id, boolean enabled) {
        this.id = id;
        this.enabled = enabled;
    }

    public String getId() {
        return id;
    }

    public boolean isEnabled() {
        return enabled;
    }


    public Map<Integer, StaticModifier> setupModifiers() {
        return new HashMap<>();
    }

    public void tick(float dt, int idx, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
                     @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        return;
    }

    public void setEnabled() {
        enabled = !this.isEnabled();
    }

    @Override
    public String toString() {
        return "Perk{id=" + id +
                ", enabled=" + enabled + "}";
    }

    static {

        CODEC =
                BuilderCodec
                        .builder(Perk.class, Perk::new)
                        .append(
                                new KeyedCodec<>("Id", Codec.STRING),
                                (component, value) -> component.id = value,
                                component -> component.id
                        )
                        .add()
                        .append(
                                new KeyedCodec<>("Enabled", Codec.BOOLEAN),
                                (component, value) -> component.enabled = value,
                                component -> component.enabled
                        )
                        .add()
                        .build();
    }
}