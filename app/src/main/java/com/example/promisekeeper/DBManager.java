package com.example.promisekeeper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {

    private final DBHelper dbHelper;
    private final SQLiteDatabase sqldb;

    private final String DB_CREATE_TABLE1 =
            "create table if not exists " +
                    Root.getString("TableName", "defTable") + "(" +
                    Root.getStringRes(R.string.COLUMN_ID) + " integer primary key autoincrement, " +
                    Root.getStringRes(R.string.COLUMN_TITLE) + " text, " +
                    Root.getStringRes(R.string.COLUMN_DESCRIPTION) + " text, " +
                    Root.getStringRes(R.string.COLUMN_COUNTER) + " integer, " +
                    Root.getStringRes(R.string.COLUMN_START_DATE) + " text, " +
                    Root.getStringRes(R.string.COLUMN_END_DATE) + " text, " +
                    Root.getStringRes(R.string.COLUMN_DONE) + " text " +
                    ");";
String tablename = "меню";
    private final String DB_CREATE_TABLE2 =
            "create table if not exists " +
                    tablename + "(" +
                    Root.getStringRes(R.string.COLUMN_ID) + " integer primary key autoincrement, " +
                    Root.getStringRes(R.string.COLUMN_TITLE) + " text " +
                    ");";

    public DBManager (Context context) {
        String[] myTables = {DB_CREATE_TABLE1, DB_CREATE_TABLE2};
        dbHelper = new DBHelper(context, myTables);
        sqldb = dbHelper.getWritableDatabase();
    }

    public void createUserTable(String user) {
        String CREATE_NEW_USER_TABLE = "create table if not exists " +
                user + "(" +
                Root.getStringRes(R.string.COLUMN_ID) + " integer primary key autoincrement, " +
                Root.getStringRes(R.string.COLUMN_TITLE) + " text, " +
                Root.getStringRes(R.string.COLUMN_DESCRIPTION) + " text, " +
                Root.getStringRes(R.string.COLUMN_COUNTER) + " integer, " +
                Root.getStringRes(R.string.COLUMN_START_DATE) + " text, " +
                Root.getStringRes(R.string.COLUMN_END_DATE) + " text, " +
                Root.getStringRes(R.string.COLUMN_DONE) + " text) " ;
        sqldb.execSQL(CREATE_NEW_USER_TABLE);
    }

    public void setData(String title, String promise, String start_date) {
        ContentValues cv = new ContentValues();
        cv.put(Root.getStringRes(R.string.COLUMN_TITLE), title);
        cv.put(Root.getStringRes(R.string.COLUMN_DESCRIPTION), promise);
        cv.put(Root.getStringRes(R.string.COLUMN_COUNTER), 1);
        cv.put(Root.getStringRes(R.string.COLUMN_START_DATE), start_date);
        cv.put(Root.getStringRes(R.string.COLUMN_END_DATE), Root.getStringRes(R.string.STRING_UNKNOWN));
        cv.put(Root.getStringRes(R.string.COLUMN_DONE), Root.getStringRes(R.string.STRING_PROCESSING));
        sqldb.insert(Root.getString("TableName", "defTable"), null, cv);
    }

    public void setMenuData(String title) {
        ContentValues cv = new ContentValues();
        cv.put(Root.getStringRes(R.string.COLUMN_TITLE), title);
        sqldb.insert("меню", null, cv);
    }

    public Cursor getAllData(int tableName) {
        return sqldb.query(Root.getString("TableName", "defTable"), null, null, null, null, null, null);
    }

    public Cursor getDataByParams(String [] columns) {
        Cursor c = sqldb.query(Root.getString("TableName", "defTable"), columns, null, null, null, null, null);
        if (c != null) c.moveToFirst();
        return c;
    }
    public Cursor getMenuData(String [] columns) {
        Cursor c = sqldb.query("меню", columns, null, null, null, null, null);
        if (c != null) c.moveToFirst();
        return c;
    }
    public Cursor getDataByParams(String tableName, String [] columns) {
        Cursor c = sqldb.query(Root.getString("TableName", "defTable"), columns, null, null, null, null, null);
        if (c != null) c.moveToFirst();
        return c;
    }

    public void deleteData(String title){
        sqldb.delete(Root.getString("TableName", "defTable"), "title = '"+title+"'", null);
        int x = 0;
    }

    public void deleteData(String tablename, String title){
        sqldb.delete(tablename, "title = '"+title+"'", null);
        int x = 0;
    }

    public void updateData (String updateColumn, String whereTitle, String new_data) {
        ContentValues cv = new ContentValues();
        cv.put(updateColumn, new_data);
        sqldb.update(Root.getString("TableName", "defTable"),cv, "title = '"+whereTitle+"'", null );
        cv.clear();
    }

    public void updateCounter (String whereTitle, int new_data) {
        ContentValues cv = new ContentValues();
        cv.put(Root.getStringRes(R.string.COLUMN_COUNTER), new_data);
        sqldb.update(Root.getString("TableName", "defTable"),cv, "title = '"+whereTitle+"'", null );
        cv.clear();
    }

    public Cursor findEntry(String tablename, String [] columns, String title) {
        Cursor c = sqldb.query(tablename, columns, "title=?", new String[] { title }, null, null, null);
        if (c != null) c.moveToFirst();
        return c;
    }

    public void deleteTable(String tablename) {
        sqldb.delete(tablename, null, null);
    }
}