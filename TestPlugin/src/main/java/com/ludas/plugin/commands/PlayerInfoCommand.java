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
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatsModule;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.ludas.plugin.TestPlugin;
import com.ludas.plugin.clazz.Perk;
import com.ludas.plugin.components.LevelComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.Map;

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

        if(debugArg.get(context)) {
            LevelComponent level = store.getComponent(ref, LevelComponent.getComponentType());
            if(level == null) return;
            EntityStatMap statMap = store.getComponent(ref, EntityStatsModule.get().getEntityStatMapComponentType());
            if(statMap == null) return;
            Perk outsidePerk = new Perk();
            Map<String, Perk> perks = level.getPerks(); // TODO: ERRO LOGICO GIGANTESCO
            level.putPerk(outsidePerk);
            for(var perk : perks.entrySet()) {
                TestPlugin.LOGGER.atInfo().log("ENTROU NO LOOP");
                Perk currentPerk = perk.getValue();
                if(currentPerk.isEnabled()) {
                    TestPlugin.LOGGER.atInfo().log("ISENABLED");
                    Map<Integer, StaticModifier> modifiers = currentPerk.setupModifiers();
                    if(modifiers == null) {
                        TestPlugin.LOGGER.atInfo().log("NULL MODIFIER");
                    };
                    for(var modifier : modifiers.entrySet()) {
                        TestPlugin.LOGGER.atInfo().log("ENTROU NO LOOP 2");
                        TestPlugin.LOGGER.atInfo().log(modifier.toString());;
                        int index = modifier.getKey();
                        StaticModifier stathicc = modifier.getValue();

                        statMap.putModifier(index, currentPerk.getId(), stathicc);
                        //Modifier existing = stats.getModifier(healthIndex, "my_plugin_bonus");
                        //stats.removeModifier(healthIndex, "my_plugin_bonus");
                    }
                }
                else {
                    TestPlugin.LOGGER.atInfo().log("ISDISABLED");
                    Map<Integer, StaticModifier> modifiers = currentPerk.setupModifiers();
                    if(modifiers == null) {
                        TestPlugin.LOGGER.atInfo().log("NULL MODIFIER");
                        break;
                    }
                    for(var modifier : modifiers.entrySet()) {
                        TestPlugin.LOGGER.atInfo().log("ENTROU NO LOOP 2");
                        int index = modifier.getKey();
                        StaticModifier stathicc = modifier.getValue();

                        statMap.removeModifier(index, currentPerk.getId());
                        //Modifier existing = stats.getModifier(healthIndex, "my_plugin_bonus");
                        //stats.removeModifier(healthIndex, "my_plugin_bonus");
                    }
                }
            }
        }
        assert player != null : "Player not found";
        assert component != null : "Component not found";
        assert transform != null : "Transform not found";

        player.sendMessage(Message.raw("Player UUID: " + component.getUuid()));
        player.sendMessage(Message.raw("Transform: " + transform.getPosition()));
    }
}
