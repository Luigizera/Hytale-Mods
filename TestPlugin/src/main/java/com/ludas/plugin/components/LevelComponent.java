package com.ludas.plugin.components;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.ludas.plugin.clazz.Perk;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LevelComponent implements Component<EntityStore> {
    public static final BuilderCodec<LevelComponent> CODEC;
    private static ComponentType<EntityStore, LevelComponent> TYPE;
    private static final int MULTIPLIER = 100;
    private Map<String, Perk> perks;
    private int level;
    private float experienceCurrent;
    private float experienceNextLevel;

    public LevelComponent(int level) {
        this.level = level;
        this.experienceCurrent = 0f;
        this.perks = new HashMap<>();
        this.experienceNextLevel = this.getExperienceToNextLevel();
    }

    public LevelComponent() {
        this.level = 1;
        this.experienceCurrent = 0.0F;
        this.perks = new HashMap<>();
        this.experienceNextLevel = this.getExperienceToNextLevel();
    }

    public LevelComponent(LevelComponent other) {
        this.level = other.getLevel();
        this.experienceCurrent = other.getCurrentExperience();
        List<Perk> otherPerks = other.getPerksAsList();
        for(Perk perk : otherPerks) {
            this.perks.put(perk.getId(), perk);
        }
        this.experienceNextLevel = other.getExperienceToNextLevel();
    }

    public static void setComponentType(ComponentType<EntityStore, LevelComponent> type) {
        TYPE = type;
    }

    public static ComponentType<EntityStore, LevelComponent> getComponentType() {
        return TYPE;
    }

    public int getExperienceToNextLevel() {
        return (level+level-1) * MULTIPLIER;
    }

    public float getCurrentExperience() {
        return experienceCurrent;
    }

    public int getLevel() {
        return this.level;
    }

    public List<Perk> getPerksAsList() {
        List<Perk> list = perks.values().stream().toList();
        return list;
    }

    public Perk getPerk(Perk perk) {
        return perks.getOrDefault(perk.getId(), null);
    }

    public void putPerk(Perk perk) {
        perks.putIfAbsent(perk.getId(), perk);
    }

    public void enableOrDisablePerk(Perk perk) {
        Perk perk1 = perks.getOrDefault(perk.getId(), null);
        if(perk1 != null) {
            perk1.setEnabled(!perk1.isEnabled());
            perks.replace(perk1.getId(), perk1);
        }
    }

    public boolean addExperience(float exp) {
        boolean hasLeveledUp = false;
        this.experienceCurrent += exp;
        while(canLevelUp()) {
            this.experienceCurrent = this.experienceCurrent - this.experienceNextLevel;
            this.addLevel();
            hasLeveledUp = true;
        }
        return hasLeveledUp;
    }

    public boolean canLevelUp() {
        return this.experienceCurrent >= this.experienceNextLevel;
    }

    protected void addLevel() {
        this.level++;
        this.experienceNextLevel = this.getExperienceToNextLevel();
    }

    @Override
    public String toString() {
        return "Level{level=" + level +
                ", experience=" + experienceCurrent +
                ", toNext=" + getExperienceToNextLevel() + "}";
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        return new LevelComponent(this);
    }

    @NullableDecl
    @Override
    public Component<EntityStore> cloneSerializable() {
        return Component.super.cloneSerializable();
    }

    static {
        CODEC = BuilderCodec.builder(LevelComponent.class, LevelComponent::new)
                        .append(new KeyedCodec<>("Level", Codec.INTEGER),
                                (data, value) -> data.level = value,
                                data -> data.level)
                        .addValidator(Validators.nonNull())
                        .add()
                        .append(new KeyedCodec<>("Experience", Codec.FLOAT),
                                (data, value) -> data.experienceCurrent = value,
                                data -> data.experienceCurrent)
                        .addValidator(Validators.nonNull())
                        .add()
                        .append(new KeyedCodec<>("Perks",
                                new MapCodec(Perk.CODEC, HashMap::new, false)),
                                (data, value) -> data.perks = value,
                                (data) -> data.perks != null && !data.perks.isEmpty() ? data.perks : null)
                        .add()
                        .build();


    }
}
