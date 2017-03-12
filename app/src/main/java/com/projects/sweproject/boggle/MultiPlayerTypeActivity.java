package com.projects.sweproject.boggle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by emenpy on 1/30/17.
 */
public class MultiPlayerTypeActivity extends Activity {

    private ImageButton host;
    private ImageButton join;
    private String PlayerType;
    private String Passcode;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);



        setContentView(R.layout.multi_player);
        host = (ImageButton)findViewById(R.id.hostbtn);
        host.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                PlayerType = "HOST";

                Intent in = new Intent(getApplicationContext(),PasscodeActivity.class);
                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(in);
            }
        });

        join = (ImageButton)findViewById(R.id.joinbtn);
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(getApplicationContext(),JoinActivity.class);
                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(in);

            }
        });




    }


}
