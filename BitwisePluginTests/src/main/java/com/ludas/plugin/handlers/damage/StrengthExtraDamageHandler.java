package com.ludas.plugin.handlers.damage;

import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageSystems;
import com.ludas.plugin.components.entity.MainStatusComponent;
import com.ludas.plugin.components.entity.StrengthComponent;
import com.ludas.plugin.events.damage.StrengthExtraDamageEvent;

import java.util.function.Consumer;

public class StrengthExtraDamageHandler implements Consumer<StrengthExtraDamageEvent> {
    @Override
    public void accept(StrengthExtraDamageEvent event) {
        if (!event.attacker().isValid()) return;
        if (!event.target().isValid()) return;
        var store = event.attacker().getStore();

        MainStatusComponent mainStatus = store.getComponent(event.attacker(), MainStatusComponent.getComponentType());
        if (mainStatus == null) return;
        StrengthComponent strength = mainStatus.getStrength();
        if(strength == null) return;

        float dmg = event.damage().getAmount() * strength.getMultiplier();
        Damage extraDmg = new Damage(Damage.NULL_SOURCE, DamageCause.OUT_OF_WORLD, dmg);
        DamageSystems.executeDamage(event.target(), event.commandBuffer(), extraDmg);
    }
}
