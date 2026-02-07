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
import com.hypixel.hytale.server.core.modules.accesscontrol.provider.HytaleWhitelistProvider;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.ludas.plugin.clazz.Perk;
import com.ludas.plugin.components.LevelComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;
import java.util.List;
/*
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
        public String action;
        public String perkId;

        public static final BuilderCodec<TestPage2.CloseEventData> CODEC =
                BuilderCodec.builder(TestPage2.CloseEventData.class, TestPage2.CloseEventData::new)
                        .append(
                                new KeyedCodec<>("Action", Codec.STRING),
                                (CloseEventData o, String v) -> o.action = v,
                                (CloseEventData o) -> o.action
                        )
                        .add()
                        .append(
                                new KeyedCodec<>("PerkId", Codec.STRING),
                                (CloseEventData o, String v) -> o.action = v,
                                (CloseEventData o) -> o.action
                        )
                        .add()
                        .build();
    }

    @Override
    public void build(@NonNullDecl Ref<EntityStore> ref,
                      @NonNullDecl UICommandBuilder cmd,
                      @NonNullDecl UIEventBuilder evt,
                      @NonNullDecl Store<EntityStore> store) {
        cmd.append("Pages/TestPage2.ui");
        LevelComponent level = store.getComponent(ref, LevelComponent.getComponentType());
        if(level == null) return;
        cmd.set("#Stat1Value.Text", String.valueOf(playersOnline));
        cmd.set("#Stat2Value.Text", String.valueOf(questCount));
        cmd.set("#Stat3Value.Text", uptime);
        buildPlayerList(cmd, evt, level);
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
        LevelComponent level = store.getComponent(ref, LevelComponent.getComponentType());
        assert level != null;
        if(data.action == "Toggle") {
            boolean enabled = level.enableOrDisablePerk(data.perkId);
            playerRef.sendMessage(Message.raw("Perk " + enabled));
        }
        player.getPageManager().setPage(ref, store, Page.None);
    }

    private void buildPlayerList(UICommandBuilder commandBuilder, UIEventBuilder eventBuilder, LevelComponent level) {
        commandBuilder.clear("#PlayerList");
        List<Perk> perks = level.getPerksAsList();

        if (perks.isEmpty()) {
            commandBuilder.appendInline("#PlayerList", "Label { Text: \"No perks\"; Anchor: (Height: 40); Style: (FontSize: 14, TextColor: #6e7da1, HorizontalAlignment: Center, VerticalAlignment: Center); }");
            return;
        }

        int i = 0;
        for (Perk perk : perks) {
            String selector = "#PlayerList[" + i + "]";
            commandBuilder.append("#PlayerList", "Pages/TestList.ui");

            commandBuilder.set(selector + " #PerkName.Text", perk.getId());
            commandBuilder.set(selector + " #PerkDesc.Text", "server.desc.perk." +perk.getId());
            eventBuilder.addEventBinding(
                    CustomUIEventBindingType.Activating,
                    selector + " #PerkEnabled",
                    new EventData().append("Action", "Remove").append("PerkId", perk.getId()),
                    false
            );
            i++;
        }
    }
}
*/