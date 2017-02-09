package com.projects.sweproject.boggle;

import android.provider.BaseColumns;

/**
 * Created by rPhilip on 1/31/17.
 */

public final class WordReaderContract {
    private WordReaderContract() {}

    public static class WordEntry implements BaseColumns {
        public static final String TABLE_NAME = "wordtable";
        public static final String COLUMN_NAME = "word";

    }



}
