/**
 * Created by emenpy on 1/30/17.
 */

package com.projects.sweproject.boggle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class SinglePlayer extends Activity {

    ImageButton easy;
    ImageButton medium;
    ImageButton hard;
    String Level;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_player);

        // Temp start board class.
        easy = (ImageButton) findViewById(R.id.button1);
        medium = (ImageButton) findViewById(R.id.button2);
        hard = (ImageButton) findViewById(R.id.button3);

        easy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Level = "Easy";
                Intent in = spNewGame.newIntent(SinglePlayer.this,Level);
                startActivity(in);
            }
        });

        medium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Level = "Medium";
                Intent in = spNewGame.newIntent(SinglePlayer.this,Level);
                startActivity(in);
            }
        });

        hard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Level = "Hard";
                Intent in = spNewGame.newIntent(SinglePlayer.this,Level);
                startActivity(in);
            }
        });
    }
}
