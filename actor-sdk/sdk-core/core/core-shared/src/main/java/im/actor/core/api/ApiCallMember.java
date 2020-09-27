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

public class ApiCallMember extends BserObject {

    private int userId;
    private ApiCallMemberStateHolder state;

    public ApiCallMember(int userId, @NotNull ApiCallMemberStateHolder state) {
        this.userId = userId;
        this.state = state;
    }

    public ApiCallMember() {

    }

    public int getUserId() {
        return this.userId;
    }

    @NotNull
    public ApiCallMemberStateHolder getState() {
        return this.state;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.userId = values.getInt(1);
        this.state = values.getObj(3, new ApiCallMemberStateHolder());
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.userId);
        if (this.state == null) {
            throw new IOException();
        }
        writer.writeObject(3, this.state);
    }

    @Override
    public String toString() {
        String res = "struct CallMember{";
        res += "userId=" + this.userId;
        res += ", state=" + this.state;
        res += "}";
        return res;
    }

}
