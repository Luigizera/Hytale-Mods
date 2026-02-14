package com.ludas.plugin.handlers;

import com.ludas.plugin.components.entity.MainStatusComponent;
import com.ludas.plugin.events.GiveMainStatusXPEvent;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.util.EventTitleUtil;

import java.awt.*;
import java.util.function.Consumer;

public class GiveMainStatusXPHandler implements Consumer<GiveMainStatusXPEvent> {
    @Override
    public void accept(GiveMainStatusXPEvent event) {
        if (!event.ref().isValid()) return;
        var store = event.ref().getStore();

        MainStatusComponent mainStatus = store.getComponent(event.ref(), MainStatusComponent.getComponentType());
        if (mainStatus == null) return;

        float xp = event.amount() / 10f;
        boolean leveledUp = mainStatus.getLevelComponent().addExperience(xp);

        Player player = store.getComponent(event.ref(), Player.getComponentType());
        PlayerRef playerRef = store.getComponent(event.ref(), PlayerRef.getComponentType());
        if(playerRef == null || player == null) return;
        player.sendMessage(Message.raw("Exp adicionada para Main: +" + xp).color(Color.ORANGE).bold(true));
        if(leveledUp) {
            EventTitleUtil.showEventTitleToPlayer(
                    playerRef,
                    Message.raw("Main Status Up!"),
                    Message.raw("Level atual: " + mainStatus.getLevelComponent().getLevel()),
                    true
            );
        }
    }
}
