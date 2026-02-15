package com.ludas.plugin.events.damage;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.event.IEvent;
import com.hypixel.hytale.event.IEventDispatcher;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

public record MagicManaDamageEvent(@Nonnull Ref<EntityStore> attacker, @Nonnull Ref<EntityStore> target, CommandBuffer<EntityStore> commandBuffer) implements IEvent<Void> {
    public static void dispatch(@Nonnull Ref<EntityStore> attacker, Ref<EntityStore> target, CommandBuffer<EntityStore> commandBuffer) {
        IEventDispatcher<MagicManaDamageEvent, MagicManaDamageEvent> dispatcher =
                HytaleServer.get().getEventBus().dispatchFor(MagicManaDamageEvent.class);

        if (dispatcher.hasListener()) {
            dispatcher.dispatch(new MagicManaDamageEvent(attacker, target, commandBuffer));
        }
    }
}
