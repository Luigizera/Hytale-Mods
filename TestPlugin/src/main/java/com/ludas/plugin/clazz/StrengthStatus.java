package com.ludas.plugin.clazz;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.ludas.plugin.perks.PoisonPerk;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class StrengthStatus extends Status {
    public static final BuilderCodec<StrengthStatus> CODEC;
    public static final String ID = "Strength";

    StrengthStatus() {
    }

    StrengthStatus(String id, float modifier, Map<String, Perk> perks, Level level) {
        super(id, modifier, perks, level);
    }


    static {
        CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)
                BuilderCodec.builder(StrengthStatus.class, StrengthStatus::new, Status.BASE_CODEC))).build();

    }
}
