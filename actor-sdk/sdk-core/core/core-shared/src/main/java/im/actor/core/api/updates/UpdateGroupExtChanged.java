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

public class UpdateGroupExtChanged extends Update {

    public static final int HEADER = 0xa35;
    public static UpdateGroupExtChanged fromBytes(byte[] data) throws IOException {
        return Bser.parse(new UpdateGroupExtChanged(), data);
    }

    private int groupId;
    private ApiMapValue ext;

    public UpdateGroupExtChanged(int groupId, @Nullable ApiMapValue ext) {
        this.groupId = groupId;
        this.ext = ext;
    }

    public UpdateGroupExtChanged() {

    }

    public int getGroupId() {
        return this.groupId;
    }

    @Nullable
    public ApiMapValue getExt() {
        return this.ext;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.groupId = values.getInt(1);
        this.ext = values.optObj(2, new ApiMapValue());
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.groupId);
        if (this.ext != null) {
            writer.writeObject(2, this.ext);
        }
    }

    @Override
    public String toString() {
        String res = "update GroupExtChanged{";
        res += "groupId=" + this.groupId;
        res += ", ext=" + this.ext;
        res += "}";
        return res;
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
