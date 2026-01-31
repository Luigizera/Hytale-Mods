package com.ludas.plugin.events;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.event.IEvent;
import com.hypixel.hytale.event.IEventDispatcher;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public record AddLevelSystemEvent(Ref<EntityStore> ref) implements IEvent<Void> {
    public static void dispatch(Ref<EntityStore> ref) {
        IEventDispatcher<AddLevelSystemEvent, AddLevelSystemEvent> dispatcher =
                HytaleServer.get().getEventBus().dispatchFor(AddLevelSystemEvent.class);

        if (dispatcher.hasListener()) {
            dispatcher.dispatch(new AddLevelSystemEvent(ref));
        }
    }
}
