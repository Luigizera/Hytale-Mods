package com.ludas.plugin.commands;

import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.SoundCategory;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.DefaultArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.FlagArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractTargetEntityCommand;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatsModule;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.message.MessageFormat;
import com.ludas.plugin.components.entity.LevelComponent;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import javax.annotation.Nonnull;
import java.util.Arrays;

public class EntityStatsCommand extends AbstractTargetEntityCommand {
    private final DefaultArg<Float> healArg;
    private final OptionalArg<Float> addMaxHealth;
    private final OptionalArg<String> messageArg;
    private final FlagArg killArg;
    private final FlagArg debugArg;
    private final FlagArg levelArg;

    public EntityStatsCommand() {
        super("entity", "server.commands.ludas.entity.desc",
                false);
        this.healArg = this.withDefaultArg("heal", "server.commands.ludas.entity.heal.arg.desc", ArgTypes.FLOAT, 0F, "0")
                .addValidator(Validators.greaterThanOrEqual(0F));
        this.addMaxHealth = this.withOptionalArg("addMaxHealth", "server.commands.ludas.entity.heal.arg.desc", ArgTypes.FLOAT)
                .addValidator(Validators.greaterThanOrEqual(0F));
        this.messageArg = this.withOptionalArg("message", "server.commands.ludas.entity.message.arg.desc", ArgTypes.STRING);
        this.debugArg = this.withFlagArg("debug", "server.commands.ludas.entity.debug.arg.desc");
        this.killArg = this.withFlagArg("kill", "server.commands.ludas.entity.kill.arg.desc");
        this.levelArg = this.withFlagArg("level", "server.commands.ludas.entity.kill.arg.desc");
    }

    protected void execute(@Nonnull CommandContext context, @Nonnull ObjectList<Ref<EntityStore>> entities,
                           @Nonnull World world, @Nonnull Store<EntityStore> store) {
        for(Ref<EntityStore> entity : entities) {
            ComponentType<EntityStore, EntityStatMap> component = EntityStatsModule.get().getEntityStatMapComponentType();
            EntityStatMap statMap = store.getComponent(entity, component);
            if (statMap == null) return;

            ObjectArrayList<Message> values = new ObjectArrayList<>(statMap.size());
            int healthIdx = DefaultEntityStatTypes.getHealth();
            EntityStatValue entityHealth = statMap.get(healthIdx);

            if(messageArg.get(context) != null) {
                context.sendMessage(Message.raw("Debug message: " + messageArg.get(context)));
                playSound("SFX_Test_Blip_C", world, context, store);
            }



            if(levelArg.get(context)) {
                LevelComponent levelComponent = store.getComponent(entity, LevelComponent.getComponentType());
                if(levelComponent != null) {
                    values.add(Message.raw("Level: " + levelComponent.getLevel()));
                }
            }

            if (entityHealth != null) {
                if(killArg.get(context)) {
                    Damage.CommandSource damageSource = new Damage.CommandSource(context.sender(), "kill");
                    DeathComponent.tryAddComponent(store, entity, new Damage(damageSource, DamageCause.COMMAND, (float)Integer.MAX_VALUE));
                    break;
                }

                if(addMaxHealth.get(context) != null) {
                    Modifier m = statMap.putModifier(healthIdx, entityHealth.getId(),
                            new StaticModifier(
                                    Modifier.ModifierTarget.MAX,
                                    StaticModifier.CalculationType.ADDITIVE,
                                    addMaxHealth.get(context)));
                    context.sendMessage(Message.raw(""+m));
                }

                if(healArg.get(context) > 0F) {
                    statMap.addStatValue(healthIdx, healArg.get(context));
                    if (this.debugArg.get(context)) {
                        float missingHealth = entityHealth.getMax() - entityHealth.get();
                        context.sendMessage(Message.raw("Entity Current Health: " + entityHealth.get()));
                        context.sendMessage(Message.raw("Entity Max Health: " + entityHealth.getMax()));
                        context.sendMessage(Message.raw("Missing Health: " + missingHealth));
                        context.sendMessage(Message.raw("Input Heal Value: " + healArg.get(context)));
                        context.sendMessage(Message.raw("Default Heal Value: " + healArg.getDefaultValue()));
                        context.sendMessage(Message.raw("DefaultEntityTypes#getHealth: " + healthIdx));
                    }
                }
            }

            for(int i = 0; i < statMap.size(); ++i) {
                EntityStatValue entityStatValue = statMap.get(i);
                assert entityStatValue != null;
                if(i == healthIdx && this.debugArg.get(context)) { //entityStatValue.getId().equals("Health")
                    context.sendMessage(Message.raw("** DEBUG ENTITY STATS INFO **"));
                    context.sendMessage(Message.raw("entityStatValue#getId: " + entityStatValue.getId()));
                    context.sendMessage(Message.raw("entityStatValue#getIndex: " + entityStatValue.getIndex()));
                    context.sendMessage(Message.raw("entityStatValue#get: " + entityStatValue.get()));
                    context.sendMessage(Message.raw("entityStatValue#getMin: " + entityStatValue.getMin()));
                    context.sendMessage(Message.raw("entityStatValue#getMax: " + entityStatValue.getMax()));
                    context.sendMessage(Message.raw("entityStatValue#getIgnoreInvulnerability: " + entityStatValue.getIgnoreInvulnerability()));
                    context.sendMessage(Message.raw("entityStatValue#getRegeneratingValues: " + Arrays.toString(entityStatValue.getRegeneratingValues())));
                    context.sendMessage(Message.raw("entityStatValue#getModifiers: " + entityStatValue.getModifiers()));
                    context.sendMessage(Message.raw("ForLoop#i: " + i));
                    context.sendMessage(Message.raw("*****************************"));
                }
                values.add(Message.translation("server.commands.ludas.entity.result").param("id", entityStatValue.getId()).param("value", entityStatValue.get()));
            }
            context.sendMessage(MessageFormat.list((Message)null, values));
        }
    }

    private void playSound(@Nonnull String sfx, @Nonnull World world, @Nonnull CommandContext context, @Nonnull Store<EntityStore> store) {
        int soundIdx = SoundEvent.getAssetMap().getIndex(sfx);
        world.execute(() -> {
            Ref<EntityStore> player = context.senderAsPlayerRef();
            context.sendMessage(Message.raw("Debug player: " + player));
            TransformComponent transform = store.getComponent(player, EntityModule.get().getTransformComponentType());
            SoundUtil.playSoundEvent3dToPlayer(player, soundIdx, SoundCategory.UI, transform.getPosition(), player.getStore());
        });
    }
}
