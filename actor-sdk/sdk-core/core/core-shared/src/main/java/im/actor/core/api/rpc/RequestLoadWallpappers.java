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

public class RequestLoadWallpappers extends Request<ResponseLoadWallpappers> {

    public static final int HEADER = 0xf1;
    public static RequestLoadWallpappers fromBytes(byte[] data) throws IOException {
        return Bser.parse(new RequestLoadWallpappers(), data);
    }

    private int maxWidth;
    private int maxHeight;

    public RequestLoadWallpappers(int maxWidth, int maxHeight) {
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
    }

    public RequestLoadWallpappers() {

    }

    public int getMaxWidth() {
        return this.maxWidth;
    }

    public int getMaxHeight() {
        return this.maxHeight;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.maxWidth = values.getInt(1);
        this.maxHeight = values.getInt(2);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.maxWidth);
        writer.writeInt(2, this.maxHeight);
    }

    @Override
    public String toString() {
        String res = "rpc LoadWallpappers{";
        res += "maxWidth=" + this.maxWidth;
        res += ", maxHeight=" + this.maxHeight;
        res += "}";
        return res;
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
