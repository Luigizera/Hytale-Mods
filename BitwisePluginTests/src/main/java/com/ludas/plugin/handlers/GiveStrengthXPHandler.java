package com.ludas.plugin.handlers;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.util.EventTitleUtil;
import com.ludas.plugin.TestPlugin;
import com.ludas.plugin.components.entity.LevelComponent;
import com.ludas.plugin.components.entity.MainStatusComponent;
import com.ludas.plugin.events.GiveStrengthXPEvent;
import com.ludas.plugin.events.GiveXPEvent;

import java.awt.*;
import java.util.function.Consumer;

public class GiveStrengthXPHandler implements Consumer<GiveStrengthXPEvent> {
    @Override
    public void accept(GiveStrengthXPEvent event) {
        if (!event.ref().isValid()) return;
        var store = event.ref().getStore();

        MainStatusComponent mainStatus = store.getComponent(event.ref(), MainStatusComponent.getComponentType());
        if (mainStatus == null) return;

        float xp = event.amount();
        boolean leveledUp = mainStatus.getStrength().getLevelComponent().addExperience(xp);

        Player player = store.getComponent(event.ref(), Player.getComponentType());
        PlayerRef playerRef = store.getComponent(event.ref(), PlayerRef.getComponentType());
        if(playerRef == null || player == null) return;
        player.sendMessage(Message.raw("Exp adicionada para força: +" + xp).color(Color.ORANGE).bold(true));
        if(leveledUp) {
            EventTitleUtil.showEventTitleToPlayer(
                    playerRef,
                    Message.raw("Level Up!"),
                    Message.raw("Level atual: " + mainStatus.getStrength().getLevelComponent().getLevel()),
                    true
            );
        }
    }
}
