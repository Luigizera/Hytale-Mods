package com.ludas.plugin.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.DefaultArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatsModule;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.ludas.plugin.TestPlugin;
import com.ludas.plugin.clazz.Perk;
import com.ludas.plugin.components.LevelComponent;
import com.ludas.plugin.perks.PoisonPerk;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.List;
import java.util.Map;

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
        PoisonPerk poison = new PoisonPerk();
        level.putPerk(poison);

        if(enablePerk.get(context) != null) {
            Perk perk = level.getPerk(enablePerk.get(context));
            if(perk != null) {
                level.enableOrDisablePerk(enablePerk.get(context));
                player.sendMessage(Message.raw("Perk: " + perk.getId() + " || Enabled: " + perk.isEnabled()));
            } else {
                player.sendMessage(Message.raw("Perk não existe manito"));
            }
        }

        List<Perk> perks = level.getPerksAsList();
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
                if(perk.isEnabled()) {
                    statMap.putModifier(index, perk.getId(), staticModifier);
                }
                else {
                    statMap.removeModifier(index, perk.getId());
                }
                strModifier += " || "+ statMap.get(index).getId() + ": "
                        + (staticModifier.getCalculationType() == StaticModifier.CalculationType.ADDITIVE ? "+": "x")
                        + staticModifier.getAmount();
            }
            player.sendMessage(Message.translation("server.commands.ludas.perk.info." + perk.getId()).param("id", perk.getId()).param("enabled", perk.isEnabled()).param("modifier", strModifier));
        }
    }
}
