package com.ludas.plugin.clazz;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.ludas.plugin.components.LevelComponent;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.Map;

public class LevelingStatusComponent implements Component<EntityStore> {


    private Map<String, Perk> perks;
    private LevelComponent level;

    LevelingStatusComponent() {

    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        return null;
    }

    @NullableDecl
    @Override
    public Component<EntityStore> cloneSerializable() {
        return Component.super.cloneSerializable();
    }
}
