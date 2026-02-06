package com.ludas.plugin.clazz;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.codec.lookup.CodecMapCodec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Status {
    public static final BuilderCodec<Status> BASE_CODEC;
    public static final CodecMapCodec<Status> MAP_CODEC = new CodecMapCodec<>();

    private String id;
    protected Map<String, Perk> perks;
    private Level level;
    protected float modifier;

    public Status() {
    }

    public Status(String id) {
        this.id = id;
        this.modifier = 0f;
        this.level = new Level();
        this.perks = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public Level getLevel() {
        return level;
    }

    public float getModifier() {
        return modifier;
    }

    public void addToModifier() {
        this.modifier += modifier;
    }

    public List<Perk> getPerksAsList() {
        return perks.values().stream().toList();
    }

    protected Map<String, Perk> getPerks() {
        return this.perks;
    }

    public Perk getPerk(String id) {
        return perks.getOrDefault(id, null);
    }

    public void putPerk(Perk perk) {
        if (this.perks == null || this.perks.isEmpty() || this.perks.size() <= 0) {
            this.perks = new HashMap<String, Perk>();
        }
        this.perks.put(perk.getId(), perk);
    }

    public HashMap<String, Perk> registerPerks() {
        return new HashMap<>();
    }

    public boolean enableOrDisablePerk(String perk) {
        Perk perk1 = perks.getOrDefault(perk, null);
        if(perk1 != null) {
            perk1.setEnabled();
            perks.replace(perk1.getId(), perk1);
            return perk1.isEnabled();
        }
        return false;
    }

    @Override
    public String toString() {
        return "Status{id=" + id +
                ", modifier=" + modifier +
                ", level=" + level.toString() +
                ", perks=" + perks.values() +
                "}";
    }

    static {
        BASE_CODEC = BuilderCodec.abstractBuilder(Status.class)
                .append(new KeyedCodec<>("Id", Codec.STRING),
                        (data, value) -> data.id = value,
                        data -> data.id)
                .add()
                .append(new KeyedCodec<>("Modifier", Codec.FLOAT),
                        (data, value) -> data.modifier = value,
                        data -> data.modifier)
                .add()
                .append(new KeyedCodec<>("Level", Level.CODEC),
                        (data, value) -> data.level = value,
                        data -> data.level)
                .add()
                .append(
                        new KeyedCodec("Perks", new MapCodec<>(Perk.MAP_CODEC, HashMap<String, Perk>::new, false)),
                        (data, value) -> data.perks = value,
                        (data) -> data.perks != null
                                && !data.perks.isEmpty() ? data.perks : null
                )
                .add().build();
    }
}
