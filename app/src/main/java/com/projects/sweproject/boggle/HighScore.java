package com.projects.sweproject.boggle;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by minphan on 3/1/17.
 */

public class HighScore extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.highscore);

        ImageButton single = (ImageButton) findViewById(R.id.singlebtn);
        single.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(HighScore.this, HighScoreSingle.class);
                startActivity(intent);


            }
        });


        ImageButton multi = (ImageButton) findViewById(R.id.multibtn);
        multi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HighScore.this, HighScoreMulti.class);
                startActivity(intent);
            }
        });








    }

}
