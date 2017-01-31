package com.projects.sweproject.boggle;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;
import android.util.*;

import java.util.concurrent.TimeUnit;

/**
 * Created by emenpy on 1/30/17.
 */
public class SinglePlayer extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.single_player);

        final TextView a = (TextView) findViewById(R.id.timer);


        new CountDownTimer(180000, 1000) {

            public void onTick(long millisUntilFinished) {




                a.setText("Time remaining: " + ((millisUntilFinished/1000)/60)  + " minute and "+ ((millisUntilFinished/1000)%60) + " seconds");
            }

            public void onFinish() {
                a.setText("done!");
            }
        }.start();


    }
}
