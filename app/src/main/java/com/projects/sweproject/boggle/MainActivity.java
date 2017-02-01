package com.projects.sweproject.boggle;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity {

    private Button singleButton;
    private Button multiButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        singleButton = (Button) findViewById(R.id.button);
        singleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent singlePlayerScreen = new Intent(MainActivity.this, SinglePlayer.class);
                startActivity(singlePlayerScreen);
            }
        });

        multiButton = (Button) findViewById(R.id.button2);
        multiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent multiPlayerScreen = new Intent(MainActivity.this, MultiPlayer.class);
                startActivity(multiPlayerScreen);
            }
        });

        WordDBHelper dbHelper = new WordDBHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(WordReaderContract.WordEntry.COLUMN_NAME, "word");

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(WordReaderContract.WordEntry.TABLE_NAME, null, values);

        //TODO: Figure out how to use the words.sql file to import the rest of the legit words.

        Log.d("Hello there!", "Hello there!");


        //Code to evaluate DB expression
//        String[] projection = {
//                WordReaderContract.WordEntry._ID,
//                WordReaderContract.WordEntry.COLUMN_NAME,
//        }
//
//        Cursor cursor = db.query(
//                WordReaderContract.WordEntry.TABLE_NAME,                     // The table to query
//                projection,                               // The columns to return
//                null,                                // The columns for the WHERE clause
//                null,                            // The values for the WHERE clause
//                null,                                     // don't group the rows
//                null,                                     // don't filter by row groups
//                null                                 // The sort order
//        );
//        List itemIds = new ArrayList<>();
//        while(cursor.moveToNext()) {
//            String itemId = cursor.getString(
//                    cursor.getColumnIndexOrThrow(WordReaderContract.WordEntry.COLUMN_NAME));
//            itemIds.add(itemId);
//        }
//        cursor.close();
//        itemIds.toArray();

    }
}
