package com.ludas.plugin.components.entity;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;


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
        BigDecimal nextLevel = new BigDecimal(this.level.toCharArray(), 0, this.level.length(), MathContext.UNLIMITED);
        nextLevel = nextLevel.add(nextLevel.subtract(BigDecimal.ONE)).multiply(new BigDecimal(MULTIPLIER));
        return nextLevel;
    }

    public BigDecimal getCurrentExperience() {
        return new BigDecimal(this.experienceCurrent.toCharArray(), 0, this.experienceCurrent.length(), MathContext.UNLIMITED);
    }

    public String getCurrentExperienceString() {
        return this.experienceCurrent;
    }

    public BigInteger getLevel() {
        return new BigInteger(this.level, 10);
    }

    public String getLevelString() {
        return this.level;
    }

    public boolean addExperience(BigDecimal exp) {
        boolean hasLeveledUp = false;
        BigDecimal expNow = getCurrentExperience().add(exp);
        experienceCurrent = expNow.toString();
        while(expNow.compareTo(this.getExperienceToNextLevel()) >= 0) {
            expNow = expNow.subtract(this.getExperienceToNextLevel());
            experienceCurrent = expNow.toString();
            this.addLevel();
            hasLeveledUp = true;
        }
        return hasLeveledUp;
    }

    protected void addLevel() {
        this.level = getLevel().add(BigInteger.ONE).toString();
    }

    @Override
    public String toString() {
        return "LevelComponent{level=" + level +
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
