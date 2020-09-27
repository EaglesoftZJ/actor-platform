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

public class ApiAppVisibleChanged extends ApiEvent {

    private boolean visible;

    public ApiAppVisibleChanged(boolean visible) {
        this.visible = visible;
    }

    public ApiAppVisibleChanged() {

    }

    public int getHeader() {
        return 4;
    }

    public boolean visible() {
        return this.visible;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.visible = values.getBool(1);
        if (values.hasRemaining()) {
            setUnmappedObjects(values.buildRemaining());
        }
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeBool(1, this.visible);
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
        String res = "struct AppVisibleChanged{";
        res += "visible=" + this.visible;
        res += "}";
        return res;
    }

}
