package com.ludas.plugin.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.DefaultArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatsModule;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.message.MessageFormat;
import com.ludas.plugin.clazz.Perk;
import com.ludas.plugin.clazz.PerkId;
import com.ludas.plugin.components.LevelComponent;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PerkCommand extends AbstractPlayerCommand {
    private final DefaultArg<String> enablePerk;

    public PerkCommand() {
        super("perk", "server.commands.ludas.perk.desc", false);
        this.enablePerk = this.withDefaultArg("enable", "server.commands.ludas.perk.enable.arg.desc", ArgTypes.STRING, null, "null");
    }
    @Override
    protected void execute(@NonNullDecl CommandContext context, @NonNullDecl Store<EntityStore> store,
                           @NonNullDecl Ref<EntityStore> ref, @NonNullDecl PlayerRef playerRef,
                           @NonNullDecl World world) {
        Player player = store.getComponent(ref, Player.getComponentType());
        if(player == null) return;
        LevelComponent level = store.getComponent(ref, LevelComponent.getComponentType());
        if(level == null) return;
        EntityStatMap statMap = store.getComponent(ref, EntityStatsModule.get().getEntityStatMapComponentType());
        if(statMap == null) return;

        if(enablePerk.get(context) != null) {
            int perkId = level.getPerkIdByName(enablePerk.get(context));
            if(perkId >= 0) {
                String perkName = level.getPerkNameById(perkId);
                if(!level.isPerkUnlocked(perkId)) {
                    player.sendMessage(Message.translation("server.commands.ludas.perk.unlock.condition." + perkName).color(Color.PINK).bold(true));
                }
                else {
                    level.enableOrDisablePerk(perkId);
                }
            }
        }

        ObjectArrayList<Message> values = new ObjectArrayList<>(PerkId.CURRENT_PERK_COUNT);
        for(int i = 0; i < PerkId.CURRENT_PERK_COUNT; ++i) {
            boolean unlocked = level.isPerkUnlocked(i);
            if(unlocked) {
                Perk perk = level.getPerkById(i);
                String perkName = level.getPerkNameById(i);
                String strModifier = "";
                boolean enabled = level.isPerkEnabled(i);
                Map<Integer, StaticModifier> modifiers = perk.setupModifiers();
                if(modifiers != null && !modifiers.isEmpty()) {
                    for(var modifier : modifiers.entrySet()) {
                        Integer index = modifier.getKey();
                        if(index >= statMap.size() || index < 0) {
                            throw new UnsupportedOperationException("Wrong implementation of Perk Index: " + index);
                        }
                        StaticModifier staticModifier = modifier.getValue();
                        if(staticModifier == null) {
                            throw new UnsupportedOperationException("Wrong implementation of Perk StaticModifier: " + staticModifier);
                        }
                        if(enabled) {
                            statMap.putModifier(index, perkName, staticModifier);
                        }
                        else {
                            statMap.removeModifier(index, perkName);

                        }
                        strModifier += " || "+ Objects.requireNonNull(statMap.get(index)).getId() + ": "
                                + (staticModifier.getCalculationType() == StaticModifier.CalculationType.ADDITIVE ? "+": "x")
                                + staticModifier.getAmount();
                    }
                }

                values.add(Message.join(
                        Message.translation("server.commands.ludas.perk.info")
                                .param("id", perkName).param("enabled", enabled).param("unlocked", unlocked),
                        Message.translation("server.commands.ludas.perk.info." + perkName)
                                .param("modifier", strModifier)).bold(true)
                );
            }
        }
        player.sendMessage(MessageFormat.list((Message)null, values));
    }
}
