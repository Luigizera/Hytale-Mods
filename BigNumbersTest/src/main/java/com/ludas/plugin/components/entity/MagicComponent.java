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
import com.ludas.plugin.perks.magic.MagicManaKillPerk;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.math.BigDecimal;

public class MagicComponent implements Component<EntityStore> {
    public static final BuilderCodec<MagicComponent> CODEC;
    private static ComponentType<EntityStore, MagicComponent> TYPE;
    public static final int PERK_LENGTH = 2;
    public static final String BASE_EXP_MULTIPLIER = "0.1";
    public static final float BASE_DMG_MULTIPLIER = 0.002f;
    private LevelComponent level;
    private int[] perks; //ints: 0 = unlock, 1 = enable

    public MagicComponent() {
        this.level = new LevelComponent();
        this.perks = new int[PERK_LENGTH];
    }

    public MagicComponent(MagicComponent other) {
        this.level = other.level;
        this.perks = other.perks;
    }

    public static void setComponentType(ComponentType<EntityStore, MagicComponent> type) {
        TYPE = type;
    }

    public static ComponentType<EntityStore, MagicComponent> getComponentType() {
        return TYPE;
    }

    public LevelComponent getLevelComponent() {
        return this.level;
    }

    public BigDecimal getDefaultExp() {
        return new BigDecimal(BASE_EXP_MULTIPLIER).multiply(new BigDecimal(LevelComponent.MULTIPLIER));
    }

    public float getDamageMultiplier() {
        int i;
        try {
            i = Integer.parseInt(this.level.getLevelString());
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
                && FLAG_ID < PerkId.MAGIC_CURRENT_PERK_COUNT;
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
            case MagicManaKillPerk.NAME -> PerkId.MAGIC_MANA_KILL;
            default -> -1;
        };
    }

    public String getPerkNameById(int FLAG_ID) {
        return switch (FLAG_ID) {
            case PerkId.MAGIC_MANA_KILL -> MagicManaKillPerk.NAME;
            default -> "unknown";
        };
    }

    public Perk getPerkById(int FLAG_ID) {
        return switch (FLAG_ID) {
            case PerkId.MAGIC_MANA_KILL -> new MagicManaKillPerk();
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
        return new MagicComponent(this);
    }

    static {
        CODEC = BuilderCodec.builder(MagicComponent.class, MagicComponent::new)
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
