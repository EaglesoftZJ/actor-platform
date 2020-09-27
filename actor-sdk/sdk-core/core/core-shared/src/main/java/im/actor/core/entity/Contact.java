/*
 * Copyright (C) 2015 Actor LLC. <https://actor.im>
 */

package im.actor.core.entity;

import com.google.j2objc.annotations.Property;

import org.jetbrains.annotations.NotNull;
import androidx.annotation.Nullable;

import java.io.IOException;

import im.actor.core.Messenger;
import im.actor.core.pinyin.HanziToPinyin;
import im.actor.runtime.bser.BserCreator;
import im.actor.runtime.bser.BserObject;
import im.actor.runtime.bser.BserValues;
import im.actor.runtime.bser.BserWriter;
import im.actor.runtime.storage.ListEngineItem;

// Disabling Bounds checks for speeding up calculations

/*-[
#define J2OBJC_DISABLE_ARRAY_BOUND_CHECKS 1
]-*/

public class Contact extends BserObject implements ListEngineItem {

    public static final BserCreator<Contact> CREATOR = new BserCreator<Contact>() {
        @Override
        public Contact createInstance() {
            return new Contact();
        }
    };

    public static final String ENTITY_NAME = "Contact";

    @Property("readonly, nonatomic")
    private int uid;
    private long sortKey;
    @Nullable
    @Property("readonly, nonatomic")
    private Avatar avatar;
    @SuppressWarnings("NullableProblems")
    @NotNull
    @Property("readonly, nonatomic")
    private String name;

    /**
     * 名字首字母
     */
    @Property("readonly, nonatomic")
    private String pyShort;

    public Contact(int uid, long sortKey, @Nullable Avatar avatar, @NotNull String name) {
        long hqtime = System.currentTimeMillis();
        this.uid = uid;
        this.sortKey = sortKey;
        this.avatar = avatar;
        this.name = name;
        try {
            if (name != null && name.length() > 0) {
//                this.pyShort = ChineseCharToEnUtil.getInstance().getAllFirstLetter(name.substring(0, 1)).toLowerCase();
                this.pyShort = HanziToPinyin.getInstance().get(name.substring(0, 1)).get(0).target.substring(0, 1).toUpperCase();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Contact() {

    }

    public int getUid() {
        return uid;
    }

    @Nullable
    public Avatar getAvatar() {
        return avatar;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        long hqtime = System.currentTimeMillis();
        uid = values.getInt(1);
        sortKey = values.getLong(2);
        name = values.getString(3);
        if (values.optBytes(4) != null) {
            avatar = new Avatar(values.getBytes(4));
        }

        try {
            if (name != null && name.length() > 0) {
//                pyShort = ChineseCharToEnUtil.getInstance().getAllFirstLetter(name.substring(0, 1)).toLowerCase();
                pyShort = HanziToPinyin.getInstance().get(name.substring(0, 1)).get(0).target.substring(0, 1).toUpperCase();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Long s = (System.currentTimeMillis() - hqtime);
        Messenger.pyTime2 += s;

    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, uid);
        writer.writeLong(2, sortKey);
        writer.writeString(3, name);
        if (avatar != null) {
            writer.writeObject(4, avatar);
        }
        if (pyShort != null) {
            writer.writeString(5, pyShort);
        }
    }

    @Override
    public long getEngineId() {
        return uid;
    }

    @Override
    public long getEngineSort() {
        return sortKey;
    }

    @Override
    public String getEngineSearch() {
        return name;
    }

    public String getPyShort() {
        return pyShort;
    }

    public void setPyShort(String pyShort) {
        this.pyShort = pyShort;
    }

}
