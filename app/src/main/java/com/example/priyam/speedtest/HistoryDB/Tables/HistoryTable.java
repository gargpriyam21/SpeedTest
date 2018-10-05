package com.example.priyam.speedtest.HistoryDB.Tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.priyam.speedtest.HistoryDB.Models.HistoryData;

import java.util.ArrayList;

import static com.example.priyam.speedtest.HistoryDB.Tables.DBConstants.*;

public class HistoryTable {

    public static final String TABLE_NAME = "history";
    public static final String CMD_CREATE =
            CMD_CREATE_TABLE_INE + TABLE_NAME +
                    LBR +
                    Coloumns.ID + TYPE_INT + TYPE_PK + TYPE_AI + COMMA +
                    Coloumns.DOWNLOAD + TYPE_TEXT + COMMA +
                    Coloumns.UPLOAD + TYPE_TEXT + COMMA +
                    Coloumns.DATE + TYPE_TEXT + COMMA +
                    Coloumns.CONTYPE + TYPE_TEXT +
                    RBR +
                    SEMI;

    public static ArrayList<HistoryData> getHistory(SQLiteDatabase db) {
        ArrayList<HistoryData> history = new ArrayList<>();

        Cursor c = db.query(
                TABLE_NAME,
                new String[]{Coloumns.ID, Coloumns.DOWNLOAD, Coloumns.UPLOAD, Coloumns.DATE, Coloumns.CONTYPE},
                null,
                null,
                null,
                null,
                null
        );
        int colForID = c.getColumnIndex(Coloumns.ID);
        int colForDownload = c.getColumnIndex(Coloumns.DOWNLOAD);
        int colForUpload = c.getColumnIndex(Coloumns.UPLOAD);
        int colForDate = c.getColumnIndex(Coloumns.DATE);
        int colforconType = c.getColumnIndex(Coloumns.CONTYPE);
        while (c.moveToNext()) {
            history.add(
                    new HistoryData(
                            c.getInt(colForID),
                            c.getString(colForDownload),
                            c.getString(colForUpload),
                            c.getString(colForDate),
                            c.getString(colforconType)
                    )
            );
        }
        return history;
    }

    public static long insertHistory(SQLiteDatabase db, HistoryData history) {
        ContentValues historyData = new ContentValues();
        historyData.put(Coloumns.DOWNLOAD, history.getDownload());
        historyData.put(Coloumns.UPLOAD, history.getUpload());
        historyData.put(Coloumns.DATE, history.getDate());
        historyData.put(Coloumns.CONTYPE, history.getConType());
        return db.insert(
                TABLE_NAME,
                null,
                historyData
        );
    }

    public static void deleteHistory(SQLiteDatabase db, int historyID) {
        db.delete(
                TABLE_NAME,
                Coloumns.ID + "=?",
                new String[]{Integer.toString(historyID)}
        );
    }


    public interface Coloumns {
        String ID = "id";
        String DOWNLOAD = "download";
        String UPLOAD = "upload";
        String DATE = "date";
        String CONTYPE = "conType";
    }

}
