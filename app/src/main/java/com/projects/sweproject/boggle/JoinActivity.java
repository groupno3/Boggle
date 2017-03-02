package com.projects.sweproject.boggle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

/**
 * Created by emenpy on 2/28/17.
 */

public class JoinActivity extends Activity {

    String PlayerType;


    ImageButton submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.join_screen);


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


        submit = (ImageButton) findViewById(R.id.submitbttn);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PlayerType = "JOIN";
                Intent in = mpNewGame.newIntent(getApplicationContext(),null,PlayerType);
                startActivity(in);

            }
        });
    }


}


