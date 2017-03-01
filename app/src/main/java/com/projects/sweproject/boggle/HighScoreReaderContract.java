package com.projects.sweproject.boggle;

import android.provider.BaseColumns;

/**
 * Created by rPhilip on 2/28/17.
 */

public class HighScoreReaderContract {
    private HighScoreReaderContract() {}

    public static class HighScoreEntry implements BaseColumns {
        public static final String TABLE_NAME = "highscore";
        public static final String COLUMN_NAME = "playername";


    }
}
