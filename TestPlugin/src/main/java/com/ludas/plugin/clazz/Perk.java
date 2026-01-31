package com.ludas.plugin.clazz;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;

public class Perk {
    public static final Perk[] EMPTY_ARRAY = new Perk[0];
    public static final BuilderCodec<Perk> CODEC;
    private int id;
    private String typeId;

    public Perk() {
    }

    public int getId() {
        return id;
    }

    public String getTypeId() {
        return typeId;
    }

    static {
        CODEC= BuilderCodec
                .builder(Perk.class, Perk::new)
                .append(
                        new KeyedCodec<>("Id", Codec.INTEGER),
                        (component, value) -> component.id = value,
                        component -> component.id
                )
                .add()
                .append(
                        new KeyedCodec<>("TypeId", Codec.STRING),
                        (component, value) -> component.typeId = "",
                        component -> component.typeId
                )
                .add()
                .build();;
    }

}