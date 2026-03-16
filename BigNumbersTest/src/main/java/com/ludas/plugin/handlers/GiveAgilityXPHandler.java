package com.ludas.plugin.handlers;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.util.EventTitleUtil;
import com.ludas.plugin.components.entity.AgilityComponent;
import com.ludas.plugin.components.entity.LevelComponent;
import com.ludas.plugin.components.entity.MainStatusComponent;
import com.ludas.plugin.events.GiveAgilityXPEvent;
import com.ludas.plugin.events.GiveMainStatusXPEvent;

import java.awt.*;
import java.math.BigDecimal;
import java.util.function.Consumer;

public class GiveAgilityXPHandler implements Consumer<GiveAgilityXPEvent> {
    @Override
    public void accept(GiveAgilityXPEvent event) {
        if (!event.ref().isValid()) return;
        var store = event.ref().getStore();
        MainStatusComponent mainStatus = store.getComponent(event.ref(), MainStatusComponent.getComponentType());
        if (mainStatus == null) return;
        AgilityComponent agility = mainStatus.getAgility();
        if(agility == null) return;
        LevelComponent level = agility.getLevelComponent();
        if(level == null) return;

        BigDecimal xp = agility.getDefaultExp();
        boolean leveledUp = level.addExperience(xp);
        GiveMainStatusXPEvent.dispatch(event.ref(), xp);

        Player player = store.getComponent(event.ref(), Player.getComponentType());
        PlayerRef playerRef = store.getComponent(event.ref(), PlayerRef.getComponentType());
        if(playerRef == null || player == null) return;
        player.sendMessage(Message.raw("Exp adicionada para agilidade: +" + xp).color(Color.ORANGE).bold(true));
        if(leveledUp) {
            EventTitleUtil.showEventTitleToPlayer(
                    playerRef,
                    Message.raw("Agility Up!"),
                    Message.raw("Level atual: " + level.getLevelString()),
                    true
            );
        }
    }
}
