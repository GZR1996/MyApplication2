package com.example.a.myapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.a.myapplication.model.MailInformation;
import com.example.a.myapplication.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by a on 2016/7/30.
 */
public class MailDB {

    private static final String TAG = "MailDB";
    public static final String DB_NAME = "Mail";
    public static final int VERSION = 1;
    private static MailDB mailDB;
    private SQLiteDatabase db;

    private MailDB(Context context) {
        MailOpenHelper mailOpenHelper = new MailOpenHelper(context,DB_NAME,null,VERSION);
        db = mailOpenHelper.getWritableDatabase();
    }

    public synchronized static MailDB getInstance (Context context) {
        if (mailDB == null) {
            mailDB = new MailDB(context);
        }
        return mailDB;
    }

    /*
     * 邮件信息
     */
    public void saveMailInformation(MailInformation mail) {
        if (mail != null) {
            ContentValues values = new ContentValues();
            values.put("mail_user",mail.getMailUser());
            values.put("subject",mail.getSubject());
            values.put("mail_from",mail.getFrom());
            values.put("receive_address",mail.getReceiveAddress());
            values.put("sent_date",mail.getSentDate());
            values.put("priority",mail.getPriority());
            values.put("seen",booleanToInt(mail.getSeen()));
            values.put("reply",booleanToInt(mail.getReply()));
            values.put("is_contain_attachment",booleanToInt(mail.getIsContainAttachment()));
            values.put("size",mail.getSize());
            values.put("content",mail.getContent());
            values.put("html","html");
            db.insert("MailInformation",null,values);
        }
    }

    public List<MailInformation> loadMailInformation(String user) {
        List<MailInformation> list = new ArrayList<>();
        Cursor cursor = db.query("MailInformation",null,"mail_user = ?",new String[] {user},null,null,"id DESC");
        while (cursor.moveToNext()) {
            MailInformation mailInformation = new MailInformation();
            mailInformation.setMailUser(cursor.getString(cursor.getColumnIndex("mail_user")));
            mailInformation.setSubject(cursor.getString(cursor.getColumnIndex("subject")));
            mailInformation.setFrom(cursor.getString(cursor.getColumnIndex("mail_from")));
            mailInformation.setReceiveAddress(cursor.getString(cursor.getColumnIndex("receive_address")));
            mailInformation.setSentDate(cursor.getString(cursor.getColumnIndex("sent_date")));
            mailInformation.setPriority(cursor.getString(cursor.getColumnIndex("priority")));
            mailInformation.setSeen(intTOBoolean(cursor.getInt(cursor.getColumnIndex("seen"))));
            mailInformation.setReply(intTOBoolean(cursor.getInt(cursor.getColumnIndex("reply"))));
            mailInformation.setIsContainAttachment(intTOBoolean(cursor.getInt(cursor.getColumnIndex("is_contain_attachment"))));
            mailInformation.setSize(cursor.getInt(cursor.getColumnIndex("size")));
            mailInformation.setContent(cursor.getString(cursor.getColumnIndex("content")));
            mailInformation.setHTML(cursor.getString(cursor.getColumnIndex("html")));
            list.add(mailInformation);
        }
        return list;
    }

    private static int booleanToInt(boolean b) {
        if (b == true) {
            return 1;
        } else {
            return 0;
        }
    }

    private static boolean intTOBoolean(int i) {
        if (i == 1){
            return true;
        } else {
            return false;
        }
    }

    /*
     * 邮箱
     */
    public boolean isEmpty() {
        Cursor cursor = db.query("User",null,null,null,null,null,null);
        if (cursor.moveToFirst()) {
            return false;
        }
        return true;
    }

    public int getNumUser() {
        Cursor cursor = db.query("User",null,null,null,null,null,null);
        int num = 0;
        while (cursor.moveToNext()) {
            num++;
        }
        return num;
    }

    public boolean saveUser(String user,String password) {
        Cursor cursor = db.query("User",null,"user = ?",new String[] {user},null,null,null);
        if (!cursor.moveToFirst()) {
            ContentValues values = new ContentValues();
            values.put("user",user);
            values.put("password",password);
            db.insert("User",null,values);
            return true;
        }
        return false;
    }

    public User loadUser(String user) {
        Log.d(TAG,user);
        Cursor cursor = db.query("User",null,"user = ?",new String[] {user},null,null,null);
        if (cursor.moveToFirst()) {
            User u = new User();
            u.setUser(cursor.getString(cursor.getColumnIndex("user")));
            u.setPassword(cursor.getString(cursor.getColumnIndex("password")));
            u.setNumMessage(cursor.getInt(cursor.getColumnIndex("num_message")));
            u.setNumNewMessage(cursor.getInt(cursor.getColumnIndex("num_new_message")));
            u.setNumUnreadMessage(cursor.getInt(cursor.getColumnIndex("num_unread_message")));
            Log.d(TAG,u.getPassword());
            return u;
        }
        return null;
    }

    public List<String> loadAllUser() {
        List<String> list = new ArrayList<>();
        Cursor cursor = db.query("User",null,null,null,null,null,null);
        while (cursor.moveToNext()) {
            list.add(cursor.getString(cursor.getColumnIndex("user")));
        }
        return list;
    }

    public void updateUser(User user) {
        ContentValues values = new ContentValues();
        values.put("user",user.getUser());
        values.put("password",user.getPassword());
        values.put("num_new_message",user.getNumNewMessage());
        values.put("num_unread_message",user.getNumUnreadMessage());
        values.put("num_message",user.getNumMessage());
        db.update("User",values,"user = ?",new String[] {user.getUser()});
    }

    //搜索
    public List<MailInformation> searchMailFrom(String user,String request) {
        List<MailInformation> list = new ArrayList<>();
        Cursor cursor = db.query("MailInformation",null,"mail_user = ?",new String[] {user},null,null,"id DESC");
        return list;
    }
}
