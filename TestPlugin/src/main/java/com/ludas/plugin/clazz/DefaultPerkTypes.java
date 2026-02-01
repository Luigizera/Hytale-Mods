package com.ludas.plugin.clazz;

import com.hypixel.hytale.protocol.EntityStatResetBehavior;
import com.hypixel.hytale.protocol.io.ProtocolException;

public enum DefaultPerkTypes {
    STRENGTH("Strength", 0),
    AGILITY("Agility", 1),
    VITALITY("Vitality", 2),
    SPEECH("Speech",3);


    public static final DefaultPerkTypes[] VALUES = values();
    private final int value;
    public final String id;


    private DefaultPerkTypes(String id, int value) {
        this.value = value;
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

    public int getValue() {
        return this.value;
    }

    public static DefaultPerkTypes fromValue(int value) {
        if (value >= 0 && value < VALUES.length) {
            return VALUES[value];
        } else {
            throw ProtocolException.invalidEnumValue("DefaultPerkTypes", value);
        }
    }
}
