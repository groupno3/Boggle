package com.projects.sweproject.boggle;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;


public class MainActivity extends Activity {

    private ImageButton singleButton;
    private ImageButton multiButton;
    private ImageButton quit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        singleButton = (ImageButton) findViewById(R.id.button);
        singleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent singlePlayerScreen = new Intent(MainActivity.this, SinglePlayerLevels.class);
                startActivity(singlePlayerScreen);
            }
        });

        multiButton = (ImageButton) findViewById(R.id.button2);
        multiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent multiPlayerScreen = new Intent(MainActivity.this, MultiPlayer.class);
                startActivity(multiPlayerScreen);
            }
        });

        quit = (ImageButton)findViewById(R.id.button4);
        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                System.exit(0);
            }
        });





        WordDBHelper dbHelper = new WordDBHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();


        //Code to evaluate DB expression - we can inspect using this.
//        String[] projection = {
//                WordReaderContract.WordEntry._ID,
//                WordReaderContract.WordEntry.COLUMN_NAME,
//        };
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
//        for (int i = 0; i < 10; i++) {
//            cursor.moveToNext();
//            String itemId = cursor.getString(
//                    cursor.getColumnIndexOrThrow(WordReaderContract.WordEntry.COLUMN_NAME));
//            itemIds.add(itemId);
//
//        }
//        cursor.close();
//        itemIds.toArray();

    }
}
