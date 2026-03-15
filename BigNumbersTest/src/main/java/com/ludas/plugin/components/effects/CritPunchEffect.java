package com.ludas.plugin.components.effects;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class CritPunchEffect implements Component<EntityStore> {
    //ataques corpo-a-corpo tem chance de causar dano critico baseado na agilidade
    private static ComponentType<EntityStore, CritPunchEffect> TYPE;

    public static void setComponentType(ComponentType<EntityStore, CritPunchEffect> type) {
        TYPE = type;
    }
    public static ComponentType<EntityStore, CritPunchEffect> getComponentType() {
        return TYPE;
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        return null;
    }
}
