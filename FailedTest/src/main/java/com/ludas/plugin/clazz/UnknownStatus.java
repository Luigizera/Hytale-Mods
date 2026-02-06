package com.ludas.plugin.clazz;

import com.ludas.plugin.perks.PoisonPerk;

import java.util.HashMap;

public class UnknownStatus extends Status {
    public static final float MODIFIER = 1f;

    public UnknownStatus() {
        super(MagnumOpusStatTypes.UNKNOWN.id);
        super.modifier = MODIFIER;
    }

    @Override
    public HashMap<String, Perk> registerPerks() {
        HashMap<String, Perk> perks = new HashMap<>();
        perks.put(PoisonPerk.ID, new PoisonPerk());
        this.perks = perks;
        return perks;
    }
}
