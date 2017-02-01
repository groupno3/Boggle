/**
 * Created by emenpy on 1/30/17.
 */

package com.projects.sweproject.boggle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.util.*;

import java.util.concurrent.TimeUnit;

public class SinglePlayer extends Activity {

    Button easy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_player);

        /* needs to be moved to start after the board is created

        final TextView a = (TextView) findViewById(R.id.timer);

        new CountDownTimer(180000, 1000) {

            public void onTick(long millisUntilFinished) {
                a.setText("Time remaining: " + ((millisUntilFinished/1000)/60)  + " minute and "+ ((millisUntilFinished/1000)%60) + " seconds");
            }

            public void onFinish() {
                a.setText("done!");
            }
        }.start();
        */

        // Temp start board class.
        easy = (Button) findViewById(R.id.button1);
        easy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getApplicationContext(), spNewGame.class);
                startActivity(in);
            }
        });
    }
}
