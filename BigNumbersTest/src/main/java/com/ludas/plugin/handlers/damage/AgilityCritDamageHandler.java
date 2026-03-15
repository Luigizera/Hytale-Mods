package com.ludas.plugin.handlers.damage;

import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.SoundCategory;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageSystems;
import com.hypixel.hytale.server.core.universe.world.ParticleUtil;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import com.ludas.plugin.clazz.Config;
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
        Random random = new Random();
        double rand = random.nextInt(0, 100) / 100d;
        float critChance = 0f;
        float critDamage = 0f;
        try {
            critChance = Float.parseFloat(agility.getCritChance());
        }
        catch (Exception e) {
            critChance = Float.MAX_VALUE;
        }

        try {
            critDamage = Float.parseFloat(agility.getCritDamage());
        }
        catch (Exception e) {
            critDamage = Float.MAX_VALUE;
        }

        if(rand <= critChance) {
            float dmg = event.damage().getAmount() * critDamage;
            if(critChance > 1) {
                dmg += event.damage().getAmount() * (critChance - 1);
            }
            Damage crit = new Damage(Damage.NULL_SOURCE, DamageCause.COMMAND, dmg);
            DamageSystems.executeDamage(event.target(), event.commandBuffer(), crit);
            Player attacker = store.getComponent(event.attacker(), Player.getComponentType());
            if (attacker == null) return;
            TransformComponent targetTransform = store.getComponent(event.target(), TransformComponent.getComponentType());
            if(targetTransform == null) return;
            TransformComponent attackerTransform = store.getComponent(event.attacker(), TransformComponent.getComponentType());
            if(attackerTransform == null) return;
            Vector3d targetPos = targetTransform.getPosition();
            Vector3d attackerPos = attackerTransform.getPosition();
            int index = SoundEvent.getAssetMap().getIndex(Config.SFX_AGILITY_CRIT);
            SoundUtil.playSoundEvent3dToPlayer(event.attacker(), index, SoundCategory.UI,
                    attackerPos.x, attackerPos.y, attackerPos.z, 0.8F, 12.0F, store);
            if(Config.isDamageCausePhysical(event.damage().getCause())) {
                for (int i = 0; i < 5; ++i) {
                    ParticleUtil.spawnParticleEffect(
                            Config.PARTICLE_BLACKFLASH_RED,
                            new Vector3d(
                                    targetPos.getX() + (random.nextFloat(0.1f, 1f) * (random.nextBoolean() ? -1 : 1)),
                                    targetPos.getY() + 0.5f + (random.nextFloat(0.1f, 1f) * (random.nextBoolean() ? -1 : 1)),
                                    targetPos.getZ() + (random.nextFloat(0.1f, 1f) * (random.nextBoolean() ? -1 : 1))
                            ),
                            event.commandBuffer());
                }
            }
            else {
                for (int i = 0; i < 5; ++i) {
                    ParticleUtil.spawnParticleEffect(
                            Config.PARTICLE_BLACKFLASH_BLUE,
                            new Vector3d(
                                    targetPos.getX() + (random.nextFloat(0.1f, 1f) * (random.nextBoolean() ? -1 : 1)),
                                    targetPos.getY() + 0.5f + (random.nextFloat(0.1f, 1f) * (random.nextBoolean() ? -1 : 1)),
                                    targetPos.getZ() + (random.nextFloat(0.1f, 1f) * (random.nextBoolean() ? -1 : 1))
                            ),
                            event.commandBuffer());
                }
            }
        }
    }
}
