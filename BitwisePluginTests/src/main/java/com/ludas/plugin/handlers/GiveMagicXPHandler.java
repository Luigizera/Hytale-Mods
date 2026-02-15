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
import com.ludas.plugin.components.entity.MagicComponent;
import com.ludas.plugin.components.entity.MainStatusComponent;
import com.ludas.plugin.events.GiveMagicXPEvent;
import com.ludas.plugin.events.GiveMainStatusXPEvent;
import com.ludas.plugin.events.GiveStrengthXPEvent;

import java.awt.*;
import java.util.function.Consumer;

public class GiveMagicXPHandler implements Consumer<GiveMagicXPEvent> {
    @Override
    public void accept(GiveMagicXPEvent event) {
        if (!event.ref().isValid()) return;
        var store = event.ref().getStore();
        MainStatusComponent mainStatus = store.getComponent(event.ref(), MainStatusComponent.getComponentType());
        if (mainStatus == null) return;
        MagicComponent magic = mainStatus.getMagic();
        if(magic == null) return;
        LevelComponent level = magic.getLevelComponent();
        if(level == null) return;

        float xp = event.amount() / 10f;
        boolean leveledUp = level.addExperience(xp);
        GiveMainStatusXPEvent.dispatch(event.ref(), xp);

        Player player = store.getComponent(event.ref(), Player.getComponentType());
        PlayerRef playerRef = store.getComponent(event.ref(), PlayerRef.getComponentType());
        if(playerRef == null || player == null) return;
        player.sendMessage(Message.raw("Exp adicionada para magia: +" + xp).color(Color.ORANGE).bold(true));
        if(leveledUp) {
            EventTitleUtil.showEventTitleToPlayer(
                    playerRef,
                    Message.raw("Magic Up!"),
                    Message.raw("Level atual: " + level.getLevel()),
                    true
            );
            EntityStatMap statMap = store.getComponent(event.ref(), EntityStatMap.getComponentType());
            if (statMap == null) return;
            EntityStatValue mana = statMap.get(DefaultEntityStatTypes.getMana());
            if(mana == null) return;
            player.sendMessage(Message.raw("+1 Mana").color(Color.CYAN).bold(true));
            statMap.putModifier(DefaultEntityStatTypes.getMana(), "LudasMagic",
                    new StaticModifier(Modifier.ModifierTarget.MAX,
                            StaticModifier.CalculationType.ADDITIVE,
                            level.getLevel()));
        }
    }
}
