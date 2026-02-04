package com.ludas.plugin.handlers;

import com.ludas.plugin.clazz.MagnumOpus;
import com.ludas.plugin.clazz.MagnumOpusStatTypes;
import com.ludas.plugin.events.GiveXPEvent;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.util.EventTitleUtil;

import java.awt.*;
import java.util.function.Consumer;

public class GiveXPHandler implements Consumer<GiveXPEvent> {
    @Override
    public void accept(GiveXPEvent event) {
        if (!event.ref().isValid()) return;

        var store = event.ref().getStore();

        MagnumOpus magnumOpus = store.getComponent(event.ref(), MagnumOpus.getComponentType());
        if (magnumOpus == null) return;

        //TODO: ARRUMAR ISSO PARA TODOS OS STATUS
        float xp = event.amount();
        boolean leveledUp = magnumOpus.getStat(MagnumOpusStatTypes.STRENGTH.id).getLevel().addExperience(xp);

        Player player = store.getComponent(event.ref(), Player.getComponentType());
        if(player != null) {
            PlayerRef playerRef = store.getComponent(event.ref(), PlayerRef.getComponentType());
            if(playerRef == null) return;
            player.sendMessage(Message.raw("Exp adicionada: +" + xp).color(Color.ORANGE).bold(true));
            if(leveledUp) {
                EventTitleUtil.showEventTitleToPlayer(
                        playerRef,
                        Message.raw("Level Up!"),
                        Message.raw("Level atual: " + magnumOpus.getStat(MagnumOpusStatTypes.STRENGTH.id).getLevel().getCurrentLevel()),
                        true
                );
            }
        }
    }
}
