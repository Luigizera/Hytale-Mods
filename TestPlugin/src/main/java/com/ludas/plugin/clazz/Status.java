package com.ludas.plugin.clazz;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.codec.lookup.CodecMapCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.io.NetworkSerializable;
import com.hypixel.hytale.server.core.modules.entitystats.asset.EntityStatType;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Status {
    public static final CodecMapCodec<Status> MAP_CODEC = new CodecMapCodec<>();
    public static final BuilderCodec<Status> BASE_CODEC;

    private String id;
    private Map<String, Perk> perks;
    private Level level;
    private float modifier;

    public Status() {
        this.id = "unknown";
        this.modifier = 0f;
        this.level = new Level();
        this.perks = new HashMap<>();
    }

    public Status(String id, float modifier, Map<String, Perk> perks, Level level) {
        this.id = id;
        this.modifier = modifier;
        this.level = level;
        this.perks = perks;
    }

    /*
    @Override
    public Status toPacket() {
        Status packet = new Status();
        packet.id = this.id;
        packet.perks = this.perks;
        packet.level = this.level;
        packet.modifier = this.modifier;
        return packet;
    }*/

    public String getId() {
        return id;
    }

    public Level getLevel() {
        return level;
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
        if (this.perks == null) {
            this.perks = new Object2ObjectOpenHashMap();
        }
        this.perks.put(perk.getId(), perk);
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
                        new KeyedCodec("Perks",
                                new MapCodec<>(Perk.MAP_CODEC, HashMap::new, false)),
                        (data, value) -> data.perks = value,
                        (data) -> data.perks != null
                                && !data.perks.isEmpty() ? data.perks : null
                )
                .add().build();
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        return null;
    }
}
