package im.actor.core.api;
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

public class ApiSearchPeerTypeCondition extends ApiSearchCondition {

    private ApiSearchPeerType peerType;

    public ApiSearchPeerTypeCondition(@NotNull ApiSearchPeerType peerType) {
        this.peerType = peerType;
    }

    public ApiSearchPeerTypeCondition() {

    }

    public int getHeader() {
        return 1;
    }

    @NotNull
    public ApiSearchPeerType getPeerType() {
        return this.peerType;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.peerType = ApiSearchPeerType.parse(values.getInt(1));
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.peerType == null) {
            throw new IOException();
        }
        writer.writeInt(1, this.peerType.getValue());
    }

    @Override
    public String toString() {
        String res = "struct SearchPeerTypeCondition{";
        res += "peerType=" + this.peerType;
        res += "}";
        return res;
    }

}
