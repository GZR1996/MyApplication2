package com.example.a.myapplication.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by a on 2016/7/30.
 */
public class MailOpenHelper extends SQLiteOpenHelper {

    public static final String CREATE_MAIL_INFORMATION = "create table MailInformation (" +
            "id integer primary key autoincrement," +
            "mail_user text," +
            "subject text," +
            "mail_from text," +
            "receive_address text," +
            "sent_date text," +
            "priority text," +
            "seen integer," +
            "reply integer," +
            "is_contain_attachment integer," +
            "size integer," +
            "content text," +
            "html text)";

    private static final String CREATE_USER = "create table User (" +
            "id integer primary key autoincrement," +
            "user text," +
            "password text," +
            "num_new_message integer," +
            "num_unread_message integer," +
            "num_message integer)";

    public MailOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context,name,factory,version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MAIL_INFORMATION);
        db.execSQL(CREATE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

