package com.ludas.plugin.handlers;

import com.ludas.plugin.TestPlugin;
import com.ludas.plugin.components.LevelComponent;
import com.ludas.plugin.events.GiveXPEvent;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.util.EventTitleUtil;
import com.ludas.plugin.perks.PoisonPerk;

import java.awt.*;
import java.util.function.Consumer;

public class GiveXPHandler implements Consumer<GiveXPEvent> {
    @Override
    public void accept(GiveXPEvent event) {
        if (!event.ref().isValid()) return;
        TestPlugin.LOGGER.atInfo().log("Event Ref: " +event.ref());
        var store = event.ref().getStore();

        LevelComponent level = store.getComponent(event.ref(), LevelComponent.getComponentType());
        TestPlugin.LOGGER.atInfo().log("Level: " + level);
        if (level == null) return;

        float xp = event.amount();
        TestPlugin.LOGGER.atInfo().log("xp: " + xp);
        boolean leveledUp = level.addExperience(xp);
        TestPlugin.LOGGER.atInfo().log("leveledUp: " + leveledUp);

        Player player = store.getComponent(event.ref(), Player.getComponentType());
        PlayerRef playerRef = store.getComponent(event.ref(), PlayerRef.getComponentType());
        if(playerRef == null || player == null) return;
        player.sendMessage(Message.raw("Exp adicionada: +" + xp).color(Color.ORANGE).bold(true));
        if(leveledUp) {
            EventTitleUtil.showEventTitleToPlayer(
                    playerRef,
                    Message.raw("Level Up!"),
                    Message.raw("Level atual: " + level.getLevel()),
                    true
            );
        }
    }
}
