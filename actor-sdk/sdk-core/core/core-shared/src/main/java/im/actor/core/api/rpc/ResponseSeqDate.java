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

public class ResponseSeqDate extends Response {

    public static final int HEADER = 0x66;
    public static ResponseSeqDate fromBytes(byte[] data) throws IOException {
        return Bser.parse(new ResponseSeqDate(), data);
    }

    private int seq;
    private byte[] state;
    private long date;

    public ResponseSeqDate(int seq, @NotNull byte[] state, long date) {
        this.seq = seq;
        this.state = state;
        this.date = date;
    }

    public ResponseSeqDate() {

    }

    public int getSeq() {
        return this.seq;
    }

    @NotNull
    public byte[] getState() {
        return this.state;
    }

    public long getDate() {
        return this.date;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.seq = values.getInt(1);
        this.state = values.getBytes(2);
        this.date = values.getLong(3);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.seq);
        if (this.state == null) {
            throw new IOException();
        }
        writer.writeBytes(2, this.state);
        writer.writeLong(3, this.date);
    }

    @Override
    public String toString() {
        String res = "response SeqDate{";
        res += "seq=" + this.seq;
        res += ", state=" + byteArrayToString(this.state);
        res += ", date=" + this.date;
        res += "}";
        return res;
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
