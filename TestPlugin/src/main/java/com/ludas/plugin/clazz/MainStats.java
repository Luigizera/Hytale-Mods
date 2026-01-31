package com.ludas.plugin.clazz;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/*
public class MainStats implements Component<EntityStore> {
    public static final BuilderCodec<MainStats> CODEC;
    private Level level;
    private List<Perk> perk;

    public MainStats() {
        this.level = new Level();
        this.perk = Perk.EMPTY_ARRAY;
    }

    public MainStats(Level level, Perk[] perk) {
        this.level = level;
        this.perk = perk;
    }




    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        return null;
    }

    static {
        CODEC = BuilderCodec.builder(MainStats.class, MainStats::new)
                .append(
                        new KeyedCodec<>("Stats", Codec.),
                        (component, value) -> component.experience = value,
                        component -> component.experience
                )
                .add()
                .build();
    }
}
*/