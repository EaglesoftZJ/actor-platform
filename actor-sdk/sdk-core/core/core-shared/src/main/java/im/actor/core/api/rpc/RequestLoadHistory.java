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

public class RequestLoadHistory extends Request<ResponseLoadHistory> {

    public static final int HEADER = 0x76;
    public static RequestLoadHistory fromBytes(byte[] data) throws IOException {
        return Bser.parse(new RequestLoadHistory(), data);
    }

    private ApiOutPeer peer;
    private long date;
    private ApiListLoadMode loadMode;
    private int limit;
    private List<ApiUpdateOptimization> optimizations;

    public RequestLoadHistory(@NotNull ApiOutPeer peer, long date, @Nullable ApiListLoadMode loadMode, int limit, @NotNull List<ApiUpdateOptimization> optimizations) {
        this.peer = peer;
        this.date = date;
        this.loadMode = loadMode;
        this.limit = limit;
        this.optimizations = optimizations;
    }

    public RequestLoadHistory() {

    }

    @NotNull
    public ApiOutPeer getPeer() {
        return this.peer;
    }

    public long getDate() {
        return this.date;
    }

    @Nullable
    public ApiListLoadMode getLoadMode() {
        return this.loadMode;
    }

    public int getLimit() {
        return this.limit;
    }

    @NotNull
    public List<ApiUpdateOptimization> getOptimizations() {
        return this.optimizations;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.peer = values.getObj(1, new ApiOutPeer());
        this.date = values.getLong(3);
        int val_loadMode = values.getInt(5, 0);
        if (val_loadMode != 0) {
            this.loadMode = ApiListLoadMode.parse(val_loadMode);
        }
        this.limit = values.getInt(4);
        this.optimizations = new ArrayList<ApiUpdateOptimization>();
        for (int b : values.getRepeatedInt(6)) {
            optimizations.add(ApiUpdateOptimization.parse(b));
        }
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.peer == null) {
            throw new IOException();
        }
        writer.writeObject(1, this.peer);
        writer.writeLong(3, this.date);
        if (this.loadMode != null) {
            writer.writeInt(5, this.loadMode.getValue());
        }
        writer.writeInt(4, this.limit);
        for (ApiUpdateOptimization i : this.optimizations) {
            writer.writeInt(6, i.getValue());
        }
    }

    @Override
    public String toString() {
        String res = "rpc LoadHistory{";
        res += "peer=" + this.peer;
        res += ", date=" + this.date;
        res += ", loadMode=" + this.loadMode;
        res += ", limit=" + this.limit;
        res += ", optimizations=" + this.optimizations;
        res += "}";
        return res;
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
