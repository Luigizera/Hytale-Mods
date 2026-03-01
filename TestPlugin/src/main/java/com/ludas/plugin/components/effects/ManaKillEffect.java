package com.ludas.plugin.components.effects;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class ManaKillEffect implements Component<EntityStore> {
    //matar o inimigo regenera mana baseada no nível de Magia
    private static ComponentType<EntityStore, ManaKillEffect> TYPE;

    public static void setComponentType(ComponentType<EntityStore, ManaKillEffect> type) {
        TYPE = type;
    }
    public static ComponentType<EntityStore, ManaKillEffect> getComponentType() {
        return TYPE;
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        return null;
    }
}
