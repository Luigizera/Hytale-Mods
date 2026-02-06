package com.ludas.plugin.pages;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class TestPage extends InteractiveCustomUIPage<TestPage.GreetEventData> {

    public static class GreetEventData {
        public String playerName;
        public static final BuilderCodec<GreetEventData> CODEC =
                BuilderCodec.builder(GreetEventData.class, GreetEventData::new)
                        .append(
                                new KeyedCodec<>("@PlayerName", Codec.STRING),
                                (obj, val) -> obj.playerName = val,
                                obj -> obj.playerName
                        )
                        .add()
                        .build();
    }

    public TestPage(PlayerRef playerRef){
        super(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction, GreetEventData.CODEC);
    }

    @Override
    public void build(@NonNullDecl Ref<EntityStore> ref,
                      @NonNullDecl UICommandBuilder cmd,
                      @NonNullDecl UIEventBuilder evt,
                      @NonNullDecl Store<EntityStore> store) {
        cmd.append("Pages/TestPage.ui");

        evt.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#GreetButton",
                new EventData().append("@PlayerName", "#NameInput.Value")
        );
    }

    @Override
    public void handleDataEvent(@NonNullDecl Ref<EntityStore> ref,
                                @NonNullDecl Store<EntityStore> store,
                                @NonNullDecl GreetEventData data) {
        Player player = store.getComponent(ref, Player.getComponentType());

        String name = data.playerName != null && !data.playerName.isEmpty()
                ? data.playerName
                : "Stranger";

        playerRef.sendMessage(Message.raw("Hello, " + name + "!"));

        assert player != null;
        player.getPageManager().setPage(ref, store, Page.None);
    }
}
