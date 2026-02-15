package com.ludas.plugin.handlers.damage;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageSystems;
import com.ludas.plugin.components.entity.AgilityComponent;
import com.ludas.plugin.components.entity.MainStatusComponent;
import com.ludas.plugin.events.damage.AgilityCritDamageEvent;

import java.awt.*;
import java.util.Random;
import java.util.function.Consumer;

public class AgilityCritDamageHandler implements Consumer<AgilityCritDamageEvent> {
    @Override
    public void accept(AgilityCritDamageEvent event) {
        if (!event.attacker().isValid()) return;
        if (!event.target().isValid()) return;
        var store = event.attacker().getStore();

        MainStatusComponent mainStatus = store.getComponent(event.attacker(), MainStatusComponent.getComponentType());
        if (mainStatus == null) return;
        AgilityComponent agility = mainStatus.getAgility();
        if(agility == null) return;

        double rand = new Random().nextInt(0, 100) / 100d;
        float critChance = agility.getCritChance();
        float critDamage = agility.getCritDamage();

        if(rand <= critChance) {
            float dmg = event.damage().getAmount() * critDamage;
            if(critChance > 1) {
                dmg += event.damage().getAmount() * (critChance - 1);
            }
            Damage crit = new Damage(Damage.NULL_SOURCE, DamageCause.OUT_OF_WORLD, dmg);
            DamageSystems.executeDamage(event.target(), event.commandBuffer(), crit);
            Player attacker = store.getComponent(event.attacker(), Player.getComponentType());
            if (attacker == null) return;
            attacker.sendMessage(Message.raw("CRIT").bold(true).color(Color.PINK));
        }
        /*
        TransformComponent transform = store.getComponent(event.target(), TransformComponent.getComponentType());
        if(transform == null) return;
        Vector3d pos = transform.getPosition();
        Vector3d defaultPos = transform.getPosition();
        /*ParticleUtil.spawnParticleEffect("RifleShooting",
                pos.x, pos.y, pos.z,
                0, 0, 0,
                20, new com.hypixel.hytale.protocol.Color((byte)20, (byte)0, (byte)0),
                (Ref)null, SpatialResource.getThreadLocalReferenceList(), event.commandBuffer());
        for(int i = 0; i < 3; ++i) {
            double offset = new Random().nextInt(1, 10);
            pos.x += offset/10f;
            pos.y += 0.5 + (offset/10f);
            pos.z += offset/10f;
            ParticleUtil.spawnParticleEffect("RifleShooting", pos, event.commandBuffer());
            pos = defaultPos;
        }*/
    }
}
