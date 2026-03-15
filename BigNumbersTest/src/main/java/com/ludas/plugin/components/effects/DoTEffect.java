package com.ludas.plugin.components.effects;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class DoTEffect implements Component<EntityStore> {

    private float damagePerTick;
    private float tickInterval;
    private int remainingTicks;
    private float elapsedTime;
    private static ComponentType<EntityStore, DoTEffect> TYPE;

    public static void setComponentType(ComponentType<EntityStore, DoTEffect> type) {
        TYPE = type;
    }
    public static ComponentType<EntityStore, DoTEffect> getComponentType() {
        return TYPE;
    }

    public DoTEffect() {
        this(5f, 1.0f, 10);
    }

    public DoTEffect(float damagePerTick, float tickInterval, int totalTicks) {
        this.damagePerTick = damagePerTick;
        this.tickInterval = tickInterval;
        this.remainingTicks = totalTicks;
        this.elapsedTime = 0f;
    }

    public DoTEffect(DoTEffect other) {
        this.damagePerTick = other.damagePerTick;
        this.tickInterval = other.tickInterval;
        this.remainingTicks = other.remainingTicks;
        this.elapsedTime = other.elapsedTime;
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        return new DoTEffect(this);
    }

    @NullableDecl
    @Override
    public Component<EntityStore> cloneSerializable() {
        return Component.super.cloneSerializable();
    }

    public float getDamagePerTick() {
        return damagePerTick;
    }

    public float getTickInterval() {
        return tickInterval;
    }

    public int getRemainingTicks() {
        return remainingTicks;
    }

    public float getElapsedTime() {
        return elapsedTime;
    }

    public void addElapsedTime(float dt) {
        this.elapsedTime += dt;
    }

    public void resetElapsedTime() {
        this.elapsedTime = 0f;
    }

    public void decrementRemainingTicks() {
        this.remainingTicks--;
    }

    public boolean isExpired() {
        return this.remainingTicks <= 0;
    }
}
