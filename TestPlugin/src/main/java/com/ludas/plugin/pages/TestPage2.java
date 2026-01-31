package com.ludas.plugin.pages;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;

public class TestPage2 extends InteractiveCustomUIPage<TestPage2.CloseEventData> {
    private final int playersOnline;
    private final int questCount;
    private final String uptime;

    public TestPage2(PlayerRef playerRef, int playersOnline, int questCount, String uptime)
    {
        super(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction, CloseEventData.CODEC);
        this.playersOnline = playersOnline;
        this.questCount = questCount;
        this.uptime = uptime;
    }

    public static class CloseEventData {
        public static final BuilderCodec<TestPage2.CloseEventData> CODEC =
                BuilderCodec.builder(TestPage2.CloseEventData.class, TestPage2.CloseEventData::new)
                        .build();
    }

    @Override
    public void build(@NonNullDecl Ref<EntityStore> ref,
                      @NonNullDecl UICommandBuilder cmd,
                      @NonNullDecl UIEventBuilder evt,
                      @NonNullDecl Store<EntityStore> store) {
        cmd.append("Pages/TestPage2.ui");
        cmd.set("#Stat1Value.Text", String.valueOf(playersOnline));
        cmd.set("#Stat2Value.Text", String.valueOf(questCount));
        cmd.set("#Stat3Value.Text", uptime);
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#CloseButton");
    }

    @Override
    public void handleDataEvent(
            @Nonnull Ref<EntityStore> ref,
            @Nonnull Store<EntityStore> store,
            @Nonnull CloseEventData data
    ) {
        // Close the page
        Player player = store.getComponent(ref, Player.getComponentType());
        assert player != null;
        player.getPageManager().setPage(ref, store, Page.None);
    }
}
