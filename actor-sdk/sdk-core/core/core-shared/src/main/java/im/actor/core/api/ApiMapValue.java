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

public class ApiMapValue extends ApiRawValue {

    private List<ApiMapValueItem> items;

    public ApiMapValue(@NotNull List<ApiMapValueItem> items) {
        this.items = items;
    }

    public ApiMapValue() {

    }

    public int getHeader() {
        return 6;
    }

    @NotNull
    public List<ApiMapValueItem> getItems() {
        return this.items;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        List<ApiMapValueItem> _items = new ArrayList<ApiMapValueItem>();
        for (int i = 0; i < values.getRepeatedCount(1); i ++) {
            _items.add(new ApiMapValueItem());
        }
        this.items = values.getRepeatedObj(1, _items);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeRepeatedObj(1, this.items);
    }

    @Override
    public String toString() {
        String res = "struct MapValue{";
        res += "}";
        return res;
    }

}
