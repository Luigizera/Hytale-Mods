package com.ludas.plugin.commands.collection;

import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

import java.util.List;

public class LudasCommandCollection extends AbstractCommandCollection {

    public LudasCommandCollection(List<AbstractCommand> abstractCommands) {
        super("ludas", "server.commands.ludas.desc");
        for(int i = 0; i < abstractCommands.size(); ++i) {
            addSubCommand(abstractCommands.get(i));
        }
    }
}
