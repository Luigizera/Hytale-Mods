package com.ludas.plugin.clazz;

import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;

public class PerkProtocol {
    public static final int NULLABLE_BIT_FIELD_SIZE = 0;
    public static final int FIXED_BLOCK_SIZE = 2;
    public static final int VARIABLE_FIELD_COUNT = 3;
    public static final int VARIABLE_BLOCK_START = 91;
    public static final int MAX_SIZE = 1677721600;
    @Nonnull
    public String id;
    @Nonnull
    public boolean enabled;
    public boolean unlocked;

    public PerkProtocol() {
        this.id = Perk.DEFAULT_ID;
        this.enabled = Perk.DEFAULT_ENABLED;
        this.unlocked = Perk.DEFAULT_UNLOCKED;
    }

    public PerkProtocol(@Nonnull String id, @Nonnull boolean enabled, @Nonnull boolean unlocked) {
        this.id = Perk.DEFAULT_ID;
        this.enabled = Perk.DEFAULT_ENABLED;
        this.unlocked = Perk.DEFAULT_UNLOCKED;
        this.id = id;
        this.enabled = enabled;
        this.unlocked = unlocked;
    }

    public PerkProtocol(@Nonnull PerkProtocol other) {
        this.id = Perk.DEFAULT_ID;
        this.enabled = Perk.DEFAULT_ENABLED;
        this.unlocked = Perk.DEFAULT_UNLOCKED;
        this.id = other.id;
        this.enabled = other.enabled;
        this.unlocked = other.unlocked;
    }

    @Nonnull
    public static PerkProtocol deserialize(@Nonnull ByteBuf buf, int offset) {
        PerkProtocol obj = new PerkProtocol();
        obj.enabled = buf.getBoolean(offset + 0);
        obj.unlocked = buf.getBoolean(offset + 1);
        int varPos0 = offset + 91 + buf.getIntLE(offset + 2);
        int assetIdLen = VarInt.peek(buf, varPos0);
        if (assetIdLen < 0) {
            throw ProtocolException.negativeLength("AssetId", assetIdLen);
        }

        if (assetIdLen > 4096000) {
            throw ProtocolException.stringTooLong("AssetId", assetIdLen, 4096000);
        }

        obj.id = PacketIO.readVarString(buf, varPos0, PacketIO.UTF8);

        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        int maxEnd = 91;
        int fieldOffset0 = buf.getIntLE(offset + 43);
        int pos0 = offset + 91 + fieldOffset0;
        int sl = VarInt.peek(buf, pos0);
        pos0 += VarInt.length(buf, pos0) + sl;
        if (pos0 - offset > maxEnd) {
            maxEnd = pos0 - offset;
        }
        return maxEnd;
    }

    public void serialize(@Nonnull ByteBuf buf) {
        buf.writeBoolean(this.enabled);
        buf.writeBoolean(this.unlocked);
        PacketIO.writeVarString(buf, this.id, 4096000);
    }

    public int computeSize() {
        return (byte) (2 + PacketIO.stringSize(this.id));
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        return buffer.readableBytes() - offset < 6 ? ValidationResult.error("Buffer too small: expected at least 6 bytes") : ValidationResult.OK;
    }

    public PerkProtocol clone() {
        PerkProtocol copy = new PerkProtocol();
        copy.id = this.id;
        copy.enabled = this.enabled;
        copy.unlocked = this.unlocked;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof com.hypixel.hytale.protocol.Modifier)) {
            return false;
        } else {
            PerkProtocol other = (PerkProtocol)obj;
            return this.id == other.id && this.enabled == other.enabled && this.unlocked == other.unlocked;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.id, this.enabled, this.unlocked});
    }
}