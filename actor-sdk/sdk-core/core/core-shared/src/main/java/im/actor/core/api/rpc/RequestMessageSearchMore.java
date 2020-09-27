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

public class RequestMessageSearchMore extends Request<ResponseMessageSearchResponse> {

    public static final int HEADER = 0xde;
    public static RequestMessageSearchMore fromBytes(byte[] data) throws IOException {
        return Bser.parse(new RequestMessageSearchMore(), data);
    }

    private byte[] loadMoreState;
    private List<ApiUpdateOptimization> optimizations;

    public RequestMessageSearchMore(@NotNull byte[] loadMoreState, @NotNull List<ApiUpdateOptimization> optimizations) {
        this.loadMoreState = loadMoreState;
        this.optimizations = optimizations;
    }

    public RequestMessageSearchMore() {

    }

    @NotNull
    public byte[] getLoadMoreState() {
        return this.loadMoreState;
    }

    @NotNull
    public List<ApiUpdateOptimization> getOptimizations() {
        return this.optimizations;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.loadMoreState = values.getBytes(1);
        this.optimizations = new ArrayList<ApiUpdateOptimization>();
        for (int b : values.getRepeatedInt(2)) {
            optimizations.add(ApiUpdateOptimization.parse(b));
        }
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.loadMoreState == null) {
            throw new IOException();
        }
        writer.writeBytes(1, this.loadMoreState);
        for (ApiUpdateOptimization i : this.optimizations) {
            writer.writeInt(2, i.getValue());
        }
    }

    @Override
    public String toString() {
        String res = "rpc MessageSearchMore{";
        res += "optimizations=" + this.optimizations;
        res += "}";
        return res;
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
