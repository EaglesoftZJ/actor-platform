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

public class RequestTyping extends Request<ResponseVoid> {

    public static final int HEADER = 0x1b;
    public static RequestTyping fromBytes(byte[] data) throws IOException {
        return Bser.parse(new RequestTyping(), data);
    }

    private ApiOutPeer peer;
    private ApiTypingType typingType;

    public RequestTyping(@NotNull ApiOutPeer peer, @NotNull ApiTypingType typingType) {
        this.peer = peer;
        this.typingType = typingType;
    }

    public RequestTyping() {

    }

    @NotNull
    public ApiOutPeer getPeer() {
        return this.peer;
    }

    @NotNull
    public ApiTypingType getTypingType() {
        return this.typingType;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.peer = values.getObj(1, new ApiOutPeer());
        this.typingType = ApiTypingType.parse(values.getInt(3));
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.peer == null) {
            throw new IOException();
        }
        writer.writeObject(1, this.peer);
        if (this.typingType == null) {
            throw new IOException();
        }
        writer.writeInt(3, this.typingType.getValue());
    }

    @Override
    public String toString() {
        String res = "rpc Typing{";
        res += "peer=" + this.peer;
        res += ", typingType=" + this.typingType;
        res += "}";
        return res;
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
