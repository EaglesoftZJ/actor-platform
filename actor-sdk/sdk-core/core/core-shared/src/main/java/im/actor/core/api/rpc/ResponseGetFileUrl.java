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

public class ResponseGetFileUrl extends Response {

    public static final int HEADER = 0x4e;
    public static ResponseGetFileUrl fromBytes(byte[] data) throws IOException {
        return Bser.parse(new ResponseGetFileUrl(), data);
    }

    private String url;
    private int timeout;
    private String unsignedUrl;
    private List<ApiHTTPHeader> unsignedUrlHeaders;

    public ResponseGetFileUrl(@NotNull String url, int timeout, @Nullable String unsignedUrl, @NotNull List<ApiHTTPHeader> unsignedUrlHeaders) {
        this.url = url;
        this.timeout = timeout;
        this.unsignedUrl = unsignedUrl;
        this.unsignedUrlHeaders = unsignedUrlHeaders;
    }

    public ResponseGetFileUrl() {

    }

    @NotNull
    public String getUrl() {
        return this.url;
    }

    public int getTimeout() {
        return this.timeout;
    }

    @Nullable
    public String getUnsignedUrl() {
        return this.unsignedUrl;
    }

    @NotNull
    public List<ApiHTTPHeader> getUnsignedUrlHeaders() {
        return this.unsignedUrlHeaders;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.url = values.getString(1);
        this.timeout = values.getInt(2);
        this.unsignedUrl = values.optString(3);
        List<ApiHTTPHeader> _unsignedUrlHeaders = new ArrayList<ApiHTTPHeader>();
        for (int i = 0; i < values.getRepeatedCount(4); i ++) {
            _unsignedUrlHeaders.add(new ApiHTTPHeader());
        }
        this.unsignedUrlHeaders = values.getRepeatedObj(4, _unsignedUrlHeaders);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.url == null) {
            throw new IOException();
        }
        writer.writeString(1, this.url);
        writer.writeInt(2, this.timeout);
        if (this.unsignedUrl != null) {
            writer.writeString(3, this.unsignedUrl);
        }
        writer.writeRepeatedObj(4, this.unsignedUrlHeaders);
    }

    @Override
    public String toString() {
        String res = "tuple GetFileUrl{";
        res += "}";
        return res;
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
