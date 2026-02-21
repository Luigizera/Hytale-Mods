package com.ludas.plugin.commands;

import com.ludas.plugin.components.effects.DoTEffect;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.FlagArg;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractTargetPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.awt.*;

public class PoisonCommand extends AbstractTargetPlayerCommand {
    private final FlagArg debugArg;

    public PoisonCommand() {
        super("poison", "server.commands.ludas.poison.desc");
        this.debugArg = this.withFlagArg("debug", "server.commands.ludas.poison.debug.arg.desc");
    }

    @Override
    protected void execute(@NonNullDecl CommandContext commandContext, @NullableDecl Ref<EntityStore> ref,
                           @NonNullDecl Ref<EntityStore> ref1, @NonNullDecl PlayerRef playerRef,
                           @NonNullDecl World world, @NonNullDecl Store<EntityStore> store) {
        if (this.debugArg.get(commandContext) == true) {
            commandContext.sendMessage(Message.translation("server.commands.ludas.poison.debug.arg.message"));
        }
        Player player = store.getComponent(ref, Player.getComponentType());
        DoTEffect damageOverTime = new DoTEffect(3f, 0.5f, 8);
        store.addComponent(ref, DoTEffect.getComponentType(), damageOverTime);
        player.sendMessage(Message.translation("server.commands.ludas.poison.message").color(Color.GREEN).bold(true));
    }
}
