package com.ludas.plugin.events;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.event.IEvent;
import com.hypixel.hytale.event.IEventDispatcher;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import java.math.BigDecimal;

public record GiveMainStatusXPEvent(@Nonnull Ref<EntityStore> ref, BigDecimal amount) implements IEvent<Void> {
    public static void dispatch(Ref<EntityStore> ref, BigDecimal amount) {
        IEventDispatcher<GiveMainStatusXPEvent, GiveMainStatusXPEvent> dispatcher =
                HytaleServer.get().getEventBus().dispatchFor(GiveMainStatusXPEvent.class);

        if (dispatcher.hasListener()) {
            dispatcher.dispatch(new GiveMainStatusXPEvent(ref, amount));
        }
    }
}
