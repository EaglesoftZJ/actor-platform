package im.actor.core.api.rpc;
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

public class ResponseLoadArchived extends Response {

    public static final int HEADER = 0xa5c;
    public static ResponseLoadArchived fromBytes(byte[] data) throws IOException {
        return Bser.parse(new ResponseLoadArchived(), data);
    }

    private List<ApiGroup> groups;
    private List<ApiUser> users;
    private List<ApiDialog> dialogs;
    private List<ApiUserOutPeer> userPeers;
    private List<ApiGroupOutPeer> groupPeers;
    private byte[] nextOffset;

    public ResponseLoadArchived(@NotNull List<ApiGroup> groups, @NotNull List<ApiUser> users, @NotNull List<ApiDialog> dialogs, @NotNull List<ApiUserOutPeer> userPeers, @NotNull List<ApiGroupOutPeer> groupPeers, @Nullable byte[] nextOffset) {
        this.groups = groups;
        this.users = users;
        this.dialogs = dialogs;
        this.userPeers = userPeers;
        this.groupPeers = groupPeers;
        this.nextOffset = nextOffset;
    }

    public ResponseLoadArchived() {

    }

    @NotNull
    public List<ApiGroup> getGroups() {
        return this.groups;
    }

    @NotNull
    public List<ApiUser> getUsers() {
        return this.users;
    }

    @NotNull
    public List<ApiDialog> getDialogs() {
        return this.dialogs;
    }

    @NotNull
    public List<ApiUserOutPeer> getUserPeers() {
        return this.userPeers;
    }

    @NotNull
    public List<ApiGroupOutPeer> getGroupPeers() {
        return this.groupPeers;
    }

    @Nullable
    public byte[] getNextOffset() {
        return this.nextOffset;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        List<ApiGroup> _groups = new ArrayList<ApiGroup>();
        for (int i = 0; i < values.getRepeatedCount(1); i ++) {
            _groups.add(new ApiGroup());
        }
        this.groups = values.getRepeatedObj(1, _groups);
        List<ApiUser> _users = new ArrayList<ApiUser>();
        for (int i = 0; i < values.getRepeatedCount(2); i ++) {
            _users.add(new ApiUser());
        }
        this.users = values.getRepeatedObj(2, _users);
        List<ApiDialog> _dialogs = new ArrayList<ApiDialog>();
        for (int i = 0; i < values.getRepeatedCount(3); i ++) {
            _dialogs.add(new ApiDialog());
        }
        this.dialogs = values.getRepeatedObj(3, _dialogs);
        List<ApiUserOutPeer> _userPeers = new ArrayList<ApiUserOutPeer>();
        for (int i = 0; i < values.getRepeatedCount(5); i ++) {
            _userPeers.add(new ApiUserOutPeer());
        }
        this.userPeers = values.getRepeatedObj(5, _userPeers);
        List<ApiGroupOutPeer> _groupPeers = new ArrayList<ApiGroupOutPeer>();
        for (int i = 0; i < values.getRepeatedCount(6); i ++) {
            _groupPeers.add(new ApiGroupOutPeer());
        }
        this.groupPeers = values.getRepeatedObj(6, _groupPeers);
        this.nextOffset = values.optBytes(4);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeRepeatedObj(1, this.groups);
        writer.writeRepeatedObj(2, this.users);
        writer.writeRepeatedObj(3, this.dialogs);
        writer.writeRepeatedObj(5, this.userPeers);
        writer.writeRepeatedObj(6, this.groupPeers);
        if (this.nextOffset != null) {
            writer.writeBytes(4, this.nextOffset);
        }
    }

    @Override
    public String toString() {
        String res = "tuple LoadArchived{";
        res += "}";
        return res;
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
