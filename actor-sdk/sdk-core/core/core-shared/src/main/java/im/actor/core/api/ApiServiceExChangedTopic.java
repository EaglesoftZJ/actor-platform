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

public class ApiServiceExChangedTopic extends ApiServiceEx {

    private String topic;

    public ApiServiceExChangedTopic(@Nullable String topic) {
        this.topic = topic;
    }

    public ApiServiceExChangedTopic() {

    }

    public int getHeader() {
        return 18;
    }

    @Nullable
    public String getTopic() {
        return this.topic;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.topic = values.optString(1);
        if (values.hasRemaining()) {
            setUnmappedObjects(values.buildRemaining());
        }
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.topic != null) {
            writer.writeString(1, this.topic);
        }
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
        String res = "struct ServiceExChangedTopic{";
        res += "topic=" + this.topic;
        res += "}";
        return res;
    }

}
