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

public class ResponseAuth extends Response {

    public static final int HEADER = 0x5;
    public static ResponseAuth fromBytes(byte[] data) throws IOException {
        return Bser.parse(new ResponseAuth(), data);
    }

    private ApiUser user;
    private ApiConfig config;

    public ResponseAuth(@NotNull ApiUser user, @NotNull ApiConfig config) {
        this.user = user;
        this.config = config;
    }

    public ResponseAuth() {

    }

    @NotNull
    public ApiUser getUser() {
        return this.user;
    }

    @NotNull
    public ApiConfig getConfig() {
        return this.config;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.user = values.getObj(2, new ApiUser());
        this.config = values.getObj(3, new ApiConfig());
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.user == null) {
            throw new IOException();
        }
        writer.writeObject(2, this.user);
        if (this.config == null) {
            throw new IOException();
        }
        writer.writeObject(3, this.config);
    }

    @Override
    public String toString() {
        String res = "response Auth{";
        res += "user=" + (this.user != null ? "set":"empty");
        res += ", config=" + this.config;
        res += "}";
        return res;
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
