package com.ludas.plugin.components.effects;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class FrenzyEffect implements Component<EntityStore> {
    //pega 20% da vida maxima do atacante e utiliza como dano extra, além de reduzir a vida atual do atacante em 20%
    private static final String ASSET_NAME = "Ludas_Frenzy";
    private static final float MULTIPLIER = 0.2f;
    private static ComponentType<EntityStore, FrenzyEffect> TYPE;

    public static void setComponentType(ComponentType<EntityStore, FrenzyEffect> type) {
        TYPE = type;
    }
    public static ComponentType<EntityStore, FrenzyEffect> getComponentType() {
        return TYPE;
    }

    public float getMultiplier() {
        return MULTIPLIER;
    }

    public static String getAssetName() {
        return ASSET_NAME;
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        return null;
    }
}
