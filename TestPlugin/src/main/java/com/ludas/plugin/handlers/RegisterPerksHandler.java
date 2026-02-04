package com.ludas.plugin.handlers;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.util.EventTitleUtil;
import com.ludas.plugin.clazz.MagnumOpus;
import com.ludas.plugin.clazz.MagnumOpusStatTypes;
import com.ludas.plugin.clazz.Status;
import com.ludas.plugin.events.GiveXPEvent;
import com.ludas.plugin.events.RegisterPerksEvent;

import java.awt.*;
import java.util.function.Consumer;

public class RegisterPerksHandler implements Consumer<RegisterPerksEvent> {
    @Override
    public void accept(RegisterPerksEvent event) {
        //TODO: ARRUMAR ISSO PARA TODOS OS STATUS
        Status status = event.magnumOpus().getStat(MagnumOpusStatTypes.STRENGTH.id);

        if(status != null) {
            status.putPerk();
        }
    }
}
