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

public class UpdateGroupAvatarChanged extends Update {

    public static final int HEADER = 0xa32;
    public static UpdateGroupAvatarChanged fromBytes(byte[] data) throws IOException {
        return Bser.parse(new UpdateGroupAvatarChanged(), data);
    }

    private int groupId;
    private ApiAvatar avatar;

    public UpdateGroupAvatarChanged(int groupId, @Nullable ApiAvatar avatar) {
        this.groupId = groupId;
        this.avatar = avatar;
    }

    public UpdateGroupAvatarChanged() {

    }

    public int getGroupId() {
        return this.groupId;
    }

    @Nullable
    public ApiAvatar getAvatar() {
        return this.avatar;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.groupId = values.getInt(1);
        this.avatar = values.optObj(2, new ApiAvatar());
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.groupId);
        if (this.avatar != null) {
            writer.writeObject(2, this.avatar);
        }
    }

    @Override
    public String toString() {
        String res = "update GroupAvatarChanged{";
        res += "groupId=" + this.groupId;
        res += ", avatar=" + this.avatar;
        res += "}";
        return res;
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
