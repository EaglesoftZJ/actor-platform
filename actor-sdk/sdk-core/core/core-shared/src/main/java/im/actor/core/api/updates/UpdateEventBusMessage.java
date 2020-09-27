package im.actor.core.api.updates;
/*
 *  Generated by the Actor API Scheme generator.  DO NOT EDIT!
 */

import im.actor.runtime.bser.*;
import im.actor.runtime.collections.*;
import static im.actor.runtime.bser.Utils.*;
import im.actor.core.network.parser.*;
import androidx.annotation.Nullable;
import org.jetbrains.annotations.NotNull;
import com.google.j2objc.annotations.ObjectiveCName;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import im.actor.core.api.*;

public class UpdateEventBusMessage extends Update {

    public static final int HEADER = 0xa02;
    public static UpdateEventBusMessage fromBytes(byte[] data) throws IOException {
        return Bser.parse(new UpdateEventBusMessage(), data);
    }

    private String id;
    private Integer senderId;
    private Long senderDeviceId;
    private byte[] message;

    public UpdateEventBusMessage(@NotNull String id, @Nullable Integer senderId, @Nullable Long senderDeviceId, @NotNull byte[] message) {
        this.id = id;
        this.senderId = senderId;
        this.senderDeviceId = senderDeviceId;
        this.message = message;
    }

    public UpdateEventBusMessage() {

    }

    @NotNull
    public String getId() {
        return this.id;
    }

    @Nullable
    public Integer getSenderId() {
        return this.senderId;
    }

    @Nullable
    public Long getSenderDeviceId() {
        return this.senderDeviceId;
    }

    @NotNull
    public byte[] getMessage() {
        return this.message;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.id = values.getString(1);
        this.senderId = values.optInt(2);
        this.senderDeviceId = values.optLong(3);
        this.message = values.getBytes(4);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.id == null) {
            throw new IOException();
        }
        writer.writeString(1, this.id);
        if (this.senderId != null) {
            writer.writeInt(2, this.senderId);
        }
        if (this.senderDeviceId != null) {
            writer.writeLong(3, this.senderDeviceId);
        }
        if (this.message == null) {
            throw new IOException();
        }
        writer.writeBytes(4, this.message);
    }

    @Override
    public String toString() {
        String res = "update EventBusMessage{";
        res += "id=" + this.id;
        res += ", senderId=" + this.senderId;
        res += ", senderDeviceId=" + this.senderDeviceId;
        res += "}";
        return res;
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
