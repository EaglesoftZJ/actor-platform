package im.actor.core.api.updates;
/*
 *  Generated by the Actor API Scheme generator.  DO NOT EDIT!
 */

import im.actor.runtime.bser.*;
import im.actor.runtime.collections.*;
import static im.actor.runtime.bser.Utils.*;
import im.actor.core.network.parser.*;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;
import com.google.j2objc.annotations.ObjectiveCName;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import im.actor.core.api.*;

public class UpdateGroupTopicChangedObsolete extends Update {

    public static final int HEADER = 0xd5;
    public static UpdateGroupTopicChangedObsolete fromBytes(byte[] data) throws IOException {
        return Bser.parse(new UpdateGroupTopicChangedObsolete(), data);
    }

    private int groupId;
    private long rid;
    private int uid;
    private String topic;
    private long date;

    public UpdateGroupTopicChangedObsolete(int groupId, long rid, int uid, @Nullable String topic, long date) {
        this.groupId = groupId;
        this.rid = rid;
        this.uid = uid;
        this.topic = topic;
        this.date = date;
    }

    public UpdateGroupTopicChangedObsolete() {

    }

    public int getGroupId() {
        return this.groupId;
    }

    public long getRid() {
        return this.rid;
    }

    public int getUid() {
        return this.uid;
    }

    @Nullable
    public String getTopic() {
        return this.topic;
    }

    public long getDate() {
        return this.date;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.groupId = values.getInt(1);
        this.rid = values.getLong(2);
        this.uid = values.getInt(3);
        this.topic = values.optString(4);
        this.date = values.getLong(5);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.groupId);
        writer.writeLong(2, this.rid);
        writer.writeInt(3, this.uid);
        if (this.topic != null) {
            writer.writeString(4, this.topic);
        }
        writer.writeLong(5, this.date);
    }

    @Override
    public String toString() {
        String res = "update GroupTopicChangedObsolete{";
        res += "groupId=" + this.groupId;
        res += ", rid=" + this.rid;
        res += ", uid=" + this.uid;
        res += ", topic=" + this.topic;
        res += ", date=" + this.date;
        res += "}";
        return res;
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}