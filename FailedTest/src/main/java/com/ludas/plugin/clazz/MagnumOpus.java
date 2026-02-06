package com.ludas.plugin.clazz;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.ludas.plugin.perks.PoisonPerk;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.HashMap;
import java.util.Map;

public class MagnumOpus implements Component<EntityStore> {
    public static final BuilderCodec<MagnumOpus> CODEC;
    private static ComponentType<EntityStore, MagnumOpus> TYPE;
    private Map<String, Status> statMap;

    public MagnumOpus() {
    }

    public MagnumOpus(MagnumOpus other) {
        statMap = other.statMap;
    }

    public Status getStat(String id) {
        return statMap.getOrDefault(id, null);
    }

    public Map<String, Status> putStatus(Status status) {
        if(statMap == null || statMap.isEmpty() || statMap.size() <= 0) {
            this.statMap = new HashMap<>();
        }
        this.statMap.put(status.getId(), status);
        return this.statMap;
    }

    public static void setComponentType(ComponentType<EntityStore, MagnumOpus> type) {
        TYPE = type;
    }

    public static ComponentType<EntityStore, MagnumOpus> getComponentType() {
        return TYPE;
    }


    private Map<String, Status> update() {
        if(statMap == null || statMap.isEmpty() || statMap.size() <= 0) {
            statMap = new HashMap<>();
        }
        return statMap;
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        MagnumOpus map = new MagnumOpus();
        map.statMap = this.statMap;
        map.statMap = map.update();
        return map;
    }

    @Override
    public String toString() {
        return "MagnumOpus{statMap=" + statMap +
                "}";
    }


    static {
        CODEC = BuilderCodec.builder(MagnumOpus.class, MagnumOpus::new)
                .append(
                        new KeyedCodec<>("Stats",
                                new MapCodec(Status.BASE_CODEC, HashMap<String, Status>::new, false)),
                        (data, value) -> data.statMap = value,
                        (data) -> {
                            HashMap<String, Status> outMap = new HashMap();
                            //HashMap<String, Perk> strengthPerks = new HashMap();
                            if (data.statMap != null) {
                                outMap.putAll(data.statMap);
                            }
                            //strengthPerks.put(PoisonPerk.ID, new PoisonPerk());
                            //outMap.putIfAbsent(StrengthStatus.ID, new StrengthStatus(StrengthStatus.ID, 1f, strengthPerks, new Level()));
                            /*for(Stats value : statMap.values) {
                                if (value != null) {
                                    outMap.putIfAbsent(value.getId(), value);
                                }
                            }*/
                            return outMap;
                        })
                .addValidator(Validators.nonNull())
                .add()
                .build();
                /*((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)
                BuilderCodec.builder(MagnumOpus.class, MagnumOpus::new)
                        .legacyVersioned()).codecVersion(5))
                .addField(
                        new KeyedCodec("MagnumOpus", new MapCodec(MagnumOpus.CODEC, HashMap::new, false)),
                        (statMap, value) -> statMap.unknown = value,
                        (statMap) -> {
            HashMap<String, Status> outMap = new HashMap();
            if (statMap.unknown != null) {
                outMap.putAll(statMap.unknown);
            }

            for(Status value : statMap.values) {
                if (value != null) {
                    outMap.putIfAbsent(value.getId(), value);
                }
            }

            return outMap;
        })).afterDecode((map) -> {
            map.values = EntityStatValue.EMPTY_ARRAY;
            map.update();
        })).build();*/
    }


}