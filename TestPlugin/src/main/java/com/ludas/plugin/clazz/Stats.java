package com.ludas.plugin.clazz;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
/*
public class Stats {
    public static final BuilderCodec<Stats> CODEC;
    private Map<String, Perk> unknown;
    @Nonnull
    private String[] statTypes;
    private Level level;

    Stats() {
        statTypes = DefaultPerkTypes.;
    }


    static {
        CODEC =
                ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)
                        BuilderCodec.builder(Stats.class, Stats::new)
                                .legacyVersioned()).codecVersion(5))
                        .addField(
                                new KeyedCodec("Stats", new MapCodec(Perk.CODEC, HashMap::new, false)),
                                (statMap, value) -> statMap.unknown = value,
                                (statMap) -> {
                    HashMap<String, Perk> outMap = new HashMap();
                    if (statMap.unknown != null) {
                        outMap.putAll(statMap.unknown);
                    }

                    for(Perk value : statMap.values) {
                        if (value != null) {
                            outMap.putIfAbsent(value.getId(), value);
                        }
                    }

                    return outMap;
                })).afterDecode((map) -> {
                    map.values = Perk.EMPTY_ARRAY;
                    map.update();
                })).build();
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        return null;
    }
}
*/