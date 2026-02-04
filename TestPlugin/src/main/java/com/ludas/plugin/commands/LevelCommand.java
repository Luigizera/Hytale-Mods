package com.ludas.plugin.commands;

import com.ludas.plugin.clazz.MagnumOpus;
import com.ludas.plugin.clazz.MagnumOpusStatTypes;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class LevelCommand extends AbstractPlayerCommand {

    public LevelCommand() {
        super("level", "description");
    }

    @Override
    protected void execute(@NonNullDecl CommandContext commandContext,
                           @NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref,
                           @NonNullDecl PlayerRef playerRef, @NonNullDecl World world) {

        Player player = store.getComponent(ref, Player.getComponentType());
        assert player != null : "Null player in PlayerLevelDataCommand";
        MagnumOpus magnumOpus = store.getComponent(ref, MagnumOpus.getComponentType());
        if(magnumOpus != null) {
            player.sendMessage(Message.raw("Level: " + magnumOpus.getStat(MagnumOpusStatTypes.STRENGTH.id).getLevel().getCurrentLevel() + " || " + magnumOpus.getStat(MagnumOpusStatTypes.STRENGTH.id).getLevel().getCurrentExperience() + " || " + magnumOpus.getStat(MagnumOpusStatTypes.STRENGTH.id).getLevel().getExperienceToNextLevel()));
        }
        else {
            store.putComponent(ref, MagnumOpus.getComponentType(), new MagnumOpus());
            player.sendMessage(Message.raw("Added MagnumOpus system"));
        }
    }
}
