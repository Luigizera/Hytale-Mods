package com.ludas.plugin.events.damage;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.event.IEvent;
import com.hypixel.hytale.event.IEventDispatcher;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

public record StrengthExtraDamageEvent(@Nonnull Ref<EntityStore> attacker, @Nonnull Ref<EntityStore> target, Damage damage, CommandBuffer<EntityStore> commandBuffer) implements IEvent<Void> {
    public static void dispatch(@Nonnull Ref<EntityStore> attacker, Ref<EntityStore> target, Damage damage, CommandBuffer<EntityStore> commandBuffer) {
        IEventDispatcher<StrengthExtraDamageEvent, StrengthExtraDamageEvent> dispatcher =
                HytaleServer.get().getEventBus().dispatchFor(StrengthExtraDamageEvent.class);

        if (dispatcher.hasListener()) {
            dispatcher.dispatch(new StrengthExtraDamageEvent(attacker, target, damage, commandBuffer));
        }
    }
}
