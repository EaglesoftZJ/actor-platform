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

public class ResponseSendAuthCodeObsolete extends Response {

    public static final int HEADER = 0x2;
    public static ResponseSendAuthCodeObsolete fromBytes(byte[] data) throws IOException {
        return Bser.parse(new ResponseSendAuthCodeObsolete(), data);
    }

    private String smsHash;
    private boolean isRegistered;

    public ResponseSendAuthCodeObsolete(@NotNull String smsHash, boolean isRegistered) {
        this.smsHash = smsHash;
        this.isRegistered = isRegistered;
    }

    public ResponseSendAuthCodeObsolete() {

    }

    @NotNull
    public String getSmsHash() {
        return this.smsHash;
    }

    public boolean isRegistered() {
        return this.isRegistered;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.smsHash = values.getString(1);
        this.isRegistered = values.getBool(2);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.smsHash == null) {
            throw new IOException();
        }
        writer.writeString(1, this.smsHash);
        writer.writeBool(2, this.isRegistered);
    }

    @Override
    public String toString() {
        String res = "tuple SendAuthCodeObsolete{";
        res += "}";
        return res;
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
