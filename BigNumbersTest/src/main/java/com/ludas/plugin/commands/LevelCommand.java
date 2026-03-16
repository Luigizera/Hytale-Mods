package com.ludas.plugin.commands;

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
            player.sendMessage(Message.raw("Main Level: " + mainStatus.getLevelComponent().getLevelString()
                    + " || " + mainStatus.getLevelComponent().getCurrentExperienceString()
                    + " || " + mainStatus.getLevelComponent().getExperienceToNextLevel()));
            player.sendMessage(Message.raw("Strength Level: " + mainStatus.getStrength().getLevelComponent().getLevelString()
                    + " || " + mainStatus.getStrength().getLevelComponent().getCurrentExperienceString()
                    + " || " + mainStatus.getStrength().getLevelComponent().getExperienceToNextLevel()));
            player.sendMessage(Message.raw("Magic Level: " + mainStatus.getMagic().getLevelComponent().getLevelString()
                    + " || " + mainStatus.getMagic().getLevelComponent().getCurrentExperienceString()
                    + " || " + mainStatus.getMagic().getLevelComponent().getExperienceToNextLevel()));
            player.sendMessage(Message.raw("Agility Level: " + mainStatus.getAgility().getLevelComponent().getLevelString()
                    + " || " + mainStatus.getAgility().getLevelComponent().getCurrentExperienceString()
                    + " || " + mainStatus.getAgility().getLevelComponent().getExperienceToNextLevel()));
            player.sendMessage(Message.raw("Vitality Level: " + mainStatus.getVitality().getLevelComponent().getLevelString()
                    + " || " + mainStatus.getVitality().getLevelComponent().getCurrentExperienceString()
                    + " || " + mainStatus.getVitality().getLevelComponent().getExperienceToNextLevel()));
        }
        else {
            store.putComponent(ref, MainStatusComponent.getComponentType(), new MainStatusComponent());
            player.sendMessage(Message.raw("Added level system"));
        }
    }
}
