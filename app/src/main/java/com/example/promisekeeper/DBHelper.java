package com.example.promisekeeper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{

    private final String[] dbCreates;

    DBHelper(Context context, String[] dbCreates) {
        super(context, "myDB", null, 1);
        this.dbCreates = dbCreates;
    }

    public void onCreate(SQLiteDatabase db) {
        for (String dbCreate : dbCreates){
            db.execSQL(dbCreate); }
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}