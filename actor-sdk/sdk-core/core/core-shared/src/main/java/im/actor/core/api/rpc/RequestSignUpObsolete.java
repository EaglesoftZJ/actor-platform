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

public class RequestSignUpObsolete extends Request<ResponseAuth> {

    public static final int HEADER = 0x4;
    public static RequestSignUpObsolete fromBytes(byte[] data) throws IOException {
        return Bser.parse(new RequestSignUpObsolete(), data);
    }

    private long phoneNumber;
    private String smsHash;
    private String smsCode;
    private String name;
    private byte[] deviceHash;
    private String deviceTitle;
    private int appId;
    private String appKey;
    private boolean isSilent;

    public RequestSignUpObsolete(long phoneNumber, @NotNull String smsHash, @NotNull String smsCode, @NotNull String name, @NotNull byte[] deviceHash, @NotNull String deviceTitle, int appId, @NotNull String appKey, boolean isSilent) {
        this.phoneNumber = phoneNumber;
        this.smsHash = smsHash;
        this.smsCode = smsCode;
        this.name = name;
        this.deviceHash = deviceHash;
        this.deviceTitle = deviceTitle;
        this.appId = appId;
        this.appKey = appKey;
        this.isSilent = isSilent;
    }

    public RequestSignUpObsolete() {

    }

    public long getPhoneNumber() {
        return this.phoneNumber;
    }

    @NotNull
    public String getSmsHash() {
        return this.smsHash;
    }

    @NotNull
    public String getSmsCode() {
        return this.smsCode;
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    @NotNull
    public byte[] getDeviceHash() {
        return this.deviceHash;
    }

    @NotNull
    public String getDeviceTitle() {
        return this.deviceTitle;
    }

    public int getAppId() {
        return this.appId;
    }

    @NotNull
    public String getAppKey() {
        return this.appKey;
    }

    public boolean isSilent() {
        return this.isSilent;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.phoneNumber = values.getLong(1);
        this.smsHash = values.getString(2);
        this.smsCode = values.getString(3);
        this.name = values.getString(4);
        this.deviceHash = values.getBytes(7);
        this.deviceTitle = values.getString(8);
        this.appId = values.getInt(9);
        this.appKey = values.getString(10);
        this.isSilent = values.getBool(11);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeLong(1, this.phoneNumber);
        if (this.smsHash == null) {
            throw new IOException();
        }
        writer.writeString(2, this.smsHash);
        if (this.smsCode == null) {
            throw new IOException();
        }
        writer.writeString(3, this.smsCode);
        if (this.name == null) {
            throw new IOException();
        }
        writer.writeString(4, this.name);
        if (this.deviceHash == null) {
            throw new IOException();
        }
        writer.writeBytes(7, this.deviceHash);
        if (this.deviceTitle == null) {
            throw new IOException();
        }
        writer.writeString(8, this.deviceTitle);
        writer.writeInt(9, this.appId);
        if (this.appKey == null) {
            throw new IOException();
        }
        writer.writeString(10, this.appKey);
        writer.writeBool(11, this.isSilent);
    }

    @Override
    public String toString() {
        String res = "rpc SignUpObsolete{";
        res += "name=" + this.name;
        res += ", deviceHash=" + byteArrayToString(this.deviceHash);
        res += ", deviceTitle=" + this.deviceTitle;
        res += "}";
        return res;
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
