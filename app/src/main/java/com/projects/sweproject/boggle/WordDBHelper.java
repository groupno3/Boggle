package com.projects.sweproject.boggle;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by rPhilip on 1/31/17.
 */

public class WordDBHelper extends SQLiteOpenHelper {
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + WordReaderContract.WordEntry.TABLE_NAME + " (" +
                    WordReaderContract.WordEntry._ID + " INTEGER PRIMARY KEY," +
                    WordReaderContract.WordEntry.COLUMN_NAME + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + WordReaderContract.WordEntry.TABLE_NAME;

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "wordtable.db";
    private Context context;

    public WordDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
        InputStream inputStream = context.getResources().openRawResource(R.raw.words);
        List<String> queries = null;
        try {
            queries = IOUtils.readLines(inputStream, Charset.defaultCharset());
        } catch (IOException e) {
            // blargh
            System.err.println(e);
        }

        for (String query : queries) {
            db.execSQL(query);
        }

        Log.d("onCreate", "onCreate");

    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public boolean getWord(String userInput){

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(WordReaderContract.WordEntry.TABLE_NAME, new String[] {"word"}, "word=?", new String[] {userInput}, null, null, null, null);

        if (cursor.getCount() > 0){
            cursor.close();
            return true;

        }
        else{
            cursor.close();
            return false;
        }
    }



}
