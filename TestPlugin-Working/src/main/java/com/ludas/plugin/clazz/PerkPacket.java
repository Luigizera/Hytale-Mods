package com.ludas.plugin.clazz;

import com.hypixel.hytale.protocol.Packet;
import io.netty.buffer.ByteBuf;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class PerkPacket implements Packet {
    @Override
    public int getId() {
        return 0;
    }

    @Override
    public void serialize(@NonNullDecl ByteBuf byteBuf) {

    }

    @Override
    public int computeSize() {
        return 0;
    }
}
