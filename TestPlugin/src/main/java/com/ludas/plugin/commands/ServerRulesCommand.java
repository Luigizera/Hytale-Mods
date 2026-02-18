package com.ludas.plugin.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.concurrent.CompletableFuture;

public class ServerRulesCommand extends AbstractAsyncCommand {
    public ServerRulesCommand() {
        super("rules", "server.commands.ludas.rules.desc", false);
    }

    @NonNullDecl
    @Override
    protected CompletableFuture<Void> executeAsync(@NonNullDecl CommandContext commandContext) {
        commandContext.sendMessage(Message.translation("server.commands.ludas.rules.message"));
        return CompletableFuture.completedFuture(null);
    }
}
