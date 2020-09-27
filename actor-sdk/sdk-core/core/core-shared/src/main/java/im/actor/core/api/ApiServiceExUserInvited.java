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

public class ApiServiceExUserInvited extends ApiServiceEx {

    private int invitedUid;

    public ApiServiceExUserInvited(int invitedUid) {
        this.invitedUid = invitedUid;
    }

    public ApiServiceExUserInvited() {

    }

    public int getHeader() {
        return 1;
    }

    public int getInvitedUid() {
        return this.invitedUid;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.invitedUid = values.getInt(1);
        if (values.hasRemaining()) {
            setUnmappedObjects(values.buildRemaining());
        }
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.invitedUid);
        if (this.getUnmappedObjects() != null) {
            SparseArray<Object> unmapped = this.getUnmappedObjects();
            for (int i = 0; i < unmapped.size(); i++) {
                int key = unmapped.keyAt(i);
                writer.writeUnmapped(key, unmapped.get(key));
            }
        }
    }

    @Override
    public String toString() {
        String res = "struct ServiceExUserInvited{";
        res += "invitedUid=" + this.invitedUid;
        res += "}";
        return res;
    }

}
