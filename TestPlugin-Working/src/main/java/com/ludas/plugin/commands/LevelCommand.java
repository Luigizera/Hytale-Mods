package com.ludas.plugin.commands;

import com.ludas.plugin.components.LevelComponent;
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
        LevelComponent playerLevel = store.getComponent(ref, LevelComponent.getComponentType());
        if(playerLevel != null) {
            player.sendMessage(Message.raw("Level: " + playerLevel.getLevel() + " || " + playerLevel.getCurrentExperience() + " || " + playerLevel.getExperienceToNextLevel()));
        }
        else {
            store.putComponent(ref, LevelComponent.getComponentType(), new LevelComponent());
            player.sendMessage(Message.raw("Added level system"));
        }
    }
}
