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
    AlertDialog.Builder alertDialog1;
    static boolean active = false;

    private TextView scoreView;

    int score = 0;
    int word_count = 0;
    int boardNum = 0;

    BoardCreator bc;
    String [][] board;
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
    private  String foundWords = "";

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

    private MultiGameInfo MGI;



    private Button SubmitRound;
    private long TimeLeft;

    CountDownTimer Timer;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mp_game_layout);

        //get selected level
        Bundle extras = getIntent().getExtras();
        Level = extras.getString("LEVEL");
        PlayerType = extras.getString("TYPE");
        Mode = extras.getString("MODE");

        SubmitButton = (Button) findViewById(R.id.submit_button);
        CancelButton =  (Button) findViewById(R.id.cancel_button);
        SubmitRound = (Button) findViewById(R.id.SubmitRound_button);

        MGI = new MultiGameInfo();

        scoreMultiDBHelper = new HighScoreMultiPlayerDBHelper(getApplicationContext());
        scoreMultiDb = scoreMultiDBHelper.getWritableDatabase();

        alertDialog = new AlertDialog.Builder(mpNewGame.this, R.style.MyAlertDialogStyle);
        alertDialog.setTitle("GAME OVER!");

        alertDialog1 = new AlertDialog.Builder(mpNewGame.this, R.style.MyAlertDialogStyle);


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

        mDatabaseReference.child("MultiGames").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                MGI = dataSnapshot.getValue(MultiGameInfo.class);
                player1score = MGI.Player1Score;
                player2score = MGI.Player2Score;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


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
        alertDialog1.create();

        SubmitRound.setOnClickListener(clickOnSubmitRound);

        //gen board
        if(PlayerType.equals("HOST")) {

            Toast.makeText(getApplicationContext(), "Level: "+ Level + "\nMode: "+ Mode, Toast.LENGTH_LONG).show();

            SubmitButton.setOnClickListener(clickOnHostSubmitButton);
            CancelButton.setOnClickListener(clickOnCancelButton);

            mDatabaseReference.child("MultiGames").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    MultiGameInfo MGI = dataSnapshot.getValue(MultiGameInfo.class);
                    bc = new BoardCreator(dbHelper, Level);
                    String[] str = bc.getBoardLayout();
                    generateBoard(str);
                    MultiPlayerBoard mpb = new MultiPlayerBoard(str, bc.getAllWordsInString());
                    MGI.Boards.add(0, mpb);
                    MGI.level = Level;
                    MGI.Mode=Mode;
                    MGI.BoardStarted=true;
                    mDatabaseReference.child("MultiGames").setValue(MGI);
                    startTimer(60000);
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

            Toast.makeText(getApplicationContext(), "MODE: " + PlayerType, Toast.LENGTH_LONG).show();

            mDatabaseReference.child("MultiGames").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    MultiGameInfo MGI = dataSnapshot.getValue(MultiGameInfo.class);

                    if (MGI.BoardStarted && isPlayer2In == false) {

                        String[] board = new String[MGI.Boards.get(0).BoardList.size()];
                        board = MGI.Boards.get(0).BoardList.toArray(board);
                        generateBoard(board);
                        Level = MGI.level;
                        AllWords = MGI.Boards.get(0).AllWords;
                        Mode = MGI.Mode;
                        mProgressDialog.dismiss();
                        isPlayer2In = true;
                        if (!player2TimerStarted) {
                            player2TimerStarted = true;
                            startTimer(60000);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
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
        active = false;
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        System.exit(0);
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


    public void startTimer(long time){

        if(!Mode.equals("ROUNDS"))
            SubmitRound.setVisibility(View.INVISIBLE);

        Timer = new CountDownTimer(time, 1000) {

            public void onTick(long millisUntilFinished) {

                timer.setText("Time left: " + ((millisUntilFinished/1000)/60)  + ":"+ ((String.format("%02d", (millisUntilFinished/1000)%60))));
                TimeLeft = millisUntilFinished;


                if (Mode.equals("ROUNDS")&&PlayerType.equals("HOST")&&MGI.IsPlayer2Lost==true) {
                    Timer.cancel();
                    timer.setText("You won!");
                    showDialog("HOST","You won!","Congratulations! You won!",true);
                }

                if (Mode.equals("ROUNDS")&&PlayerType.equals("JOIN")&&MGI.IsPlayer1Lost==true){
                    Timer.cancel();
                    timer.setText("You won!");
                    showDialog("JOIN","You won!","Congratulations! You won!",true);
                }
            }

            public void onFinish() {
                if (PlayerType.equals("HOST")) {
                    if (Mode.equals("ROUNDS")) {
                        mDatabaseReference.child("MultiGames").child("IsPlayer1Lost").setValue(true);
                        showDialog("HOST","You Lost!", "You lost! Better luck next time.", true);
                    }
                    else {
                        if (player1score > player2score)
                            showDialog("HOST","You won!","Congratulations! You won!",true);
                        else if (player1score < player2score)
                            showDialog("HOST","You Lost!", "You lost! Better luck next time.", true);
                        else
                            showDialog("HOST","It's a tie!", "No one won! It's a tie.", true);
                    }
                }
                if (PlayerType.equals("JOIN")) {
                    if (Mode.equals("ROUNDS")) {
                        mDatabaseReference.child("MultiGames").child("IsPlayer2Lost").setValue(true);
                        showDialog("JOIN", "You Lost!", "You lost! Better luck next time.", true);
                    }
                    else {
                        if (player2score > player1score)
                            showDialog("JOIN","You won!","Congratulations! You won!",true);
                        else if (player2score < player1score)
                            showDialog("JOIN","You Lost!", "You lost! Better luck next time.", true);
                        else
                            showDialog("JOIN","It's a tie!", "No one won! It's a tie.",true);

                    }
                }
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
                                foundWords+= input + " ";
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

                        if (Mode.equals("ROUNDS")){

                            if(MGI.IsPlayer2Lost==true){

                                Toast.makeText(getApplicationContext(), "You Win!", Toast.LENGTH_SHORT);
                            }
                        }

                        if (selected_words.contains(letter_path) == false) {

                            Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_SHORT).show();
                            selected_words.add(letter_path);
                            word_count++;
                            foundWords+= input + " ";
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


            //Toast.makeText(mpNewGame.this, "Type: " +PlayerType, Toast.LENGTH_SHORT).show();
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
                                foundWords+= input + " ";
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
                            foundWords+= input + " ";
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

    // TODO: Multi Rounds, Round Submit function.
    // This is called when the player taps "submit Round"
    // You check if the player has submitted 5 words.
    // Then try to get the next board in the Boards array on FB.
    // if it doesn't exist you Gen a new one, if there is one you get it.
    // After the board is setup you add the players current time to the new two minutes.

    View.OnClickListener clickOnSubmitRound = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (selected_words.size()>1){

                boardNum++;
                Log.i("Board*******: ", "Board Size: "+ MGI.Boards.size());
                //get new board from firebase if it's there
                if ((MGI.Boards.size()-1)>=boardNum){
                    Log.i("Board*******: ", "Getting a board from firebase");
                    String[] board = new String[16];
                    board = MGI.Boards.get(boardNum).BoardList.toArray(board);
                    AllWords=MGI.Boards.get(boardNum).AllWords;
                    generateBoard(board);
                }

                else{

                    Log.i("Board*******: ", "Creating a new board");

                    bc = new BoardCreator(dbHelper, Level);
                    String[] str = bc.getBoardLayout();
                    AllWords = bc.getAllWordsInString();
                    generateBoard(str);

                    MultiPlayerBoard MPB = new MultiPlayerBoard(str, AllWords);
                    MGI.Boards.add(MPB);
                    mDatabaseReference.child("MultiGames").child("Boards").setValue(MGI.Boards);
                }

                if(Timer != null) {
                    Timer.cancel();
                    Timer = null;
                }
                selected_words = new ArrayList();
                startTimer((score*1000)+TimeLeft);

            }
            else
                Toast.makeText(getApplicationContext(), "You must identify at least 5 words to go to next the round!", Toast.LENGTH_SHORT).show();



        }
    };

    public void showScore(String PlayerType){
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
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    MultiGameInfo MGI = new MultiGameInfo();
                    mDatabaseReference.child("MultiGames").setValue(MGI);
                    finish();
                    System.exit(0);
                }
            });

        }
        if(PlayerType.equals("HOST"))
            alertDialog.setMessage("You found the following words:\n" + foundWords + "\n\nThe valid words in this board are:\n\n" + bc.getAllWordsInString());
        else
            alertDialog.setMessage("You found the following words:\n" + foundWords + "\n\nThe valid words in this board are:\n\n" + AllWords);

        alertDialog.setPositiveButton("BACK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                //quit go back to Mainacitivyt
                Intent intent = new Intent(mpNewGame.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                System.exit(0);
            }
        }).show();

    }

    public void showDialog(final String PlayerType, final String title, final String Message, final boolean displayHighScore){


        timer.setText(title);
        alertDialog1.setTitle(title);
        alertDialog1.setMessage(Message);
        alertDialog1.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                SubmitButton.setEnabled(false);
                CancelButton.setEnabled(false);
                SubmitRound.setEnabled(false);
                dialog.dismiss();
                if(displayHighScore)
                    showScore(PlayerType);
                else{
                    Intent intent = new Intent(mpNewGame.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }


            }
        }).show();
    }


}