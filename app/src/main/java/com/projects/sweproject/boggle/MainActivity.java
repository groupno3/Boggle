package com.projects.sweproject.boggle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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








    }
}
