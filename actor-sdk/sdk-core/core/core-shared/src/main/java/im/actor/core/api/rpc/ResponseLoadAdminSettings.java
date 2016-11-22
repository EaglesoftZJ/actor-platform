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

public class ResponseLoadAdminSettings extends Response {

    public static final int HEADER = 0xaea;
    public static ResponseLoadAdminSettings fromBytes(byte[] data) throws IOException {
        return Bser.parse(new ResponseLoadAdminSettings(), data);
    }

    private ApiAdminSettings settings;

    public ResponseLoadAdminSettings(@NotNull ApiAdminSettings settings) {
        this.settings = settings;
    }

    public ResponseLoadAdminSettings() {

    }

    @NotNull
    public ApiAdminSettings getSettings() {
        return this.settings;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.settings = values.getObj(1, new ApiAdminSettings());
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.settings == null) {
            throw new IOException();
        }
        writer.writeObject(1, this.settings);
    }

    @Override
    public String toString() {
        String res = "tuple LoadAdminSettings{";
        res += "}";
        return res;
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
