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

public class RequestInviteToTeam extends Request<ResponseVoid> {

    public static final int HEADER = 0xa08;
    public static RequestInviteToTeam fromBytes(byte[] data) throws IOException {
        return Bser.parse(new RequestInviteToTeam(), data);
    }

    private ApiUserOutPeer user;
    private ApiOutTeam destTeam;

    public RequestInviteToTeam(@NotNull ApiUserOutPeer user, @NotNull ApiOutTeam destTeam) {
        this.user = user;
        this.destTeam = destTeam;
    }

    public RequestInviteToTeam() {

    }

    @NotNull
    public ApiUserOutPeer getUser() {
        return this.user;
    }

    @NotNull
    public ApiOutTeam getDestTeam() {
        return this.destTeam;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.user = values.getObj(1, new ApiUserOutPeer());
        this.destTeam = values.getObj(3, new ApiOutTeam());
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.user == null) {
            throw new IOException();
        }
        writer.writeObject(1, this.user);
        if (this.destTeam == null) {
            throw new IOException();
        }
        writer.writeObject(3, this.destTeam);
    }

    @Override
    public String toString() {
        String res = "rpc InviteToTeam{";
        res += "user=" + this.user;
        res += ", destTeam=" + this.destTeam;
        res += "}";
        return res;
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
