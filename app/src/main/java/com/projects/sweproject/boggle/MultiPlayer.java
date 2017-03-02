package com.projects.sweproject.boggle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by emenpy on 1/30/17.
 */
public class MultiPlayer extends Activity {

    ImageButton host;
    ImageButton join;
    String PlayerType;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.multi_player);
        host = (ImageButton)findViewById(R.id.hostbtn);
        host.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                PlayerType = "HOST";
                Intent in = MultiPlayerLevels.newIntent(getApplicationContext(), PlayerType);
                startActivity(in);
            }
        });

        join = (ImageButton)findViewById(R.id.joinbtn);
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(getApplicationContext(),JoinActivity.class);
                startActivity(in);

            }
        });




    }


}
