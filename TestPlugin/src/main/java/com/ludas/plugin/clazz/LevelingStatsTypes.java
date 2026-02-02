package com.ludas.plugin.clazz;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.protocol.io.ProtocolException;

public enum LevelingStatsTypes {
    UNKNOWN("Unknown"),
    STRENGTH("Strength"),
    AGILITY("Agility"),
    VITALITY("Vitality"),
    SPEECH("Speech"),
    CRIT_RATE("CritRate"),
    CRIT_DAMAGE("CritDamage");


    public static final LevelingStatsTypes[] VALUES = values();
    public final String id;


    private LevelingStatsTypes(String id) {
        this.id = id;
    }

    public String getStrength() {
        return STRENGTH.id;
    }

    public String getAgility() {
        return AGILITY.id;
    }

    public String getVitality() {
        return VITALITY.id;
    }

    public String getSpeech() {
        return SPEECH.id;
    }

    public String getId() {
        return id;
    }

    public static LevelingStatsTypes fromValue(int value) {
        if (value >= 0 && value < VALUES.length) {
            return VALUES[value];
        } else {
            throw ProtocolException.invalidEnumValue("LevelingStatsTypes", value);
        }
    }
}
