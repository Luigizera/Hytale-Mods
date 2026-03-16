package com.ludas.plugin.handlers;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.util.EventTitleUtil;
import com.ludas.plugin.components.entity.LevelComponent;
import com.ludas.plugin.components.entity.MainStatusComponent;
import com.ludas.plugin.components.entity.StrengthComponent;
import com.ludas.plugin.components.entity.VitalityComponent;
import com.ludas.plugin.events.GiveMainStatusXPEvent;
import com.ludas.plugin.events.GiveStrengthXPEvent;
import com.ludas.plugin.events.GiveVitalityXPEvent;

import java.awt.*;
import java.math.BigDecimal;
import java.util.function.Consumer;

public class GiveVitalityXPHandler implements Consumer<GiveVitalityXPEvent> {
    @Override
    public void accept(GiveVitalityXPEvent event) {
        if (!event.ref().isValid()) return;
        var store = event.ref().getStore();
        MainStatusComponent mainStatus = store.getComponent(event.ref(), MainStatusComponent.getComponentType());
        if (mainStatus == null) return;
        VitalityComponent vitality = mainStatus.getVitality();
        if(vitality == null) return;
        LevelComponent level = vitality.getLevelComponent();
        if(level == null) return;

        BigDecimal xp = vitality.getDefaultExp();
        boolean leveledUp = level.addExperience(xp);
        GiveMainStatusXPEvent.dispatch(event.ref(), xp);

        Player player = store.getComponent(event.ref(), Player.getComponentType());
        PlayerRef playerRef = store.getComponent(event.ref(), PlayerRef.getComponentType());
        if(playerRef == null || player == null) return;
        player.sendMessage(Message.raw("Exp adicionada para vitalidade: +" + xp).color(Color.ORANGE).bold(true));
        if(leveledUp) {
            EventTitleUtil.showEventTitleToPlayer(
                    playerRef,
                    Message.raw("Vitality Up!"),
                    Message.raw("Level atual: " + level.getLevelString()),
                    true
            );
            int vitalityModifier;
            try {
                vitalityModifier = Integer.parseInt(level.getLevelString());
            }
            catch (NumberFormatException e) {
                vitalityModifier = Integer.MAX_VALUE;
            }

            EntityStatMap statMap = store.getComponent(event.ref(), EntityStatMap.getComponentType());
            if (statMap == null) return;
            EntityStatValue health = statMap.get(DefaultEntityStatTypes.getHealth());
            if(health == null) return;
            player.sendMessage(Message.raw("+1 Vitalidade").color(Color.PINK).bold(true));
            statMap.putModifier(DefaultEntityStatTypes.getHealth(), "LudasVitality",
                    new StaticModifier(Modifier.ModifierTarget.MAX,
                            StaticModifier.CalculationType.ADDITIVE,
                            vitalityModifier));
        }
    }
}
