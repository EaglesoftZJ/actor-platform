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

public class ApiUpdateContainer extends BserObject {

    private int updateHeader;
    private byte[] update;

    public ApiUpdateContainer(int updateHeader, @NotNull byte[] update) {
        this.updateHeader = updateHeader;
        this.update = update;
    }

    public ApiUpdateContainer() {

    }

    public int getUpdateHeader() {
        return this.updateHeader;
    }

    @NotNull
    public byte[] getUpdate() {
        return this.update;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.updateHeader = values.getInt(1);
        this.update = values.getBytes(2);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.updateHeader);
        if (this.update == null) {
            throw new IOException();
        }
        writer.writeBytes(2, this.update);
    }

    @Override
    public String toString() {
        String res = "struct UpdateContainer{";
        res += "updateHeader=" + this.updateHeader;
        res += ", update=" + byteArrayToStringCompact(this.update);
        res += "}";
        return res;
    }

}
