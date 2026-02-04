package com.ludas.plugin.events;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.event.IEvent;
import com.hypixel.hytale.event.IEventDispatcher;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.ludas.plugin.clazz.MagnumOpus;

import javax.annotation.Nonnull;

public record RegisterPerksEvent(@Nonnull MagnumOpus magnumOpus) implements IEvent<Void> {
    public static void dispatch(MagnumOpus magnumOpus) {
        IEventDispatcher<RegisterPerksEvent, RegisterPerksEvent> dispatcher =
                HytaleServer.get().getEventBus().dispatchFor(RegisterPerksEvent.class);

        if (dispatcher.hasListener()) {
            dispatcher.dispatch(new RegisterPerksEvent(magnumOpus));
        }
    }
}
