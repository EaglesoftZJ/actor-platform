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

public class ResponseSearchContacts extends Response {

    public static final int HEADER = 0x71;
    public static ResponseSearchContacts fromBytes(byte[] data) throws IOException {
        return Bser.parse(new ResponseSearchContacts(), data);
    }

    private List<ApiUser> users;
    private List<ApiUserOutPeer> userPeers;

    public ResponseSearchContacts(@NotNull List<ApiUser> users, @NotNull List<ApiUserOutPeer> userPeers) {
        this.users = users;
        this.userPeers = userPeers;
    }

    public ResponseSearchContacts() {

    }

    @NotNull
    public List<ApiUser> getUsers() {
        return this.users;
    }

    @NotNull
    public List<ApiUserOutPeer> getUserPeers() {
        return this.userPeers;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        List<ApiUser> _users = new ArrayList<ApiUser>();
        for (int i = 0; i < values.getRepeatedCount(1); i ++) {
            _users.add(new ApiUser());
        }
        this.users = values.getRepeatedObj(1, _users);
        List<ApiUserOutPeer> _userPeers = new ArrayList<ApiUserOutPeer>();
        for (int i = 0; i < values.getRepeatedCount(2); i ++) {
            _userPeers.add(new ApiUserOutPeer());
        }
        this.userPeers = values.getRepeatedObj(2, _userPeers);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeRepeatedObj(1, this.users);
        writer.writeRepeatedObj(2, this.userPeers);
    }

    @Override
    public String toString() {
        String res = "tuple SearchContacts{";
        res += "}";
        return res;
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
