package com.ludas.plugin.packet;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.io.PacketHandler;
import com.hypixel.hytale.server.core.io.adapter.PacketAdapters;
import com.hypixel.hytale.server.core.io.handlers.game.GamePacketHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class PlayerPacketTracker {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private static class PlayerStats {
        final Map<String, AtomicInteger> sent = new ConcurrentHashMap<>();
        final Map<String, AtomicInteger> received = new ConcurrentHashMap<>();
    }
    private static final Map<String, PlayerStats> stats = new ConcurrentHashMap<>();

    private static String getPlayerName(PacketHandler handler) {
        if (handler instanceof GamePacketHandler gpHandler) {
            return gpHandler.getPlayerRef().getUsername();
        }
        return null;
    }

    public static void registerPacketCounters() {
        PacketAdapters.registerInbound((PacketHandler handler, Packet packet) -> {
            String playerName = getPlayerName(handler);
            if (playerName != null) {
                stats.computeIfAbsent(playerName, k -> new PlayerStats())
                        .received.computeIfAbsent(packet.getClass().getSimpleName(), k -> new AtomicInteger(0))
                        .incrementAndGet();
            }
        });

        // Listener for sent packets (Outbound)
        PacketAdapters.registerOutbound((PacketHandler handler, Packet packet) -> {
            String playerName = getPlayerName(handler);
            if (playerName != null) {
                stats.computeIfAbsent(playerName, k -> new PlayerStats())
                        .sent.computeIfAbsent(packet.getClass().getSimpleName(), k -> new AtomicInteger(0))
                        .incrementAndGet();
            }
        });

        // Schedule logging every 3 seconds
        HytaleServer.SCHEDULED_EXECUTOR.scheduleAtFixedRate(() -> {
            if (stats.isEmpty()) return;

            for (Map.Entry<String, PlayerStats> entry : stats.entrySet()) {
                String player = entry.getKey();
                PlayerStats pStats = entry.getValue();

                StringBuilder sb = new StringBuilder();

                // Build Sent string
                List<String> sentLogs = new ArrayList<>();
                pStats.sent.forEach((type, atomic) -> {
                    int count = atomic.getAndSet(0);
                    if (count > 0) {
                        sentLogs.add(type + " x" + count);
                    }
                });

                if (!sentLogs.isEmpty()) {
                    sb.append("Sent ").append(String.join(", ", sentLogs));
                }

                // Build Received string
                List<String> recvLogs = new ArrayList<>();
                pStats.received.forEach((type, atomic) -> {
                    int count = atomic.getAndSet(0);
                    if (count > 0) {
                        recvLogs.add(type + " x" + count);
                    }
                });

                if (!recvLogs.isEmpty()) {
                    if (!sb.isEmpty()) sb.append("\n");
                    sb.append("Received ").append(String.join(", ", recvLogs));
                }

                if (!sb.isEmpty()) {
                    LOGGER.atInfo().log("To " + player + ":\n" + sb);
                }
            }
        }, 3, 3, TimeUnit.SECONDS);
    }
}