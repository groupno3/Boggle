package com.projects.sweproject.boggle;

import android.provider.BaseColumns;

/**
 * Created by rPhilip on 3/2/17.
 */

public class HighScoreMultiPlayerReaderContract {
    private HighScoreMultiPlayerReaderContract() {}

    public static class HighScoreMultiEntry implements BaseColumns {
        public static final String TABLE_NAME = "highscore";
        public static final String COLUMN_NAME_PLAYER = "playername";
        public static final String COLUMN_NAME_SCORE = "score";
        public static final String COLUMN_NAME_LEVEL = "level";
    }
}
