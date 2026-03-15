package com.ludas.plugin.components.entity;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.math.BigDecimal;
import java.math.BigInteger;


public class LevelComponent implements Component<EntityStore> {
    public static final BuilderCodec<LevelComponent> CODEC;
    private static ComponentType<EntityStore, LevelComponent> TYPE;
    public static final String MULTIPLIER = "100";
    public static final String START_LEVEL = "1";
    public static final String START_EXPERIENCE = "0.0";
    private String level;
    private String experienceCurrent;

    public LevelComponent(BigInteger level) {
        this.level = level.signum() == 1 ? level.toString() : START_LEVEL;
        this.experienceCurrent = START_EXPERIENCE;
    }

    public LevelComponent() {
        this.level = START_LEVEL;
        this.experienceCurrent = START_EXPERIENCE;
    }

    public LevelComponent(LevelComponent other) {
        this.level = other.level;
        this.experienceCurrent = other.experienceCurrent;
    }

    public static void setComponentType(ComponentType<EntityStore, LevelComponent> type) {
        TYPE = type;
    }

    public static ComponentType<EntityStore, LevelComponent> getComponentType() {
        return TYPE;
    }

    public BigDecimal getExperienceToNextLevel() {
        BigDecimal nextLevel = new BigDecimal(this.level);
        nextLevel = nextLevel.add(nextLevel.subtract(new BigDecimal("1"))).multiply(new BigDecimal(MULTIPLIER));
        return nextLevel;
    }

    public BigDecimal getCurrentExperience() {
        return new BigDecimal(this.experienceCurrent);
    }

    public BigInteger getLevel() {
        return new BigInteger(this.level);
    }

    public boolean addExperience(BigDecimal exp) {
        boolean hasLeveledUp = false;
        experienceCurrent = getCurrentExperience().add(exp).toString();
        while(canLevelUp()) {
            experienceCurrent = getCurrentExperience().subtract(this.getExperienceToNextLevel()).toString();
            this.addLevel();
            hasLeveledUp = true;
        }
        return hasLeveledUp;
    }

    public boolean canLevelUp() {
        return getCurrentExperience().compareTo(this.getExperienceToNextLevel()) >= 0;
    }

    protected void addLevel() {
        this.level = getLevel().add(new BigInteger("1")).toString();
    }

    @Override
    public String toString() {
        return "Level{level=" + level +
                ", experience=" + experienceCurrent +
                ", toNext=" + this.getExperienceToNextLevel() + "}";
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        LevelComponent level = new LevelComponent(this);
        return level;
    }

    @NullableDecl
    @Override
    public Component<EntityStore> cloneSerializable() {
        return Component.super.cloneSerializable();
    }

    static {
        CODEC = BuilderCodec.builder(LevelComponent.class, LevelComponent::new)
                        .append(new KeyedCodec<String>("Level", Codec.STRING),
                                (data, value) -> data.level = value,
                                data -> data.level)
                        .add()
                        .append(new KeyedCodec<String>("Experience", Codec.STRING),
                                (data, value) -> data.experienceCurrent = value,
                                data -> data.experienceCurrent)
                        .add()
                        .build();
    }
}
