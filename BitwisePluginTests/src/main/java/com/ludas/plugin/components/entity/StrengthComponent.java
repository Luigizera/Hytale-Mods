package com.ludas.plugin.components.entity;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.ludas.plugin.clazz.Perk;
import com.ludas.plugin.clazz.StrengthPerkId;
import com.ludas.plugin.perks.strength.StrengthExplosivePerk;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class StrengthComponent implements Component<EntityStore> {
    public static final BuilderCodec<StrengthComponent> CODEC;
    private static ComponentType<EntityStore, StrengthComponent> TYPE;
    private LevelComponent level;
    public static final int PERK_LENGTH = 2;
    public static final float BASE_EXP_MULTIPLIER = 0.1f;
    public static final float BASE_DMG_MULTIPLIER = 0.002f;
    private int[] perks; //ints: 0 = unlock, 1 = enable

    public StrengthComponent() {
        this.level = new LevelComponent();
        this.perks = new int[PERK_LENGTH];
    }

    public StrengthComponent(StrengthComponent other) {
        this.level = other.level;
        this.perks = other.perks;
    }

    public static void setComponentType(ComponentType<EntityStore, StrengthComponent> type) {
        TYPE = type;
    }

    public static ComponentType<EntityStore, StrengthComponent> getComponentType() {
        return TYPE;
    }

    public LevelComponent getLevelComponent() {
        return this.level;
    }

    public float getDefaultExp() {
        return BASE_EXP_MULTIPLIER * LevelComponent.MULTIPLIER;
    }

    public float getDamageMultiplier() {
        return BASE_DMG_MULTIPLIER * this.level.getLevel();
    }

    private boolean isPerkValid(int FLAG_ID) {
        return perks != null
                && perks.length == PERK_LENGTH
                && FLAG_ID >= 0
                && FLAG_ID < StrengthPerkId.CURRENT_PERK_COUNT;
    }

    public void setUnlocked(int FLAG_ID) {
        if(!isPerkValid(FLAG_ID)) throw new RuntimeException("Perk is not valid in setUnlocked");
        perks[0] |= (1 << FLAG_ID);
    }

    public boolean isPerkUnlocked(int FLAG_ID) {
        if(!isPerkValid(FLAG_ID)) return false;
        int unlocked = perks[0];
        return (unlocked & (1 << FLAG_ID)) != 0;
    }

    public boolean isPerkEnabled(int FLAG_ID) {
        if(!isPerkValid(FLAG_ID)) return false;
        int enabled = perks[1];
        return (enabled & (1 << FLAG_ID)) != 0;
    }

    public int getPerkIdByName(String name) {
        return switch (name.toUpperCase()) {
            case "EXPLOSIVE" -> StrengthPerkId.EXPLOSIVE_PERK;
            default -> -1;
        };
    }

    public String getPerkNameById(int FLAG_ID) {
        return switch (FLAG_ID) {
            case StrengthPerkId.EXPLOSIVE_PERK -> StrengthExplosivePerk.NAME;
            default -> "unknown";
        };
    }

    public Perk getPerkById(int FLAG_ID) {
        return switch (FLAG_ID) {
            case StrengthPerkId.EXPLOSIVE_PERK -> new StrengthExplosivePerk();
            default -> null;
        };
    }

    public void enableOrDisablePerk(int FLAG_ID) {
        if(FLAG_ID < 0) return;
        if(isPerkValid(FLAG_ID)) {
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

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        return new StrengthComponent(this);
    }

    static {
        CODEC = BuilderCodec.builder(StrengthComponent.class, StrengthComponent::new)
                .append(new KeyedCodec<>("Level", LevelComponent.CODEC),
                        (data, value) -> data.level = value,
                        data -> data.level)
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
