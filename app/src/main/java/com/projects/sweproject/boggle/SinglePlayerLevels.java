/**
 * Created by emenpy on 1/30/17.
 */

package com.projects.sweproject.boggle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class SinglePlayerLevels extends Activity {

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

        //newIntent(Context packageContext, String PlayerType, String gameMode, String gameLevel)
        easy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Mode = "SinglePlayer";
                Level = "Easy";
                Intent in = ShakeActivity.newIntent(SinglePlayerLevels.this, null, Mode, Level);
                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(in);
            }
        });

        medium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Mode = "SinglePlayer";
                Level = "Medium";
                Intent in = ShakeActivity.newIntent(SinglePlayerLevels.this, null, Mode, Level);
                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(in);
            }
        });

        hard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Mode = "SinglePlayer";
                Level = "Hard";
                Intent in = ShakeActivity.newIntent(SinglePlayerLevels.this, null, Mode, Level);
                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(in);
            }
        });
    }
}
