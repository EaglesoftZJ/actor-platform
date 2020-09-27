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

public class ApiAvatar extends BserObject {

    private ApiAvatarImage smallImage;
    private ApiAvatarImage largeImage;
    private ApiAvatarImage fullImage;

    public ApiAvatar(@Nullable ApiAvatarImage smallImage, @Nullable ApiAvatarImage largeImage, @Nullable ApiAvatarImage fullImage) {
        this.smallImage = smallImage;
        this.largeImage = largeImage;
        this.fullImage = fullImage;
    }

    public ApiAvatar() {

    }

    @Nullable
    public ApiAvatarImage getSmallImage() {
        return this.smallImage;
    }

    @Nullable
    public ApiAvatarImage getLargeImage() {
        return this.largeImage;
    }

    @Nullable
    public ApiAvatarImage getFullImage() {
        return this.fullImage;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.smallImage = values.optObj(1, new ApiAvatarImage());
        this.largeImage = values.optObj(2, new ApiAvatarImage());
        this.fullImage = values.optObj(3, new ApiAvatarImage());
        if (values.hasRemaining()) {
            setUnmappedObjects(values.buildRemaining());
        }
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.smallImage != null) {
            writer.writeObject(1, this.smallImage);
        }
        if (this.largeImage != null) {
            writer.writeObject(2, this.largeImage);
        }
        if (this.fullImage != null) {
            writer.writeObject(3, this.fullImage);
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
        String res = "struct Avatar{";
        res += "smallImage=" + (this.smallImage != null ? "set":"empty");
        res += ", largeImage=" + (this.largeImage != null ? "set":"empty");
        res += ", fullImage=" + (this.fullImage != null ? "set":"empty");
        res += "}";
        return res;
    }

}
