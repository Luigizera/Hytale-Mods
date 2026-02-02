package com.ludas.plugin.clazz;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class Stat {
    public static final BuilderCodec<Stat> CODEC;
    private String id;

    public Stat() {
        id = LevelingStatsTypes.UNKNOWN.id;
    }

    public Stat(LevelingStatsTypes id) {
        this.id = id.id;
    }

    public String getId() {
        return id;
    }

    static {
       CODEC = BuilderCodec.builder(Stat.class, Stat::new)
                .append(new KeyedCodec<>("Id", Codec.STRING),
                        (data, value) -> data.id = value,
                        data -> data.id)
                .add()
                .build();
    }


}