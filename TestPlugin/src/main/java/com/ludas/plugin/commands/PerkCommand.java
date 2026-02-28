package com.ludas.plugin.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.DefaultArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
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
import com.ludas.plugin.clazz.Config;
import com.ludas.plugin.clazz.Perk;
import com.ludas.plugin.clazz.PerkId;
import com.ludas.plugin.clazz.PerkType;
import com.ludas.plugin.components.entity.*;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.awt.*;
import java.util.Map;
import java.util.Objects;

public class PerkCommand extends AbstractPlayerCommand {
    private final DefaultArg<String> enablePerk;
    private final RequiredArg<String> status;

    public PerkCommand() {
        super("perk", "server.commands.ludas.perk.desc", false);
        this.status = this.withRequiredArg("status", "server.commands.ludas.perk.status.arg.desc", ArgTypes.STRING);
        this.enablePerk = this.withDefaultArg("enable", "server.commands.ludas.perk.enable.arg.desc", ArgTypes.STRING, null, "null");
    }

    private static void setupModifiersLoop(Perk perk, EntityStatMap statMap,
                                           boolean enabled, boolean unlocked,
                                           String perkName, String strModifier,
                                           ObjectArrayList<Message> values, String translationKey) {
        Map<Integer, StaticModifier> modifiers = perk.setupModifiers();
        if (modifiers != null && !modifiers.isEmpty()) {
            for (var modifier : modifiers.entrySet()) {
                Integer index = modifier.getKey();
                if (index > statMap.size() || index < 0) {
                    throw new UnsupportedOperationException("Wrong implementation of Perk Index: " + index);
                }
                StaticModifier staticModifier = modifier.getValue();
                if (staticModifier == null) {
                    throw new UnsupportedOperationException("Wrong implementation of Perk StaticModifier: " + staticModifier);
                }
                if (enabled) {
                    statMap.putModifier(index, perkName, staticModifier);
                }
                else {
                    statMap.removeModifier(index, perkName);
                }
                strModifier += " || " + Objects.requireNonNull(statMap.get(index)).getId() + ": "
                        + (staticModifier.getCalculationType() == StaticModifier.CalculationType.ADDITIVE ? "+" : "x")
                        + staticModifier.getAmount();
            }
        }

        values.add(Message.join(
                Message.translation("server.commands.ludas.perk.info")
                        .param("id", perkName).param("enabled", enabled).param("unlocked", unlocked),
                Message.translation(translationKey + perkName)
                        .param("modifier", strModifier)).bold(true)
        );
    }

    @Override
    protected void execute(@NonNullDecl CommandContext context, @NonNullDecl Store<EntityStore> store,
                           @NonNullDecl Ref<EntityStore> ref, @NonNullDecl PlayerRef playerRef,
                           @NonNullDecl World world) {
        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) return;
        MainStatusComponent mainStatus = store.getComponent(ref, MainStatusComponent.getComponentType());
        if (mainStatus == null) return;
        EntityStatMap statMap = store.getComponent(ref, EntityStatsModule.get().getEntityStatMapComponentType());
        if (statMap == null) return;

        switch (status.get(context).toUpperCase()) {
            case PerkType.MAIN: {
                if (enablePerk.get(context) != null) {
                    int perkId = mainStatus.getPerkIdByName(enablePerk.get(context));
                    if (perkId >= 0) {
                        String perkName = mainStatus.getPerkNameById(perkId);
                        if (!mainStatus.isPerkUnlocked(perkId)) {
                            player.sendMessage(Message.translation("server.commands.ludas.perk.unlock.condition.main." + perkName).color(Color.PINK).bold(true));
                        } else {
                            mainStatus.enableOrDisablePerk(perkId);
                        }
                    }
                }

                ObjectArrayList<Message> values = new ObjectArrayList<>();
                for (int i = 0; i < PerkId.MAIN_CURRENT_PERK_COUNT; ++i) {
                    boolean unlocked = mainStatus.isPerkUnlocked(i);
                    if (unlocked) {
                        Perk perk = mainStatus.getPerkById(i);
                        String perkName = mainStatus.getPerkNameById(i);
                        String strModifier = "";
                        boolean enabled = mainStatus.isPerkEnabled(i);
                        setupModifiersLoop(perk, statMap, enabled, unlocked, perkName, strModifier, values, "server.commands.ludas.perk.info.main.");
                    }
                }
                player.sendMessage(MessageFormat.list((Message) null, values));
                break;
            }
            case PerkType.STRENGTH: {
                StrengthComponent strength = mainStatus.getStrength();
                if(strength == null) return;

                if (enablePerk.get(context) != null) {
                    int perkId = strength.getPerkIdByName(enablePerk.get(context));
                    if (perkId >= 0) {
                        String perkName = strength.getPerkNameById(perkId);
                        if (!strength.isPerkUnlocked(perkId)) {
                            player.sendMessage(Message.translation("server.commands.ludas.perk.unlock.condition.strength." + perkName).color(Color.PINK).bold(true));
                        } else {
                            strength.enableOrDisablePerk(perkId);
                        }
                    }
                }

                ObjectArrayList<Message> values = new ObjectArrayList<>();
                for (int i = 0; i < PerkId.STRENGTH_CURRENT_PERK_COUNT; ++i) {
                    boolean unlocked = strength.isPerkUnlocked(i);
                    if (unlocked) {
                        Perk perk = strength.getPerkById(i);
                        String perkName = strength.getPerkNameById(i);
                        String strModifier = "";
                        boolean enabled = strength.isPerkEnabled(i);
                        setupModifiersLoop(perk, statMap, enabled, unlocked, perkName, strModifier, values, "server.commands.ludas.perk.info.strength.");
                    }
                }
                player.sendMessage(MessageFormat.list((Message) null, values));
                break;
            }
            case PerkType.AGILITY: {
                AgilityComponent agility = mainStatus.getAgility();
                if(agility == null) return;
                if (enablePerk.get(context) != null) {
                    int perkId = agility.getPerkIdByName(enablePerk.get(context));
                    if (perkId >= 0) {
                        String perkName = agility.getPerkNameById(perkId);
                        if (!agility.isPerkUnlocked(perkId)) {
                            player.sendMessage(Message.translation("server.commands.ludas.perk.unlock.condition.agility." + perkName).color(Color.PINK).bold(true));
                        } else {
                            agility.enableOrDisablePerk(perkId);
                        }
                    }
                }

                ObjectArrayList<Message> values = new ObjectArrayList<>();
                for (int i = 0; i < PerkId.STRENGTH_CURRENT_PERK_COUNT; ++i) {
                    boolean unlocked = agility.isPerkUnlocked(i);
                    if (unlocked) {
                        Perk perk = agility.getPerkById(i);
                        String perkName = agility.getPerkNameById(i);
                        String strModifier = "";
                        boolean enabled = agility.isPerkEnabled(i);
                        setupModifiersLoop(perk, statMap, enabled, unlocked, perkName, strModifier, values, "server.commands.ludas.perk.info.agility.");
                    }
                }
                player.sendMessage(MessageFormat.list((Message) null, values));
                break;
            }
            case PerkType.MAGIC: {
                MagicComponent magic = mainStatus.getMagic();
                if(magic == null) return;
                if (enablePerk.get(context) != null) {
                    int perkId = magic.getPerkIdByName(enablePerk.get(context));
                    if (perkId >= 0) {
                        String perkName = magic.getPerkNameById(perkId);
                        if (!magic.isPerkUnlocked(perkId)) {
                            player.sendMessage(Message.translation("server.commands.ludas.perk.unlock.condition.magic." + perkName).color(Color.PINK).bold(true));
                        } else {
                            magic.enableOrDisablePerk(perkId);
                        }
                    }
                }

                ObjectArrayList<Message> values = new ObjectArrayList<>();
                for (int i = 0; i < PerkId.STRENGTH_CURRENT_PERK_COUNT; ++i) {
                    boolean unlocked = magic.isPerkUnlocked(i);
                    if (unlocked) {
                        Perk perk = magic.getPerkById(i);
                        String perkName = magic.getPerkNameById(i);
                        String strModifier = "";
                        boolean enabled = magic.isPerkEnabled(i);
                        setupModifiersLoop(perk, statMap, enabled, unlocked, perkName, strModifier, values, "server.commands.ludas.perk.info.magic.");
                    }
                }
                player.sendMessage(MessageFormat.list((Message) null, values));
                break;
            }
            case PerkType.VITALITY: {
                VitalityComponent vitality = mainStatus.getVitality();
                if(vitality == null) return;
                if (enablePerk.get(context) != null) {
                    int perkId = vitality.getPerkIdByName(enablePerk.get(context));
                    if (perkId >= 0) {
                        String perkName = vitality.getPerkNameById(perkId);
                        if (!vitality.isPerkUnlocked(perkId)) {
                            player.sendMessage(Message.translation("server.commands.ludas.perk.unlock.condition.vitality." + perkName).color(Color.PINK).bold(true));
                        } else {
                            vitality.enableOrDisablePerk(perkId);
                        }
                    }
                }

                ObjectArrayList<Message> values = new ObjectArrayList<>();
                for (int i = 0; i < PerkId.STRENGTH_CURRENT_PERK_COUNT; ++i) {
                    boolean unlocked = vitality.isPerkUnlocked(i);
                    if (unlocked) {
                        Perk perk = vitality.getPerkById(i);
                        String perkName = vitality.getPerkNameById(i);
                        String strModifier = "";
                        boolean enabled = vitality.isPerkEnabled(i);
                        setupModifiersLoop(perk, statMap, enabled, unlocked, perkName, strModifier, values, "server.commands.ludas.perk.info.vitality.");
                    }
                }
                player.sendMessage(MessageFormat.list((Message) null, values));
                break;
            }
            default: {
                player.sendMessage(Message.translation("server.commands.ludas.perk.status.arg.unknown").color(Color.PINK).bold(true));
                break;
            }
        }

    }
}


