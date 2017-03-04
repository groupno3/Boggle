package com.projects.sweproject.boggle;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by rPhilip on 3/2/17.
 */

public class HighScoreMultiPlayerDBHelper extends SQLiteOpenHelper{
    private Context context;
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "highscoreMulti.db";

    public HighScoreMultiPlayerDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + HighScoreMultiPlayerReaderContract.HighScoreMultiEntry.TABLE_NAME + " (" +
                    HighScoreMultiPlayerReaderContract.HighScoreMultiEntry._ID + " INTEGER PRIMARY KEY," +
                    HighScoreMultiPlayerReaderContract.HighScoreMultiEntry.COLUMN_NAME_PLAYER + " TEXT," +
                    HighScoreMultiPlayerReaderContract.HighScoreMultiEntry.COLUMN_NAME_SCORE + " INTEGER," +
                    HighScoreMultiPlayerReaderContract.HighScoreMultiEntry.COLUMN_NAME_LEVEL + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + HighScoreMultiPlayerReaderContract.HighScoreMultiEntry.TABLE_NAME;

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
        db.execSQL("CREATE INDEX scoreIndex ON " + HighScoreMultiPlayerReaderContract.HighScoreMultiEntry.TABLE_NAME
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
                HighScoreMultiPlayerReaderContract.HighScoreMultiEntry.TABLE_NAME,
                new String[] {"score"},
                "level=?",
                new String[] {level},
                null,
                null,
                null,
                null);


        while (cursor.moveToNext()) {
            int score = cursor.getInt(
                    cursor.getColumnIndexOrThrow(HighScoreMultiPlayerReaderContract.HighScoreMultiEntry.COLUMN_NAME_SCORE));
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

    public HighScoreKeeper getTopFiveScoreboard() {
        HighScoreKeeper keeper = new HighScoreKeeper();
        getTopFiveByLevel("Easy", keeper.easyNames, keeper.easyScores);
        getTopFiveByLevel("Medium", keeper.mediumNames, keeper.mediumScores);
        getTopFiveByLevel("Hard", keeper.hardNames, keeper.hardScores);

        return keeper;
    }

    private void getTopFiveByLevel(String level, ArrayList<String> names, ArrayList<String> scores) {
        SQLiteDatabase db = this.getReadableDatabase();
//        private Cursor queryLastEvents() {
//            return getDatabase().query("table_name", null, null, null, null, null, "row_id DESC", "10");
//        }

        Cursor cursor = db.query(
                HighScoreMultiPlayerReaderContract.HighScoreMultiEntry.TABLE_NAME,
                new String[] {HighScoreMultiPlayerReaderContract.HighScoreMultiEntry.COLUMN_NAME_PLAYER,
                        HighScoreMultiPlayerReaderContract.HighScoreMultiEntry.COLUMN_NAME_SCORE},
                "level=?",
                new String[] {level},
                null,
                null,
                HighScoreMultiPlayerReaderContract.HighScoreMultiEntry.COLUMN_NAME_SCORE,
                "5"
        );
        while (cursor.moveToNext()) {
            int score = cursor.getInt(
                    cursor.getColumnIndexOrThrow(HighScoreMultiPlayerReaderContract.HighScoreMultiEntry.COLUMN_NAME_SCORE));
            String name = cursor.getString(
                    cursor.getColumnIndexOrThrow(HighScoreMultiPlayerReaderContract.HighScoreMultiEntry.COLUMN_NAME_PLAYER));
            names.add(name);
            scores.add(Integer.toString(score));
        }
    }
}
