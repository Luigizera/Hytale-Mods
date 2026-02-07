package com.ludas.plugin.components;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.ludas.plugin.clazz.Perk;
import com.ludas.plugin.clazz.PerkId;
import com.ludas.plugin.perks.PoisonPerk;
import com.ludas.plugin.perks.StatusPerk;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;


public class LevelComponent implements Component<EntityStore> {
    public static final BuilderCodec<LevelComponent> CODEC;
    private static ComponentType<EntityStore, LevelComponent> TYPE;
    public static final int MULTIPLIER = 100;
    public static final int START_LEVEL = 1;
    public static final int PERK_LENGTH = 2;
    private int[] perks; //ints: 0 = unlock, 1 = enable
    private int level;
    private float experienceCurrent;
    private float experienceNextLevel;

    public LevelComponent(int level) {
        this.level = level <= 0 ? START_LEVEL : level;
        this.experienceCurrent = 0f;
        this.perks = new int[PERK_LENGTH];
        this.experienceNextLevel = this.getExperienceToNextLevel();
    }

    public LevelComponent() {
        this.level = START_LEVEL;
        this.experienceCurrent = 0.0F;
        this.perks = new int[PERK_LENGTH];
        this.experienceNextLevel = this.getExperienceToNextLevel();
    }

    public LevelComponent(LevelComponent other) {
        this.level = other.level;
        this.experienceCurrent = other.experienceCurrent;
        this.perks = other.perks;
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

    private boolean isValid(int FLAG_ID) {
        return perks != null
                && perks.length == 2
                && FLAG_ID >= 0
                && FLAG_ID < PerkId.CURRENT_PERK_COUNT;
    }
    public void setUnlocked(int FLAG_ID) {
        if(!isValid(FLAG_ID)) throw new RuntimeException("Perk is not valid in setUnlocked");
        perks[0] |= (1 << FLAG_ID);
    }

    public boolean isPerkUnlocked(int FLAG_ID) {
        if(!isValid(FLAG_ID)) return false;
        int unlocked = perks[0];
        return (unlocked & (1 << FLAG_ID)) != 0;
    }

    public boolean isPerkEnabled(int FLAG_ID) {
        if(!isValid(FLAG_ID)) return false;
        int enabled = perks[1];
        return (enabled & (1 << FLAG_ID)) != 0;
    }

    public int getPerkIdByName(String name) {
        return switch (name.toUpperCase()) {
            case "POISON" -> PerkId.POISON_PERK;
            case "STATUS" -> PerkId.STATUS_PERK;
            default -> -1;
        };
    }

    //TODO: Make perkid possible
    public String getPerkNameById(int FLAG_ID) {
        return switch (FLAG_ID) {
            case PerkId.POISON_PERK -> PoisonPerk.NAME;
            case PerkId.STATUS_PERK -> StatusPerk.NAME;
            default -> "unknown";
        };
    }

    //TODO: Make perkid possible
    public Perk getPerkById(int FLAG_ID) {
        return switch (FLAG_ID) {
            case PerkId.POISON_PERK -> new PoisonPerk();
            case PerkId.STATUS_PERK -> new StatusPerk();
            default -> null;
        };
    }

    public void enableOrDisablePerk(int FLAG_ID) {
        if(FLAG_ID < 0) return;
        if(isValid(FLAG_ID)) {
            if(isPerkUnlocked(FLAG_ID)) {
                if(isPerkEnabled(FLAG_ID)) {
                    perks[1] -= (1 << FLAG_ID);
                }
                else {
                    perks[1] |= (1 << FLAG_ID);
                }
            }
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
                        .append(
                                new KeyedCodec("Perks", Codec.INT_ARRAY),
                                (data, value) -> data.perks = value,
                                (data) -> data.perks)
                        .addValidator(Validators.nonNull())
                        .add()
                        .build();


    }
}
