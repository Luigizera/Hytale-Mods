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
import com.ludas.plugin.perks.general.HealingAreaPerk;
import com.ludas.plugin.perks.general.PoisonPerk;
import com.ludas.plugin.perks.general.StatusPerk;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class MainStatusComponent implements Component<EntityStore> {
    public static final BuilderCodec<MainStatusComponent> CODEC;
    private static ComponentType<EntityStore, MainStatusComponent> TYPE;
    public static final int PERK_LENGTH = 2;
    public static final String BASE_MULTIPLIER = "0.1";
    private LevelComponent level;
    private StrengthComponent strength;
    private VitalityComponent vitality;
    private MagicComponent magic;
    private AgilityComponent agility;
    private int[] perks; //ints: 0 = unlock, 1 = enable


    public MainStatusComponent() {
        this.level = new LevelComponent();
        this.strength = new StrengthComponent();
        this.vitality = new VitalityComponent();
        this.magic = new MagicComponent();
        this.agility = new AgilityComponent();
        this.perks = new int[PERK_LENGTH];
    }

    public MainStatusComponent(MainStatusComponent other) {
        this.level = other.level;
        this.strength = other.strength;
        this.vitality = other.vitality;
        this.magic = other.magic;
        this.agility = other.agility;
        this.perks = other.perks;
    }

    public static void setComponentType(ComponentType<EntityStore, MainStatusComponent> type) {
        TYPE = type;
    }

    public static ComponentType<EntityStore, MainStatusComponent> getComponentType() {
        return TYPE;
    }

    public String getMultiplier() {
        return BASE_MULTIPLIER;
    }

    public LevelComponent getLevelComponent() {
        return level;
    }

    public StrengthComponent getStrength() {
        return strength;
    }

    public VitalityComponent getVitality() {
        return vitality;
    }

    public AgilityComponent getAgility() {
        return agility;
    }

    public MagicComponent getMagic() {
        return magic;
    }

    private boolean isPerkValid(int FLAG_ID) {
        return perks != null
                && perks.length == PERK_LENGTH
                && FLAG_ID >= 0
                && FLAG_ID < PerkId.MAIN_CURRENT_PERK_COUNT;
    }

    public void setUnlocked(int FLAG_ID) {
        if(!isPerkValid(FLAG_ID)) throw new RuntimeException("Main Perk is not valid in setUnlocked");
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
            case PoisonPerk.NAME -> PerkId.MAIN_POISON_PERK;
            case StatusPerk.NAME -> PerkId.MAIN_STATUS_PERK;
            case HealingAreaPerk.NAME -> PerkId.MAIN_HEALING_AREA;
            default -> -1;
        };
    }

    public String getPerkNameById(int FLAG_ID) {
        return switch (FLAG_ID) {
            case PerkId.MAIN_POISON_PERK -> PoisonPerk.NAME;
            case PerkId.MAIN_STATUS_PERK -> StatusPerk.NAME;
            case PerkId.MAIN_HEALING_AREA -> HealingAreaPerk.NAME;
            default -> "unknown";
        };
    }

    public Perk getPerkById(int FLAG_ID) {
        return switch (FLAG_ID) {
            case PerkId.MAIN_POISON_PERK -> new PoisonPerk();
            case PerkId.MAIN_STATUS_PERK -> new StatusPerk();
            case PerkId.MAIN_HEALING_AREA -> new HealingAreaPerk();
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
        return new MainStatusComponent(this);
    }

    static {
        CODEC = BuilderCodec.builder(MainStatusComponent.class, MainStatusComponent::new)
                .append(new KeyedCodec<>("Level", LevelComponent.CODEC),
                        (data, value) -> data.level = value,
                        data -> data.level)
                .addValidator(Validators.nonNull())
                .add()
                .append(new KeyedCodec<>("Strength", StrengthComponent.CODEC),
                        (data, value) -> data.strength = value,
                        data -> data.strength)
                .addValidator(Validators.nonNull())
                .add()
                .append(new KeyedCodec<>("Magic", MagicComponent.CODEC),
                        (data, value) -> data.magic = value,
                        data -> data.magic)
                .addValidator(Validators.nonNull())
                .add()
                .append(new KeyedCodec<>("Vitality", VitalityComponent.CODEC),
                        (data, value) -> data.vitality = value,
                        data -> data.vitality)
                .addValidator(Validators.nonNull())
                .add()
                .append(new KeyedCodec<>("Agility", AgilityComponent.CODEC),
                        (data, value) -> data.agility = value,
                        data -> data.agility)
                .addValidator(Validators.nonNull())
                .add()
                .append(new KeyedCodec("Perks", Codec.INT_ARRAY),
                        (data, value) -> data.perks = value,
                        (data) -> data.perks)
                .addValidator(Validators.nonNull())
                .add()
                .build();
    }


}
