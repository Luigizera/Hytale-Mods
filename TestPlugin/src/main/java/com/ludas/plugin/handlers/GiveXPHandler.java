package com.ludas.plugin.handlers;

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

        var store = event.ref().getStore();

        LevelComponent level = store.getComponent(event.ref(), LevelComponent.getComponentType());
        if (level == null) return;


        float xp = event.amount();
        boolean leveledUp = level.addExperience(xp);

        Player player = store.getComponent(event.ref(), Player.getComponentType());
        if(player != null) {
            PlayerRef playerRef = store.getComponent(event.ref(), PlayerRef.getComponentType());
            if(playerRef == null) return;
            player.sendMessage(Message.raw("Exp adicionada: +" + xp).color(Color.ORANGE).bold(true));
            if(leveledUp) {
                EventTitleUtil.showEventTitleToPlayer(
                        playerRef,
                        Message.raw("Level Up!"),
                        Message.raw("Level atual: " + level.getLevel()),
                        true
                );
                switch (level.getLevel()) {
                    case 2:
                        level.putPerk(new PoisonPerk());
                        player.sendMessage(Message.raw("Novo perk desbloqueado: +" + PoisonPerk.ID).color(Color.ORANGE).bold(true));
                }
            }
        }
    }
}
