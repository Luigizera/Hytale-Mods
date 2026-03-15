package com.ludas.plugin.components.entity;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.ludas.plugin.clazz.Perk;
import com.ludas.plugin.clazz.PerkId;
import com.ludas.plugin.perks.strength.StrengthCritPunchPerk;
import com.ludas.plugin.perks.strength.StrengthFrenzyPerk;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.math.BigDecimal;
import java.math.BigInteger;

public class StrengthComponent implements Component<EntityStore> {
    public static final BuilderCodec<StrengthComponent> CODEC;
    private static ComponentType<EntityStore, StrengthComponent> TYPE;
    public static final int PERK_LENGTH = 2;
    public static final String BASE_EXP_MULTIPLIER = "0.1";
    public static final float BASE_DMG_MULTIPLIER = 0.002f;
    private static final int RESET_THRESHOLD = 100;
    private LevelComponent level;
    //private int resets;
    private int[] perks; //ints: 0 = unlock, 1 = enable

    public StrengthComponent() {
        this.level = new LevelComponent();
        this.perks = new int[PERK_LENGTH];
        //this.resets = 0;
    }

    public StrengthComponent(int resets) {
        this.level = new LevelComponent();
        this.perks = new int[PERK_LENGTH];
        //this.resets = resets;
    }

    public StrengthComponent(StrengthComponent other) {
        this.level = other.level;
        this.perks = other.perks;
        //this.resets = other.resets;
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
    /*
    public int getResets() {
        return resets;
    }

    public boolean canReset() {
        return this.level.getLevel() >= RESET_THRESHOLD * (resets + 1);
    }

    public int nextReset() {
        return RESET_THRESHOLD * (resets + 1);
    }*/

    public BigDecimal getDefaultExp() {
        return new BigDecimal(BASE_EXP_MULTIPLIER).multiply(new BigDecimal(LevelComponent.MULTIPLIER)) /** (resets + 1)*/;
    }

    public float getDamageMultiplier() {
        int i;
        try {
            i = Integer.parseInt(this.level.getLevel().toString());
        }
        catch (NumberFormatException e) {
            i = Integer.MAX_VALUE;
        }
        return (BASE_DMG_MULTIPLIER * i) /** (resets + 1)*/;
    }

    private boolean isPerkValid(int FLAG_ID) {
        return perks != null
                && perks.length == PERK_LENGTH
                && FLAG_ID >= 0
                && FLAG_ID < PerkId.STRENGTH_CURRENT_PERK_COUNT;
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
        return switch (name.toLowerCase()) {
            case StrengthFrenzyPerk.NAME -> PerkId.STRENGTH_FRENZY;
            case StrengthCritPunchPerk.NAME -> PerkId.STRENGTH_CRITICAL_PUNCH;
            default -> -1;
        };
    }

    public String getPerkNameById(int FLAG_ID) {
        return switch (FLAG_ID) {
            case PerkId.STRENGTH_FRENZY -> StrengthFrenzyPerk.NAME;
            case PerkId.STRENGTH_CRITICAL_PUNCH -> StrengthCritPunchPerk.NAME;
            default -> "unknown";
        };
    }

    public Perk getPerkById(int FLAG_ID) {
        return switch (FLAG_ID) {
            case PerkId.STRENGTH_FRENZY -> new StrengthFrenzyPerk();
            case PerkId.STRENGTH_CRITICAL_PUNCH -> new StrengthCritPunchPerk();
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
