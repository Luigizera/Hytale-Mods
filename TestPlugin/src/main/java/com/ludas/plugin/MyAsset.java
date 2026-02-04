package com.ludas.plugin;

import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class MyAsset
implements JsonAssetWithMap<String, IndexedLookupTableAssetMap<String, MyAsset>> {

    public static final BuilderCodec<MyAsset> CODEC;

    private String id;
    private String name;
    private int value;

    public MyAsset() {
        id = "NULO";
    }

    public String getId() {
        return id;
    }
    public String getName() { return name; }
    public int getValue() { return value; }

    static {
        CODEC = BuilderCodec.builder(MyAsset.class, MyAsset::new)
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