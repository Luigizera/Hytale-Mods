package com.ludas.plugin.events;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.event.IEvent;
import com.hypixel.hytale.event.IEventDispatcher;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

public record GiveMagicXPEvent(@Nonnull Ref<EntityStore> ref, float amount) implements IEvent<Void> {
    public static void dispatch(Ref<EntityStore> ref, float amount) {
        IEventDispatcher<GiveMagicXPEvent, GiveMagicXPEvent> dispatcher =
                HytaleServer.get().getEventBus().dispatchFor(GiveMagicXPEvent.class);

        if (dispatcher.hasListener()) {
            dispatcher.dispatch(new GiveMagicXPEvent(ref, amount));
        }
    }
}
