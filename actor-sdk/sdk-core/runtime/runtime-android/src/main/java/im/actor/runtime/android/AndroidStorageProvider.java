/*
 * Copyright (C) 2015 Actor LLC. <https://actor.im>
 */

package im.actor.runtime.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import im.actor.runtime.StorageRuntime;
import im.actor.runtime.android.storage.AndroidProperties;
import im.actor.runtime.android.storage.NoOpOpenHelper;
import im.actor.runtime.android.storage.SQLiteKeyValue;
import im.actor.runtime.android.storage.SQLiteList;
import im.actor.runtime.storage.KeyValueStorage;
import im.actor.runtime.storage.ListStorage;
import im.actor.runtime.storage.PreferencesStorage;

public class AndroidStorageProvider implements StorageRuntime {

    private static final String DB = "ACTOR";

    private SQLiteDatabase database;
    private AndroidProperties properties;

    public AndroidStorageProvider() {
        this.properties = new AndroidProperties(AndroidContext.getContext());
    }

    @Override
    public PreferencesStorage createPreferencesStorage() {
        return properties;
    }

    @Override
    public KeyValueStorage createKeyValue(String name) {
        return new SQLiteKeyValue(getDatabase(), "kv_" + name);
    }

    @Override
    public ListStorage createList(String name) {
        return new SQLiteList(getDatabase(), "ls_" + name);
    }

    /**
     * 安卓移动端释放缓存
     */
    @Override
    public void resetStorage() {
        properties.clear();
        SharedPreferences spUswer = AndroidContext.getContext().getSharedPreferences("userList", Context.MODE_PRIVATE);
        SharedPreferences spIp =AndroidContext.getContext().getSharedPreferences("ipLogin",Context.MODE_PRIVATE);
        SharedPreferences spLogin = AndroidContext.getContext().getSharedPreferences("userLogin",Context.MODE_PRIVATE);
        SharedPreferences ipList =  AndroidContext.getContext().getSharedPreferences("ipList",Context.MODE_PRIVATE);
        spUswer.edit().clear().commit();
        spIp.edit().clear().commit();
        spLogin.edit().clear().commit();
        ipList.edit().clear().commit();

        ArrayList<String> tables = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT name FROM sqlite_master WHERE type='table';", null);
        try {
            while (cursor.moveToNext()) {
                tables.add(cursor.getString(0));
            }
        } finally {
            cursor.close();
        }
        for (String s : tables) {
            getDatabase().execSQL("drop table " + s + ";");
        }
    }

    private synchronized SQLiteDatabase getDatabase() {
        if (database == null) {
            NoOpOpenHelper helper = new NoOpOpenHelper(AndroidContext.getContext(), DB);
            database = helper.getWritableDatabase();
        }
        return database;
    }
}
