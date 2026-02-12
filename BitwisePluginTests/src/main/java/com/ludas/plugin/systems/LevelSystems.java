package com.ludas.plugin.systems;


import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.HolderSystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.ludas.plugin.components.entity.LevelComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.Random;

public class LevelSystems {

    public static class NPCSpawnSystem extends HolderSystem<EntityStore> {

        public NPCSpawnSystem() {
        }

        @NonNullDecl
        @Override
        public Query<EntityStore> getQuery() {
            return Query.and(new Query[]{NPCEntity.getComponentType(), Query.not(LevelComponent.getComponentType())});
        }

        @Override
        public void onEntityAdd(@NonNullDecl Holder<EntityStore> holder,
                                @NonNullDecl AddReason addReason, @NonNullDecl Store<EntityStore> store) {
            NPCEntity npc = holder.getComponent(NPCEntity.getComponentType());
            if (npc == null) return;
            LevelComponent level = holder.getComponent(LevelComponent.getComponentType());
            if(level == null) {
                int rand = new Random().nextInt(1, 100);
                holder.putComponent(LevelComponent.getComponentType(), new LevelComponent(rand));
                level = holder.getComponent(LevelComponent.getComponentType());
            }

        }

        @Override
        public void onEntityRemoved(@NonNullDecl Holder<EntityStore> holder, @NonNullDecl RemoveReason removeReason,
                                    @NonNullDecl Store<EntityStore> store) {

        }
    }
}

