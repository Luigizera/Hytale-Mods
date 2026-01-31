package com.ludas.plugin.handlers;

import com.ludas.plugin.components.LevelComponent;
import com.ludas.plugin.events.AddLevelSystemEvent;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;

import java.awt.*;
import java.util.function.Consumer;

public class AddLevelSystemHandler implements Consumer<AddLevelSystemEvent> {
    @Override
    public void accept(AddLevelSystemEvent event) {
        if (!event.ref().isValid()) return;

        var ref = event.ref();
        var store = event.ref().getStore();

        LevelComponent level = store.getComponent(ref, LevelComponent.getComponentType());
        if(level != null) return;

        store.putComponent(ref, LevelComponent.getComponentType(), new LevelComponent());

        Player player = store.getComponent(ref, Player.getComponentType());
        if(player != null) {
            player.sendMessage(Message.raw("Adicionado Sistema de Level").color(Color.ORANGE));
        }
    }
}
