package im.actor.core.api.rpc;
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

public class ResponseLoadMembers extends Response {

    public static final int HEADER = 0xae3;
    public static ResponseLoadMembers fromBytes(byte[] data) throws IOException {
        return Bser.parse(new ResponseLoadMembers(), data);
    }

    private List<ApiMember> members;
    private List<ApiUserOutPeer> users;
    private byte[] next;

    public ResponseLoadMembers(@NotNull List<ApiMember> members, @NotNull List<ApiUserOutPeer> users, @Nullable byte[] next) {
        this.members = members;
        this.users = users;
        this.next = next;
    }

    public ResponseLoadMembers() {

    }

    @NotNull
    public List<ApiMember> getMembers() {
        return this.members;
    }

    @NotNull
    public List<ApiUserOutPeer> getUsers() {
        return this.users;
    }

    @Nullable
    public byte[] getNext() {
        return this.next;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        List<ApiMember> _members = new ArrayList<ApiMember>();
        for (int i = 0; i < values.getRepeatedCount(3); i ++) {
            _members.add(new ApiMember());
        }
        this.members = values.getRepeatedObj(3, _members);
        List<ApiUserOutPeer> _users = new ArrayList<ApiUserOutPeer>();
        for (int i = 0; i < values.getRepeatedCount(1); i ++) {
            _users.add(new ApiUserOutPeer());
        }
        this.users = values.getRepeatedObj(1, _users);
        this.next = values.optBytes(2);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeRepeatedObj(3, this.members);
        writer.writeRepeatedObj(1, this.users);
        if (this.next != null) {
            writer.writeBytes(2, this.next);
        }
    }

    @Override
    public String toString() {
        String res = "tuple LoadMembers{";
        res += "}";
        return res;
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
