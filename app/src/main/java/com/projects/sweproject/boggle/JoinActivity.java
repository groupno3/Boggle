package com.projects.sweproject.boggle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by emenpy on 2/28/17.
 */

public class JoinActivity extends Activity {

    String PlayerType;
    private DatabaseReference mDatabaseReference;
    ImageButton submit;

    private Boolean isGameOn = false;
    private Boolean isPlayer2In = false;
    private String PassCode = "";
    private EditText inputPassCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_screen);


        submit = (ImageButton) findViewById(R.id.submitbttn);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();



        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        inputPassCode = (EditText) findViewById(R.id.pass);

        mDatabaseReference.child("MultiGames").addValueEventListener(VE);

        //Toast.makeText(getApplicationContext(), "Passcode: " + PassCode, Toast.LENGTH_SHORT).show();
        //newIntent(Context packageContext, String gameLevel, String playerType)

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(isPlayer2In == false) {
                        if (PassCode.equals(inputPassCode.getText().toString())) {
                            mDatabaseReference.removeEventListener(VE);
                            PlayerType = "JOIN";
                            //mDatabaseReference.child("Board").child("BoardStarted").setValue(true);
                            mDatabaseReference.child("MultiGames").child("Player2Joined").setValue(true);
                            Intent in = mpNewGame.newIntent(getApplicationContext(), null, PlayerType,null);
                            startActivity(in);
                        }
                        else
                            Toast.makeText(getApplicationContext(), "You have entered wrong pass code!", Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(getApplicationContext(), "Sorry! you cannot join the game at this time.", Toast.LENGTH_SHORT).show();


            }
        });
    }

    ValueEventListener VE = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            MultiGameInfo MGI = dataSnapshot.getValue(MultiGameInfo.class);
            isGameOn= MGI.BoardStarted;
            PassCode=MGI.PassCode;
            isPlayer2In=MGI.Player2Joined;

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };


}


