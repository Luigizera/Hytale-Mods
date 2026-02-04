package com.ludas.plugin;

import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.ludas.plugin.clazz.Level;

public class MyAsset
        implements JsonAssetWithMap<String, IndexedLookupTableAssetMap<String, MyAsset>> {

    public static final BuilderCodec<MyAsset> CODEC;

    private String id;
    private String name;
    private int value;

    public MyAsset() {
    }

    @Override
    public String getId() { return id; }
    public String getName() { return name; }
    public int getValue() { return value; }

    static {
        CODEC = BuilderCodec.builder(MyAsset.class, MyAsset::new)
                        .append(new KeyedCodec<>("Id", Codec.STRING),
                                (obj, val) -> obj.id = val,
                                obj -> obj.id)
                        .add()
                        .append(new KeyedCodec<>("Name", Codec.STRING),
                                (obj, val) -> obj.name = val,
                                obj -> obj.name)
                        .add()
                        .append(new KeyedCodec<>("Value", Codec.INTEGER),
                                (obj, val) -> obj.value = val,
                                obj -> obj.value)
                        .add()
                        .build();
    }
}