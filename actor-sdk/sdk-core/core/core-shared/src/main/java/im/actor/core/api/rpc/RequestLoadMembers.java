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

public class RequestLoadMembers extends Request<ResponseLoadMembers> {

    public static final int HEADER = 0xae2;
    public static RequestLoadMembers fromBytes(byte[] data) throws IOException {
        return Bser.parse(new RequestLoadMembers(), data);
    }

    private ApiGroupOutPeer group;
    private int limit;
    private byte[] next;

    public RequestLoadMembers(@NotNull ApiGroupOutPeer group, int limit, @Nullable byte[] next) {
        this.group = group;
        this.limit = limit;
        this.next = next;
    }

    public RequestLoadMembers() {

    }

    @NotNull
    public ApiGroupOutPeer getGroup() {
        return this.group;
    }

    public int getLimit() {
        return this.limit;
    }

    @Nullable
    public byte[] getNext() {
        return this.next;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.group = values.getObj(1, new ApiGroupOutPeer());
        this.limit = values.getInt(2);
        this.next = values.optBytes(3);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.group == null) {
            throw new IOException();
        }
        writer.writeObject(1, this.group);
        writer.writeInt(2, this.limit);
        if (this.next != null) {
            writer.writeBytes(3, this.next);
        }
    }

    @Override
    public String toString() {
        String res = "rpc LoadMembers{";
        res += "group=" + this.group;
        res += ", limit=" + this.limit;
        res += ", next=" + this.next;
        res += "}";
        return res;
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
