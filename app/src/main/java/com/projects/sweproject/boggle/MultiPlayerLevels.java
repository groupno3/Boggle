/**
 * Created by emenpy on 1/30/17.
 */

package com.projects.sweproject.boggle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MultiPlayerLevels extends Activity {

    ImageButton easy;
    ImageButton medium;
    ImageButton hard;
    String Level;
    String Mode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_player);

        // Temp start board class.
        easy = (ImageButton) findViewById(R.id.button1);
        medium = (ImageButton) findViewById(R.id.button2);
        hard = (ImageButton) findViewById(R.id.button3);

        Bundle extras = getIntent().getExtras();
        final String PlayerType = extras.getString("TYPE");
        final String PlayerMode = extras.getString("MODE");

        //Toast.makeText(getApplicationContext(), "TYPE: "+ PlayerType, Toast.LENGTH_LONG).show();
        //newIntent(Context packageContext, String PlayerType, String gameMode, String gameLevel)

        easy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Mode = "MultiPlayerTypeActivity";
                Level = "Easy";
                Intent in = ShakeActivity.newIntent(MultiPlayerLevels.this, PlayerType, PlayerMode, Level);
                startActivity(in);
            }
        });

        medium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Mode = "MultiPlayerTypeActivity";
                Level = "Medium";
                Intent in = ShakeActivity.newIntent(MultiPlayerLevels.this, PlayerType, PlayerMode, Level);
                startActivity(in);
            }
        });

        hard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Mode = "MultiPlayerTypeActivity";
                Level = "Hard";
                Intent in = ShakeActivity.newIntent(MultiPlayerLevels.this, PlayerType, PlayerMode, Level);
                startActivity(in);
            }
        });
    }

    public static Intent newIntent(Context packageContext, String PlayerType, String GameMode) {
        Intent i = new Intent( packageContext, MultiPlayerLevels.class);
        i.putExtra("TYPE",PlayerType);
        i.putExtra("MODE",GameMode);
        return i;
    }
}

