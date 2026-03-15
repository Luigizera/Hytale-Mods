package com.ludas.plugin.handlers.damage;

import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageSystems;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.ludas.plugin.components.entity.MagicComponent;
import com.ludas.plugin.components.entity.MainStatusComponent;
import com.ludas.plugin.events.damage.MagicManaDamageEvent;

import java.util.function.Consumer;

public class MagicManaDamageHandler implements Consumer<MagicManaDamageEvent> {
    @Override
    public void accept(MagicManaDamageEvent event) {
        if (!event.attacker().isValid()) return;
        if (!event.target().isValid()) return;
        var store = event.attacker().getStore();

        MainStatusComponent mainStatus = store.getComponent(event.attacker(), MainStatusComponent.getComponentType());
        if (mainStatus == null) return;
        EntityStatMap statMap = store.getComponent(event.attacker(), EntityStatMap.getComponentType());
        if (statMap == null) return;
        MagicComponent magic = mainStatus.getMagic();
        if(magic == null) return;
        EntityStatValue attackerMana = statMap.get(DefaultEntityStatTypes.getMana());
        if(attackerMana == null) return;

        if(attackerMana.getMax() > 0) {
            float dmg = attackerMana.getMax() * magic.getDamageMultiplier();
            Damage extraDmg = new Damage(Damage.NULL_SOURCE, DamageCause.COMMAND, dmg);
            DamageSystems.executeDamage(event.target(), event.commandBuffer(), extraDmg);
        }
    }
}
