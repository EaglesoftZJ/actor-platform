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

public class UpdateGroupTopicChanged extends Update {

    public static final int HEADER = 0xa38;
    public static UpdateGroupTopicChanged fromBytes(byte[] data) throws IOException {
        return Bser.parse(new UpdateGroupTopicChanged(), data);
    }

    private int groupId;
    private String topic;

    public UpdateGroupTopicChanged(int groupId, @Nullable String topic) {
        this.groupId = groupId;
        this.topic = topic;
    }

    public UpdateGroupTopicChanged() {

    }

    public int getGroupId() {
        return this.groupId;
    }

    @Nullable
    public String getTopic() {
        return this.topic;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.groupId = values.getInt(1);
        this.topic = values.optString(2);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.groupId);
        if (this.topic != null) {
            writer.writeString(2, this.topic);
        }
    }

    @Override
    public String toString() {
        String res = "update GroupTopicChanged{";
        res += "groupId=" + this.groupId;
        res += ", topic=" + this.topic;
        res += "}";
        return res;
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
