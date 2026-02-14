package com.ludas.plugin.handlers;

import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageSystems;
import com.ludas.plugin.components.entity.MainStatusComponent;
import com.ludas.plugin.events.StrengthExtraDamageEvent;

import java.awt.*;
import java.util.function.Consumer;

public class StrengthExtraDamageHandler implements Consumer<StrengthExtraDamageEvent> {
    @Override
    public void accept(StrengthExtraDamageEvent event) {
        if (!event.attacker().isValid()) return;
        if (!event.target().isValid()) return;
        var store = event.attacker().getStore();

        MainStatusComponent mainStatus = store.getComponent(event.attacker(), MainStatusComponent.getComponentType());
        if (mainStatus == null) return;

        float dmg = event.damage().getAmount() * mainStatus.getStrength().getMultiplier();
        Damage strExtraDamage = new Damage(Damage.NULL_SOURCE, DamageCause.OUT_OF_WORLD, dmg);
        DamageSystems.executeDamage(event.target(), event.commandBuffer(), strExtraDamage);
    }
}
