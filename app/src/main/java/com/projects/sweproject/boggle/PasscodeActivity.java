package com.projects.sweproject.boggle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PasscodeActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseReference;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcode);
        TextView t1;
        String passcode;

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        MultiGameInfo newGame = new MultiGameInfo();
        passcode = newGame.PassCode;


        mDatabaseReference.child("MultiGames").setValue(newGame);
        t1 = (TextView) findViewById(R.id.textView2);
        t1.setText("Game Passcode: \n" +passcode +"\n\n Waiting for player 2... ");

        mDatabaseReference.child("Board").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                MultiGameInfo MGI = dataSnapshot.getValue(MultiGameInfo.class);

                if(MGI.Player2Joined){
                    //TODO: Add message to inform player 2 joined
                    Toast.makeText(getApplicationContext(),"Player 2 joined",Toast.LENGTH_SHORT).show();
                    Intent in = new Intent(getApplicationContext(), MultiPlayerModes.class);
                    startActivity(in);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }

}
