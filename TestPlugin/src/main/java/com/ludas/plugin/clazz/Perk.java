package com.ludas.plugin.clazz;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;

import java.util.HashMap;
import java.util.Map;

public class Perk {
    public static final BuilderCodec<Perk> CODEC;
    private String id;
    private String description;
    private boolean enabled;

    public Perk() {
        this.id = "default";
        this.description = "default";
        this.enabled = false;
    }

    public Perk(String id, String description, boolean enabled) {
        this.id = id;
        this.description = description;
        this.enabled = enabled;
    }

    public String getId() {
        return id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getDescription() {
        return description;
    }

    public Map<Integer, StaticModifier> setupModifiers() {
        Map<Integer, StaticModifier> modifiers = new HashMap<>();

        /*int healthIndex = DefaultEntityStatTypes.getHealth();
        int staminaIndex = DefaultEntityStatTypes.getStamina();

        StaticModifier modifier1 = new StaticModifier(
                Modifier.ModifierTarget.MAX,
                StaticModifier.CalculationType.ADDITIVE,
                50.0f
        );

        StaticModifier modifier2 = new StaticModifier(
                Modifier.ModifierTarget.MAX,
                StaticModifier.CalculationType.ADDITIVE,
                50.0f
        );

        modifiers.put(healthIndex, modifier1);
        modifiers.put(staminaIndex, modifier2);*/

        return modifiers;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "Perk{id=" + id +
                ", enabled=" + enabled +
                ", description=" + description + "}";
    }

    static {

        CODEC =
                BuilderCodec
                        .builder(Perk.class, Perk::new)
                        .append(
                                new KeyedCodec<>("Id", Codec.STRING),
                                (component, value) -> component.id = value,
                                component -> component.id
                        )
                        .add()
                        .append(
                                new KeyedCodec<>("Enabled", Codec.BOOLEAN),
                                (component, value) -> component.enabled = value,
                                component -> component.enabled
                        )
                        .add()
                        .append(
                                new KeyedCodec<>("Description", Codec.STRING),
                                (component, value) -> component.description = value,
                                component -> component.description
                        )
                        .add()
                        .build();
    }
}