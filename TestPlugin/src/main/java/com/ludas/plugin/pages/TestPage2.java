package com.ludas.plugin.pages;

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
        Level level = store.getComponent(ref, Level.getComponentType());
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
        Level level = store.getComponent(ref, Level.getComponentType());
        assert level != null;
        if(data.action == "Toggle") {
            boolean enabled = level.enableOrDisablePerk(data.perkId);
            playerRef.sendMessage(Message.raw("Perk " + enabled));
        }
        player.getPageManager().setPage(ref, store, Page.None);
    }

    private void buildPlayerList(UICommandBuilder commandBuilder, UIEventBuilder eventBuilder, Level level) {
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