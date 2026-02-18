package com.ludas.plugin.events;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.event.IEvent;
import com.hypixel.hytale.event.IEventDispatcher;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

public record GiveVitalityXPEvent(@Nonnull Ref<EntityStore> ref) implements IEvent<Void> {
    public static void dispatch(Ref<EntityStore> ref) {
        IEventDispatcher<GiveVitalityXPEvent, GiveVitalityXPEvent> dispatcher =
                HytaleServer.get().getEventBus().dispatchFor(GiveVitalityXPEvent.class);

        if (dispatcher.hasListener()) {
            dispatcher.dispatch(new GiveVitalityXPEvent(ref));
        }
    }
}
