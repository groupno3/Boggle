package com.projects.sweproject.boggle;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by rPhilip on 2/28/17.
 */

public class HighScoreDBHelper extends SQLiteOpenHelper{

    private Context context;
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "highscore.db";

    public HighScoreDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + HighScoreReaderContract.HighScoreEntry.TABLE_NAME + " (" +
                    HighScoreReaderContract.HighScoreEntry._ID + " INTEGER PRIMARY KEY," +
                    HighScoreReaderContract.HighScoreEntry.COLUMN_NAME_PLAYER + " TEXT," +
                    HighScoreReaderContract.HighScoreEntry.COLUMN_NAME_SCORE + " INTEGER," +
                    HighScoreReaderContract.HighScoreEntry.COLUMN_NAME_LEVEL + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + HighScoreReaderContract.HighScoreEntry.TABLE_NAME;

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
        db.execSQL("CREATE INDEX scoreIndex ON " + HighScoreReaderContract.HighScoreEntry.TABLE_NAME
                + " (score);");
        Log.d("onCreate", "onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public boolean isHighScore(int candidateScore, String level) {
        //this method determines whether the player's score is high enough
        //to be in the top 5 scores listed
        ArrayList<Integer> scores = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                HighScoreReaderContract.HighScoreEntry.TABLE_NAME,
                new String[] {"score"},
                "level=?",
                new String[] {level},
                null,
                null,
                null,
                null);


        while (cursor.moveToNext()) {
            int score = cursor.getInt(
                    cursor.getColumnIndexOrThrow(HighScoreReaderContract.HighScoreEntry.COLUMN_NAME_SCORE));
            scores.add(score);
        }

        Collections.sort(scores);
        Collections.reverse(scores);

        if (scores.size() < 5) {
            return true;
        } else if (scores.get(4) < candidateScore) {
            return true;
        } else {
            return false;
        }
    }

//    FeedEntry.TABLE_NAME,                     // The table to query
//    projection,                               // The columns to return
//    selection,                                // The columns for the WHERE clause
//    selectionArgs,                            // The values for the WHERE clause
//    null,                                     // don't group the rows
//    null,                                     // don't filter by row groups
//    sortOrder                                 // The sort order
}
