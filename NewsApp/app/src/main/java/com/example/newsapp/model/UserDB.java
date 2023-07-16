package com.example.newsapp.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDB extends SQLiteOpenHelper {
    private static final String DBNAME = "AUASS_USERS.db";

    public UserDB(Context context){
        super(context, DBNAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        DB.execSQL("create Table users(email TEXT, password TEXT primary key, isAdmin INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase DB, int i, int i1) {
        DB.execSQL("drop Table if exists users");
        onCreate(DB);
    }

    public boolean insertUser(User user){
        SQLiteDatabase myDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", user.getEmail());
        contentValues.put("password", user.getPassword());
        contentValues.put("isAdmin", user.isAdmin());
        long result = myDB.insert("users", null, contentValues);
        return result!=-1;
    }

    public Boolean validateLoginDetails(String email, String password){
        SQLiteDatabase myDB = this.getWritableDatabase();
        Cursor cursor = myDB.rawQuery("SELECT * FROM users where email = ? AND password = ?", new String[]{email, password});
        return cursor.getCount()>0;
    }
}
