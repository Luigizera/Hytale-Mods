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
import com.ludas.plugin.TestPlugin;
import com.ludas.plugin.clazz.MagnumOpus;
import com.ludas.plugin.clazz.MagnumOpusStatTypes;
import com.ludas.plugin.clazz.Perk;
import com.ludas.plugin.clazz.Status;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.awt.*;
import java.util.Map;

public class PerkCommand extends AbstractPlayerCommand {
    private final DefaultArg<String> enablePerk;
    private final DefaultArg<String> statId;

    public PerkCommand() {
        super("perk", "server.commands.ludas.perk.desc", false);
        this.enablePerk = this.withDefaultArg("enable", "server.commands.ludas.perk.enable.arg.desc", ArgTypes.STRING, null, "null");
        this.statId = this.withDefaultArg("stat", "server.commands.ludas.perk.enable.arg.desc", ArgTypes.STRING, MagnumOpusStatTypes.UNKNOWN.id, "Unknown");
    }
    @Override
    protected void execute(@NonNullDecl CommandContext context, @NonNullDecl Store<EntityStore> store,
                           @NonNullDecl Ref<EntityStore> ref, @NonNullDecl PlayerRef playerRef,
                           @NonNullDecl World world) {
        Player player = store.getComponent(ref, Player.getComponentType());
        if(player == null) return;
        MagnumOpus magnumOpus = store.getComponent(ref, MagnumOpus.getComponentType());
        TestPlugin.LOGGER.atInfo().log("magnum opus: " + magnumOpus);
        if(magnumOpus == null) return;
        EntityStatMap statMap = store.getComponent(ref, EntityStatsModule.get().getEntityStatMapComponentType());
        if(statMap == null) return;

        String statTypes = MagnumOpusStatTypes.UNKNOWN.id;

        if(statId.get(context) != null) {
            try {
                MagnumOpusStatTypes.valueOf(statId.get(context).toUpperCase());
            }
            catch (Exception e) {
                player.sendMessage(Message.raw("Provide a valid stat name."));
                return;
            }
            statTypes = statId.get(context).toUpperCase();
        }

        if (enablePerk.get(context) != null) {
            Perk perk = magnumOpus.getStat(statTypes).getPerk(enablePerk.get(context));
            if (perk != null) {
                if (!perk.isUnlocked()) {
                    player.sendMessage(Message.translation("server.commands.ludas.perk.unlock.condition." + perk.getId()).color(Color.PINK).bold(true));
                }
                else {
                    magnumOpus.getStat(statTypes).enableOrDisablePerk(enablePerk.get(context));
                }
            }
            else {
                player.sendMessage(Message.raw("Perk inexistente"));
            }
        }
        TestPlugin.LOGGER.atInfo().log("stattypes: " + statTypes);
        Status status = magnumOpus.getStat(statTypes);
        TestPlugin.LOGGER.atInfo().log("get status: " + status);
        TestPlugin.LOGGER.atInfo().log("perks: " + status.getPerksAsList());

        ObjectArrayList<Message> values = new ObjectArrayList<>(statMap.size());
        java.util.List<Perk> perks = status.getPerksAsList();;
        if(perks == null || perks.isEmpty()) return;
        for(Perk perk : perks) {
            String strModifier = "";
            Map<Integer, StaticModifier> modifiers = perk.setupModifiers();
            if(modifiers == null || modifiers.isEmpty()) continue;
            for(var modifier : modifiers.entrySet()) {
                Integer index = modifier.getKey();
                if(index >= statMap.size() || index < 0 || index == null) {
                    throw new UnsupportedOperationException("Wrong implementation of Perk Index: " + index);
                }
                StaticModifier staticModifier = modifier.getValue();
                if(staticModifier == null) {
                    throw new UnsupportedOperationException("Wrong implementation of Perk StaticModifier: " + staticModifier);
                }
                if(perk.isUnlocked() && perk.isEnabled()) {
                    statMap.putModifier(index, perk.getId(), staticModifier);
                }
                else {
                    statMap.removeModifier(index, perk.getId());
                }
                strModifier += " || "+ statMap.get(index).getId() + ": "
                        + (staticModifier.getCalculationType() == StaticModifier.CalculationType.ADDITIVE ? "+": "x")
                        + staticModifier.getAmount();
            }
            values.add(Message.translation("server.commands.ludas.perk.info").param("id", perk.getId()).param("enabled", perk.isEnabled()).param("unlocked", perk.isUnlocked()));
            values.add(Message.raw(" "));
            values.add(Message.translation("server.commands.ludas.perk.info." + perk.getId()).param("modifier", strModifier));
        }
        player.sendMessage(MessageFormat.list((Message)null, values));
    }
}
