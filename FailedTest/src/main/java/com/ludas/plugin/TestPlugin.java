package com.ludas.plugin;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.ludas.plugin.clazz.*;
import com.ludas.plugin.commands.*;
import com.ludas.plugin.commands.collection.LudasCommandCollection;
import com.ludas.plugin.components.PoisonComponent;
import com.ludas.plugin.events.GiveXPEvent;
import com.ludas.plugin.handlers.GiveXPHandler;
import com.ludas.plugin.perks.PoisonPerk;
import com.ludas.plugin.systems.MagnumOpusSystems;
import com.ludas.plugin.systems.PoisonSystem;
import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.event.EventRegistry;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandRegistry;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.ArrayList;
import java.util.List;

public class TestPlugin extends JavaPlugin {
    private static TestPlugin instance;
    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static List<AbstractCommand> commands;

    public TestPlugin(@NonNullDecl JavaPluginInit init) {
        super(init);
        instance = this;
        commands = new ArrayList<>();
    }

    @Override
    protected void setup() {
        super.setup();
        LOGGER.atInfo().log("TestPlugin loading...");

        registerEntities();
        registerEvents();
        addCommandsToList();
        registerCommands(commands);
        LOGGER.atInfo().log("TestPlugin loaded.");
    }

    public static TestPlugin get() {
        return instance;
    }

    private void addCommandsToList() {
        commands.add(new TestCommand());
        commands.add(new TestUICommand());
        commands.add(new TestUI2Command());
        commands.add(new ServerRulesCommand());
        commands.add(new PlayerInfoCommand());
        commands.add(new PoisonCommand());
        commands.add(new EntityStatsCommand());
        commands.add(new LevelCommand());
        commands.add(new PerkCommand());
    }

    private void registerEntities() {
        LOGGER.atInfo().log("Registering Entities...");
        ComponentRegistryProxy<EntityStore> entityRegistry = this.getEntityStoreRegistry();


        getCodecRegistry(Perk.MAP_CODEC).register(PoisonPerk.ID, PoisonPerk.class, PoisonPerk.CODEC);

        var magnumOpus = entityRegistry.registerComponent(MagnumOpus.class, "MagnumOpus", MagnumOpus.CODEC);
        MagnumOpus.setComponentType(magnumOpus);

        entityRegistry.registerSystem(new MagnumOpusSystems.PlayerSpawnSystem());
        entityRegistry.registerSystem(new MagnumOpusSystems.NPCSpawnSystem());
        entityRegistry.registerSystem(new MagnumOpusSystems.GetExpFromNpcSystem());
        entityRegistry.registerSystem(new MagnumOpusSystems.PerkTick());

        var poisonComponent = entityRegistry.registerComponent(PoisonComponent.class, PoisonComponent::new);
        PoisonComponent.setComponentType(poisonComponent);
        entityRegistry.registerSystem(new PoisonSystem(PoisonComponent.getComponentType()));
    }

    private void registerEvents() {
        LOGGER.atInfo().log("Registering Events...");
        EventRegistry eventRegistry = getEventRegistry();

        eventRegistry.register(GiveXPEvent.class, new GiveXPHandler());
    }

    private void registerCommands(List<AbstractCommand> commands) {
        LOGGER.atInfo().log("Registering Commands...");
        CommandRegistry registry = this.getCommandRegistry();
        registry.registerCommand(new LudasCommandCollection(commands)); //collection
    }
}
