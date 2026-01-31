package com.ludas.plugin.components;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class LevelComponent implements Component<EntityStore> {
    public static final BuilderCodec<LevelComponent> CODEC;
    private static ComponentType<EntityStore, LevelComponent> TYPE;
    private static final int MULTIPLIER = 100;
    private static final int START_LEVEL = 1;
    private int level;
    private float experienceCurrent;
    private float experienceNextLevel;

    public LevelComponent(int level) {
        this.level = level;
        this.experienceCurrent = 0f;
        this.experienceNextLevel = this.getExperienceToNextLevel();
    }

    public LevelComponent() {
        this.level = START_LEVEL;
        this.experienceCurrent = 0.0F;
        this.experienceNextLevel = this.getExperienceToNextLevel();
    }

    public LevelComponent(LevelComponent other) {
        this.level = other.getLevel();
        this.experienceCurrent = other.getCurrentExperience();
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
                        .build();
    }
}
