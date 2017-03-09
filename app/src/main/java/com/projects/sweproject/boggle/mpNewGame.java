package com.projects.sweproject.boggle;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class mpNewGame extends AppCompatActivity {

    int[][] matrix = {{R.id.Point00, R.id.Point01, R.id.Point02, R.id.Point03},
            {R.id.Point10, R.id.Point11, R.id.Point12, R.id.Point13},
            {R.id.Point20, R.id.Point21, R.id.Point22, R.id.Point23},
            {R.id.Point30, R.id.Point31, R.id.Point32, R.id.Point33}};

    Point[][] lMatrix;
    Point lastP;
    int[][] touchPath;
    int gridX,gridY;
    int viewHeight;
    int viewWidth;
    int offset;
    boolean player2TimerStarted = false;
    AlertDialog.Builder alertDialog;
    static boolean active = false;

    private TextView scoreView;

    int score = 0;
    int word_count = 0;
    int boardNum = 0;

    BoardCreator bc;
    String [][] board;
    String level;
    String Level;
    String PlayerType;
    String Mode;

    private LinearLayout main;
    private SquareTextView sq;
    private TextView wordIn;
    private TextView timer;
    private String letter, word,letter_path="";
    private Boolean isPlayer2In;
    private String AllWords ="";

    private ArrayList<String> selected_words;
    // The following are used for the shake detection


    private DatabaseReference mDatabaseReference;

    private WordDBHelper dbHelper;
    SQLiteDatabase db;

    HighScoreMultiPlayerDBHelper scoreMultiDBHelper;
    SQLiteDatabase scoreMultiDb;
    CutThroatMode CTM;

    private ProgressDialog mProgressDialog;

    Button SubmitButton;
    Button CancelButton;

    private int Player1ListCount =1;
    private int Player2ListCount =1;

    private int player1score;
    private int player2score;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sp_game_layout);

        //get selected level
        Bundle extras = getIntent().getExtras();
        Level = extras.getString("LEVEL");
        PlayerType = extras.getString("TYPE");
        Mode = extras.getString("MODE");
        this.level = Level;

        SubmitButton = (Button) findViewById(R.id.submit_button);
        CancelButton =  (Button) findViewById(R.id.cancel_button);

        scoreMultiDBHelper = new HighScoreMultiPlayerDBHelper(getApplicationContext());
        scoreMultiDb = scoreMultiDBHelper.getWritableDatabase();

        alertDialog = new AlertDialog.Builder(mpNewGame.this, R.style.MyAlertDialogStyle);
        alertDialog.setTitle("GAME OVER!");

        dbHelper = new WordDBHelper(getApplicationContext());
        db = dbHelper.getWritableDatabase();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        // ShakeDetector initialization

        scoreView = (TextView) findViewById(R.id.score_textView);
        scoreView.setText("Your Score: "+score);

        //init
        board = new String[4][4];
        lMatrix = new Point[4][4];
        lastP = null;
        touchPath = new int[4][4];
        viewHeight = 0;
        viewWidth = 0;
        wordIn = (TextView)findViewById(R.id.WordInput);
        timer = (TextView) findViewById(R.id.timer);

        //Touch grid
        main = (LinearLayout) findViewById(R.id.MainLayout);
        main.post(new Runnable() {
            @Override
            public void run() {
                SquareLayout box = (SquareLayout) findViewById(R.id.SquareLayout);
                gridX = (int)box.getX();

                sq = (SquareTextView) findViewById(matrix[0][0]);
                viewWidth = sq.getWidth();
                viewHeight = sq.getHeight();

                offset = (viewWidth * 2) / 6;

                int x;
                int y = 0;
                for(int i = 0; i < 4; ++i){
                    x = 0;
                    for(int j = 0; j < 4; ++j){
                        lMatrix[i][j] = new Point(x, y);
                        x = x + viewWidth;
                    }
                    y = y + viewHeight;
                }
            }
        });

        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            gridY =
                    TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }
        gridY += getStatusBarHeight();
        //Log.i("*** TAG :: ","gridY = "+ gridY);
        //start

        mDatabaseReference.child("MultiGames").child("CTMData").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                CTM = dataSnapshot.getValue(CutThroatMode.class);
                if(Player1ListCount < CTM.Player1WordList.size()) {
                    Toast.makeText(getApplicationContext(), "Player 1 selected : " + CTM.Player1WordList.get(CTM.Player1WordList.size() - 1).toUpperCase(), Toast.LENGTH_SHORT).show();
                    Player1ListCount = CTM.Player1WordList.size();
                }

                if(Player2ListCount < CTM.Player2WordList.size()) {
                    Toast.makeText(getApplicationContext(), "Player 2 selected : " + CTM.Player2WordList.get(CTM.Player2WordList.size() - 1).toUpperCase(), Toast.LENGTH_SHORT).show();
                    Player2ListCount = CTM.Player2WordList.size();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        startGame();
    }


    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void startGame(){
        //clear

        word = "";
        letter_path ="";
        wordIn.setText(word);
        selected_words = new ArrayList<String>();
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Waiting for host to join...");
        alertDialog.create();

        //gen board
        if(PlayerType.equals("HOST")) {
            Toast.makeText(getApplicationContext(), "Level: "+ Level + "Mode: "+ Mode, Toast.LENGTH_LONG).show();
//            bc = new BoardCreator(dbHelper, level);
//            String[] str = bc.getBoardLayout();
//            generateBoard(str);

            SubmitButton.setOnClickListener(clickOnHostSubmitButton);
            CancelButton.setOnClickListener(clickOnCancelButton);

            mDatabaseReference.child("MultiGames").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    MultiGameInfo MGI = dataSnapshot.getValue(MultiGameInfo.class);
                    bc = new BoardCreator(dbHelper, level);
                    String[] str = bc.getBoardLayout();
                    generateBoard(str);
                    MultiPlayerBoard mpb = new MultiPlayerBoard(str, bc.getAllWordsInString());
                    //mDatabaseReference.child("MultiGames").child("Boards").
                    MGI.Boards.add(0, mpb);
                    MGI.level = Level;
                    MGI.Mode=Mode;
                    MGI.BoardStarted=true;
                    mDatabaseReference.child("MultiGames").setValue(MGI);
                    startTimer();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else if(PlayerType.equals("JOIN")) {

            SubmitButton.setOnClickListener(clickOnJoinSubmitButton);
            CancelButton.setOnClickListener(clickOnCancelButton);

            //close keyboard from last window
            closeKeyBoard();
            isPlayer2In = false;
            mProgressDialog.show();

            Toast.makeText(getApplicationContext(), "MODE: "+ PlayerType, Toast.LENGTH_LONG).show();

            mDatabaseReference.child("MultiGames").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    MultiGameInfo MGI = dataSnapshot.getValue(MultiGameInfo.class);

                    if(MGI.BoardStarted){

                        String[] board = new String[MGI.Boards.get(0).BoardList.size()];
                        board = MGI.Boards.get(0).BoardList.toArray(board);
                        generateBoard(board);
                        Level = MGI.level;
                        AllWords = MGI.Boards.get(0).AllWords;
                        Mode = MGI.Mode;
                        mProgressDialog.dismiss();
                        if(!player2TimerStarted)
                        {
                            player2TimerStarted =true;
                            startTimer();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    // TODO: multi rounds
    // This is called when the player taps "submit Round"
    // You check if the player has submitted 5 words.
    // Then try to get the next board in the Boards array on FB.
    // if it doesn't exist you Gen a new one, if there is one you get it.
    // After the board is setup you add the players current time to the new two minutes.
    //
    private void submitRound(View view){
        // check words.
        if(selected_words.size()>=5) {
            mDatabaseReference.child("MultiGames").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    MultiGameInfo MGI = dataSnapshot.getValue(MultiGameInfo.class);
                    // check lose condition
                    // Get next board
                    boardNum++;
                    if(MGI.Boards.size()>=boardNum){
                        // Board exists so we get it from DB.
                        String[] board = new String[MGI.Boards.get(boardNum).BoardList.size()];
                        board = MGI.Boards.get(boardNum).BoardList.toArray(board);
                        generateBoard(board);
                        AllWords = MGI.Boards.get(boardNum).AllWords;
                    } else {
                        // There isn't a next board so we make one.
                        bc = new BoardCreator(dbHelper, level);
                        String[] str = bc.getBoardLayout();
                        AllWords = bc.getAllWordsInString();
                        generateBoard(str);
                        MultiPlayerBoard mpb = new MultiPlayerBoard(str, AllWords);
                        MGI.Boards.add(boardNum, mpb);

                        // Set new board
                        // TODO: Multi Round This is most likely wrong.
                        mDatabaseReference.child("MultiGames").child("Boards").setValue(MGI.Boards);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            // sync time + points
            // start.
            //startTimer(oldtime);
        } // else 'Not Enough words'
    }

    private void track(int x, int y) {
        int pointX;
        int pointY;
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                //wordIn.append("|"+lMatrix[i][j].x+","+lMatrix[i][j].y+"|");
                pointX = lMatrix[i][j].x + gridX;
                pointY = lMatrix[i][j].y + gridY;

                if (x > pointX + offset && x < pointX + viewWidth - offset) {
                    if (y > pointY + offset && y < pointY + viewHeight - offset) {
                        if (touchPath[i][j] == 0) {
                            if (lastP == null)
                                lastP = new Point(i,j);
                            //Log.d("*** Touch Grid ::: ","lp: "+lastP.x+" "+lastP.y+" i,j"+i+j);
                            if (lastP.x-1 <= i && i <= lastP.x+1 && lastP.y-1 <= j && j <= lastP.y+1) {
                                sq = (SquareTextView) findViewById(matrix[i][j]);
                                letter = sq.getText().toString();

                                //highlight
                                sq.setBackgroundColor(Color.RED);

                                word = word + letter;
                                letter_path = letter_path + i + j;
                                wordIn.setText(word);
                                //wordIn.setGravity(Gravity.LEFT);


                                //un highlight

                                touchPath[i][j] = 1;
                                lastP = new Point(i,j);
                            }
                        }
                    }
                }
            }
        }
    }

    private void touchRest(){
        // clear touchPath
        lastP = null;
        for(int i = 0; i < 4; ++i){
            for(int j = 0; j < 4; ++j){
                touchPath[i][j] = 0;
                sq = (SquareTextView) findViewById(matrix[i][j]);
                sq.setBackgroundColor(Color.WHITE);
            }
        }
        // clear word
        word = "";
        letter_path = "";
        wordIn.setText("");
        //wordIn.append(" ");
        //resetHighlight();
    }

    public boolean dispatchTouchEvent(MotionEvent event){
        int X = (int) event.getX();
        int Y = (int) event.getY();
        int EA = event.getAction();

        switch (EA){
            case MotionEvent.ACTION_DOWN:
                //Log.d("*** DispatchTouch :: ","Action Down X:"+X+" Y:"+Y);
                track(X, Y);
                break;
            case MotionEvent.ACTION_MOVE:
                //Log.d("*** DispatchTouch :: ","Action Move X:"+X+" Y:"+Y);
                track(X, Y);
                break;
        }

        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onResume() {
        super.onResume();
        active = true;
    }

    @Override
    public void onBackPressed() {
        // do something on back.
        super.onBackPressed();
        active = false;
    }

    public int calculateScore(String word){

        if (word.length() == 3 || word.length() == 4){
            score++;
        }

        else if (word.length() == 5 ){
            score = score + 2;
        }

        else if (word.length() == 6 ){
            score = score + 3;
        }

        else if (word.length() == 7 ){
            score = score + 5;
        }

        else if (word.length() >= 8 ){
            score = score + 10;
        }

        return score;
    }

    public static Intent newIntent(Context packageContext, String gameLevel, String playerType, String gameMode) {
        Intent i = new Intent( packageContext, mpNewGame.class);
        i.putExtra("LEVEL",gameLevel);
        i.putExtra("TYPE",playerType);
        i.putExtra("MODE",gameMode);
        return i;
    }

    public void generateBoard(String[] str){

        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                sq = (SquareTextView) findViewById(matrix[i][j]);
                touchPath[i][j] = 0;
                board[i][j] = str[i * 4 + j];
                sq.setText(board[i][j], TextView.BufferType.EDITABLE);
            }
        }
    }

    // TODO:: Multi Rounds timer
    /*  If the timer finishes in Multi round, then that player has lost. and Only needs to set the lost condition
                    in FireBase.
                    if mode == "rounds"
                    firebase -> set lose condition for that player to true (a bool)
                    if host
                        firebase set -> player1lose == true;
                    else if join
                        firebase set -> player2lose == true;
                    ...
                    display your score & losing message.

    public void startTimer(int time){

    }
    */

    public void startTimer(){

        //start timer
        // TODO: create motion lock
        new CountDownTimer(180000, 1000) {

            public void onTick(long millisUntilFinished) {

                timer.setText("Time left: " + ((millisUntilFinished/1000)/60)  + ":"+ ((String.format("%02d", (millisUntilFinished/1000)%60))));

            }

            public void onFinish() {

                //TODO: Multi Round Modify this method to support Multi-rounds, should not break other modes
                /*  If the timer finishes in Multi round, then that player has lost. and Only needs to set the lost condition
                    in FireBase.
                    if mode == "rounds"
                    firebase -> set lose condition for that player to true (a bool)
                    if host
                        firebase set -> player1lose == true;
                    else if join
                        firebase set -> player2lose == true;
                    ...
                    display your score & losing message.
                 */
                mDatabaseReference.child("MultiGames").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        MultiGameInfo MGI = dataSnapshot.getValue(MultiGameInfo.class);

                        player1score = MGI.Player1Score;
                        player2score = MGI.Player2Score;

                        }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                timer.setText("Time's up!");

                if(PlayerType.equals("HOST")) {
                    if(player1score>player2score){
                        //TODO: Minh, Display dialog to HOST that he won. Ex: Wohoo! You won! (with OK button which will close the button)
                        //Toast.makeText(getApplicationContext(), "Player 1 won!", Toast.LENGTH_SHORT).show();

                        alertDialog.setMessage("Congratulation! The host won!");
                        alertDialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });



                    }
                    if(player1score<player2score){
                        //TODO: Minh, Display dialog to HOST that he lost. Ex: Oh no! You lost! player 2 won. (with OK button which will close the button)
                        alertDialog.setMessage("Host lost! Better luck next time!");
                        alertDialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });



                    }
                    else{
                        //TODO: Minh, Display dialog to HOST that it was a tie. Ex: Wow! It's a tie!. (with OK button which will close the button)
                        alertDialog.setMessage("Wow! We have a tie");
                        alertDialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.dismiss();
                            }
                        });


                    }
                    //then do this
                    if (scoreMultiDBHelper.isHighScore(score, Level)) {
                        alertDialog.setTitle("Congratulations! Your score: " + score + " is in top 5");
                        final EditText highScoreName = new EditText(mpNewGame.this);
                        highScoreName.setHint("Please enter your name:");
                        alertDialog.setView(highScoreName);
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                        alertDialog.setNegativeButton("Submit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked OK button
                                //quit go back to Mainacitivyt
                                String name = highScoreName.getText().toString();
                                ContentValues vals = new ContentValues();
                                vals.put(HighScoreMultiPlayerReaderContract.HighScoreMultiEntry.COLUMN_NAME_PLAYER, name);
                                vals.put(HighScoreMultiPlayerReaderContract.HighScoreMultiEntry.COLUMN_NAME_SCORE, score);
                                vals.put(HighScoreMultiPlayerReaderContract.HighScoreMultiEntry.COLUMN_NAME_LEVEL, Level);
                                scoreMultiDb.insert(HighScoreMultiPlayerReaderContract.HighScoreMultiEntry.TABLE_NAME, null, vals);
                                closeKeyBoard();
                                Intent intent = new Intent(mpNewGame.this, MainActivity.class);
                                startActivity(intent);

                            }
                        });

                    }
                    alertDialog.setMessage("The valid words in this board are:\n\n" + bc.getAllWordsInString());
                }
                else if(PlayerType.equals("JOIN")) {

                    if(player2score>player1score){
                        //TODO: Minh, Display dialog to JOIN that he won. Ex: Wohoo! You won! (with OK button which will close the button)
                       // Toast.makeText(getApplicationContext(), "Player 2 won!", Toast.LENGTH_SHORT).show();
                        alertDialog.setMessage("Congratulation! Player 2 won!");
                        alertDialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

                    }
                    if(player2score<player1score){
                        //TODO: Minh, Display dialog to JOIN that he lost. Ex: Oh no! You lost! player 1 won. (with OK button which will close the button)

                        alertDialog.setMessage("Sorry, You lost! Player 1 won. Better luck next time!");
                        alertDialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });


                    }
                    else{
                        //TODO: Minh, Display dialog to HOST that it was a tie. Ex: Wow! It's a tie!. (with OK button which will close the button)
                        alertDialog.setMessage("Wow! We have a tie!");
                        alertDialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });


                    }

                    if (scoreMultiDBHelper.isHighScore(score, Level)) {
                        alertDialog.setTitle("Congratulations! Your score: " + score + " is in top 5");
                        final EditText highScoreName = new EditText(mpNewGame.this);
                        highScoreName.setHint("Please enter your name:");
                        alertDialog.setView(highScoreName);
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                        alertDialog.setNegativeButton("Submit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked OK button
                                //quit go back to Mainacitivyt
                                String name = highScoreName.getText().toString();
                                ContentValues vals = new ContentValues();
                                vals.put(HighScoreMultiPlayerReaderContract.HighScoreMultiEntry.COLUMN_NAME_PLAYER, name);
                                vals.put(HighScoreMultiPlayerReaderContract.HighScoreMultiEntry.COLUMN_NAME_SCORE, score);
                                vals.put(HighScoreMultiPlayerReaderContract.HighScoreMultiEntry.COLUMN_NAME_LEVEL, Level);
                                scoreMultiDb.insert(HighScoreMultiPlayerReaderContract.HighScoreMultiEntry.TABLE_NAME, null, vals);
                                closeKeyBoard();
                                Intent intent = new Intent(mpNewGame.this, MainActivity.class);
                                startActivity(intent);

                            }
                        });
                    }
                    alertDialog.setMessage("The valid words in this board are:\n\n" + AllWords);
                }
                alertDialog.setPositiveButton("BACK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        //quit go back to Mainacitivyt
                        Intent intent = new Intent(mpNewGame.this, SinglePlayerLevels.class);
                        startActivity(intent);

                    }
                });
                if(active)
                    alertDialog.show();
                SubmitButton.setEnabled(false);
                CancelButton.setEnabled(false);
            }
        }.start();

    }

    public void closeKeyBoard(){

        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

    }

    View.OnClickListener clickOnHostSubmitButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            final String input = wordIn.getText().toString();

            if(input.length()<3){
                Toast.makeText(getApplicationContext(), "Word should be longer than 2 letters!", Toast.LENGTH_SHORT).show();
            }
            else {
                boolean isValidWord = dbHelper.getWord(input);
                if (isValidWord == true) {

                    //Cut-throat mode for HOST starts here

                    if(Mode.equals("CUTTHROAT")){

                        if (!selected_words.contains(letter_path)) {
                            if (!CTM.Player2TraceList.contains(letter_path)){
                                CTM.Player1TraceList.add(letter_path);
                                CTM.Player1WordList.add(input);
                                mDatabaseReference.child("MultiGames").child("CTMData").setValue(CTM);
                                Player1ListCount++;
                                Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_SHORT).show();
                                selected_words.add(letter_path);
                                word_count++;
                                scoreView.setText("Your Score: " + calculateScore(input));

                            }
                            else
                                Toast.makeText(getApplicationContext(), "Player 2 has already selected this word!", Toast.LENGTH_SHORT).show();
                        }
                        else
                            Toast.makeText(getApplicationContext(), "you have already selected this word!", Toast.LENGTH_SHORT).show();
                    }
                    //Cut-throat mode for HOST ends here
                    //Basic mode for HOST starts here
                    else {
                        if (selected_words.contains(letter_path) == false) {

                            Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_SHORT).show();
                            selected_words.add(letter_path);
                            word_count++;
                            scoreView.setText("Your Score: " + calculateScore(input));
                        } else {
                            Toast.makeText(getApplicationContext(), "you have already selected this word!", Toast.LENGTH_SHORT).show();
                        }

                    }
                    //Basic mode for HOST ends here



                } else
                    Toast.makeText(getApplicationContext(), "Wrong!", Toast.LENGTH_SHORT).show();
            }
            touchRest();
            mDatabaseReference.child("MultiGames").child("Player1Score").setValue(score);


        }
    };

    View.OnClickListener clickOnJoinSubmitButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            final String input = wordIn.getText().toString();

            if(input.length()<3){
                Toast.makeText(getApplicationContext(), "Word should be longer than 2 letters!", Toast.LENGTH_SHORT).show();
            }
            else {
                boolean isValidWord = dbHelper.getWord(input);
                if (isValidWord == true) {

                    //Cut-throat mode for JOIN starts here

                    if(Mode.equals("CUTTHROAT")){

                        if (!selected_words.contains(letter_path)) {
                            if (!CTM.Player1TraceList.contains(letter_path)){
                                CTM.Player2TraceList.add(letter_path);
                                CTM.Player2WordList.add(input);
                                mDatabaseReference.child("MultiGames").child("CTMData").setValue(CTM);
                                Player2ListCount++;
                                Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_SHORT).show();
                                selected_words.add(letter_path);
                                word_count++;
                                scoreView.setText("Your Score: " + calculateScore(input));

                            }
                            else
                                Toast.makeText(getApplicationContext(), "Player 1 has already selected this word!", Toast.LENGTH_SHORT).show();
                        }
                        else
                            Toast.makeText(getApplicationContext(), "you have already selected this word!", Toast.LENGTH_SHORT).show();
                    }

                    //Cut-throat mode for JOIN ends here
                    //Basic mode for JOIN starts here
                    else {
                        if (selected_words.contains(letter_path) == false) {

                            Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_SHORT).show();
                            selected_words.add(letter_path);
                            word_count++;
                            scoreView.setText("Your Score: " + calculateScore(input));
                        } else {
                            Toast.makeText(getApplicationContext(), "you have already selected this word!", Toast.LENGTH_SHORT).show();
                        }

                    }

                    //Basic mode for JOIN ends here

                } else
                    Toast.makeText(getApplicationContext(), "Wrong!", Toast.LENGTH_SHORT).show();
            }
            touchRest();
            mDatabaseReference.child("MultiGames").child("Player2Score").setValue(score);


        }
    };


    View.OnClickListener clickOnCancelButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            touchRest();
        }
    };

}