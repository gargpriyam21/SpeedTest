package com.example.priyam.speedtest.HistoryDB;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.priyam.speedtest.HistoryDB.Tables.HistoryTable;

public class HistoryDBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "history.db";
    public static final int DB_VER = 1;

    public HistoryDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VER    );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(HistoryTable.CMD_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
