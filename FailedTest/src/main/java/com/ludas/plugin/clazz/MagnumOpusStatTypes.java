package com.ludas.plugin.clazz;

import com.hypixel.hytale.protocol.io.ProtocolException;

public enum MagnumOpusStatTypes {
    UNKNOWN("Unknown"),
    STRENGTH("Strength"),
    AGILITY("Agility"),
    VITALITY("Vitality"),
    SPEECH("Speech"),
    CRIT_RATE("CritRate"),
    CRIT_DAMAGE("CritDamage");

    public static final MagnumOpusStatTypes[] VALUES = values();
    public final String id;
    MagnumOpusStatTypes(String id) {
        this.id = id;
    }


    public static MagnumOpusStatTypes fromValue(int value) {
        if (value >= 0 && value < VALUES.length) {
            return VALUES[value];
        } else {
            throw ProtocolException.invalidEnumValue("LevelingStatsTypes", value);
        }
    }
}
