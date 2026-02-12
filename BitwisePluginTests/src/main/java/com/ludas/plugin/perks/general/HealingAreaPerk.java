package com.ludas.plugin.perks.general;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.PersistentModel;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.ludas.plugin.clazz.Perk;
import com.ludas.plugin.clazz.PerkId;
import com.ludas.plugin.clazz.Vector2dClean;
import com.ludas.plugin.components.entity.StrengthComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HealingAreaPerk extends Perk{
    public static final String NAME = "healingArea";
    private static int count = 0;

    public HealingAreaPerk() {
    }

    @Override
    public Map<Integer, StaticModifier> setupModifiers() {
        return null;
    }

    @Override
    public void unlockCondition(int idx, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk) {
        StrengthComponent strength = archetypeChunk.getComponent(idx, StrengthComponent.getComponentType());
        if(strength == null) return;

        if(strength.getLevelComponent().getLevel() <= 2) {
            return;
        }
        Player player = archetypeChunk.getComponent(idx, Player.getComponentType());
        if(player == null) return;
        strength.setUnlocked(PerkId.HEALING_AREA);
        player.sendMessage(Message.translation("server.perks.ludas.unlocked").param("id", NAME));
    }

    public void tick(float dt, int idx, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
                     @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        Player player = archetypeChunk.getComponent(idx, Player.getComponentType());
        if (player == null) return;

        if (player.getGameMode() != GameMode.Adventure) return;
        Ref<EntityStore> playerRef = player.getReference();
        if (playerRef == null) return;
        TransformComponent transform = store.getComponent(playerRef, TransformComponent.getComponentType());
        if (transform == null) return;
        if(count < 1) {
            Vector3d playerPos = transform.getPosition();


            double x = playerPos.getX();
            double y = playerPos.getY();
            double z = playerPos.getZ();
            double r = 3;

            List<Vector2dClean> list = circle(x, z, r);
            for (Vector2dClean vector2d : list) {
                if(playerPos.distanceTo(vector2d.x, y, vector2d.y) < 0) {
                    Vector3d vector3d = new Vector3d(vector2d.x, y, vector2d.y); // position
                    Holder<EntityStore> holder = EntityStore.REGISTRY.newHolder();
                    ModelAsset modelAsset = ModelAsset.getAssetMap().getAsset("Minecart");
                    Model model = Model.createScaledModel(modelAsset, 1.0f);
                    holder.addComponent(PersistentModel.getComponentType(), new PersistentModel(model.toReference()));
                    holder.addComponent(ModelComponent.getComponentType(), new ModelComponent(model));
                    holder.addComponent(BoundingBox.getComponentType(), new BoundingBox(model.getBoundingBox()));
                    holder.addComponent(NetworkId.getComponentType(), new NetworkId(store.getExternalData().takeNextNetworkId()));
                    holder.ensureComponent(UUIDComponent.getComponentType());
                    holder.addComponent(TransformComponent.getComponentType(), new TransformComponent(vector3d, new Vector3f(0, 0, 0)));

                    commandBuffer.addEntity(holder, AddReason.SPAWN);
                }
            }
            count++;
        }
    }

    private List<Vector2dClean> circle(double x, double y, double r) {
        List<Vector2dClean> list = new ArrayList<>();
        double b = y;
        b += r;
        double a = x;
        //[x, y]
        //[a, b]
        list.add(new Vector2dClean(a, b));
        while(b > y) {
            b--;
            a++;
            list.add(new Vector2dClean(a, b));
        }
        while(a > x) {
            a--;
            b--;
            list.add(new Vector2dClean(a, b));
        }
        while(b < y) {
            b++;
            a--;
            list.add(new Vector2dClean(a, b));
        }
        while(a < x) {
            a++;
            b++;
            list.add(new Vector2dClean(a, b));
        }
        return list;
    }

    public void removeComponents(int idx, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
                                 @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        return;
    }
}
