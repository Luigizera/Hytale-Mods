package com.ludas.plugin.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.FlagArg;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class PlayerInfoCommand extends AbstractPlayerCommand {
    private final FlagArg debugArg;

    public PlayerInfoCommand() {
        super("playerinfo", "server.commands.ludas.playerinfo.desc", false);
        this.debugArg = this.withFlagArg("debug", "server.commands.ludas.entity.debug.arg.desc");
    }

    @Override
    protected void execute(@NonNullDecl CommandContext context,
                           @NonNullDecl Store<EntityStore> store,
                           @NonNullDecl Ref<EntityStore> ref,
                           @NonNullDecl PlayerRef playerRef,
                           @NonNullDecl World world) {
        Player player = store.getComponent(ref, Player.getComponentType());
        UUIDComponent component = store.getComponent(ref, UUIDComponent.getComponentType());
        TransformComponent transform = store.getComponent(ref, TransformComponent.getComponentType());


        assert player != null : "Player not found";
        assert component != null : "Component not found";
        assert transform != null : "Transform not found";
        if(debugArg.get(context)) {
            player.sendMessage(Message.raw("Debug: " + debugArg.get(context)));
        }

        player.sendMessage(Message.raw("Player UUID: " + component.getUuid()));
        player.sendMessage(Message.raw("Transform: " + transform.getPosition()));
    }
}
