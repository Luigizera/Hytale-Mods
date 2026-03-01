package com.ludas.plugin.commands;

import com.ludas.plugin.components.entity.LevelComponent;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.ludas.plugin.components.entity.MainStatusComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class LevelCommand extends AbstractPlayerCommand {

    public LevelCommand() {
        super("level", "description");
    }

    @Override
    protected void execute(@NonNullDecl CommandContext context,
                           @NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref,
                           @NonNullDecl PlayerRef playerRef, @NonNullDecl World world) {

        Player player = store.getComponent(ref, Player.getComponentType());
        if(player == null) return;
        MainStatusComponent mainStatus = store.getComponent(ref, MainStatusComponent.getComponentType());
        if(mainStatus != null) {
            player.sendMessage(Message.raw("Level: " + mainStatus.getStrength().getLevelComponent().getLevel()
                    + " || " + mainStatus.getStrength().getLevelComponent().getCurrentExperience()
                    + " || " + mainStatus.getStrength().getLevelComponent().getExperienceToNextLevel()));
        }
        else {
            store.putComponent(ref, MainStatusComponent.getComponentType(), new MainStatusComponent());
            player.sendMessage(Message.raw("Added level system"));
        }
    }
}
