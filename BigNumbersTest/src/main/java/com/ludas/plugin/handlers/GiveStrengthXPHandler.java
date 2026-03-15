package com.ludas.plugin.handlers;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.util.EventTitleUtil;
import com.ludas.plugin.TestPlugin;
import com.ludas.plugin.components.entity.LevelComponent;
import com.ludas.plugin.components.entity.MainStatusComponent;
import com.ludas.plugin.components.entity.StrengthComponent;
import com.ludas.plugin.events.GiveStrengthXPEvent;
import com.ludas.plugin.events.GiveMainStatusXPEvent;

import java.awt.*;
import java.math.BigDecimal;
import java.util.function.Consumer;

public class GiveStrengthXPHandler implements Consumer<GiveStrengthXPEvent> {
    @Override
    public void accept(GiveStrengthXPEvent event) {
        if (!event.ref().isValid()) return;
        var store = event.ref().getStore();
        MainStatusComponent mainStatus = store.getComponent(event.ref(), MainStatusComponent.getComponentType());
        if (mainStatus == null) return;
        StrengthComponent strength = mainStatus.getStrength();
        if(strength == null) return;
        LevelComponent level = strength.getLevelComponent();
        if(level == null) return;

        BigDecimal xp = strength.getDefaultExp();
        boolean leveledUp = level.addExperience(xp);
        GiveMainStatusXPEvent.dispatch(event.ref(), xp);

        Player player = store.getComponent(event.ref(), Player.getComponentType());
        PlayerRef playerRef = store.getComponent(event.ref(), PlayerRef.getComponentType());
        if(playerRef == null || player == null) return;
        player.sendMessage(Message.raw("Exp adicionada para força: +" + xp).color(Color.ORANGE).bold(true));
        if(leveledUp) {
            EventTitleUtil.showEventTitleToPlayer(
                    playerRef,
                    Message.raw("Strength Up!"),
                    Message.raw("Level atual: " + level.getLevel()),
                    true
            );
        }
    }
}
