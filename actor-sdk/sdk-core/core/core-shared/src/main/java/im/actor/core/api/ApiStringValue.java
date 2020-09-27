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

public class ApiStringValue extends ApiRawValue {

    private String text;

    public ApiStringValue(@NotNull String text) {
        this.text = text;
    }

    public ApiStringValue() {

    }

    public int getHeader() {
        return 1;
    }

    @NotNull
    public String getText() {
        return this.text;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.text = values.getString(1);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.text == null) {
            throw new IOException();
        }
        writer.writeString(1, this.text);
    }

    @Override
    public String toString() {
        String res = "struct StringValue{";
        res += "}";
        return res;
    }

}
