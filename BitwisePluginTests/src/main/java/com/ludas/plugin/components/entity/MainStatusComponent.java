package com.ludas.plugin.components.entity;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class MainStatusComponent implements Component<EntityStore> {
    public static final BuilderCodec<MainStatusComponent> CODEC;
    private static ComponentType<EntityStore, MainStatusComponent> TYPE;
    private LevelComponent level;
    private StrengthComponent strength;

    public MainStatusComponent() {
        this.level = new LevelComponent();
        this.strength = new StrengthComponent();
    }

    public MainStatusComponent(MainStatusComponent other) {
        this.level = other.level;
        this.strength = other.strength;
    }

    public static void setComponentType(ComponentType<EntityStore, MainStatusComponent> type) {
        TYPE = type;
    }

    public static ComponentType<EntityStore, MainStatusComponent> getComponentType() {
        return TYPE;
    }

    public LevelComponent getLevelComponent() {
        return level;
    }

    public StrengthComponent getStrength() {
        return strength;
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
                .build();
    }


}
