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

public class ApiMapValueItem extends BserObject {

    private String key;
    private ApiRawValue value;

    public ApiMapValueItem(@NotNull String key, @NotNull ApiRawValue value) {
        this.key = key;
        this.value = value;
    }

    public ApiMapValueItem() {

    }

    @NotNull
    public String getKey() {
        return this.key;
    }

    @NotNull
    public ApiRawValue getValue() {
        return this.value;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.key = values.getString(1);
        this.value = ApiRawValue.fromBytes(values.getBytes(2));
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.key == null) {
            throw new IOException();
        }
        writer.writeString(1, this.key);
        if (this.value == null) {
            throw new IOException();
        }

        writer.writeBytes(2, this.value.buildContainer());
    }

    @Override
    public String toString() {
        String res = "struct MapValueItem{";
        res += "}";
        return res;
    }

}
