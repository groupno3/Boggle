package com.projects.sweproject.boggle;

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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class spNewGame extends AppCompatActivity {

    int[][] matrix = {{R.id.Point00, R.id.Point01, R.id.Point02, R.id.Point03},
            {R.id.Point10, R.id.Point11, R.id.Point12, R.id.Point13},
            {R.id.Point20, R.id.Point21, R.id.Point22, R.id.Point23},
            {R.id.Point30, R.id.Point31, R.id.Point32, R.id.Point33}};

    Point[][] lMatrix;
    int[][] touchPath;
    int gridX,gridY;
    int viewHeight;
    int viewWidth;
    int offset;

    static boolean active = false;

    private TextView scoreView;

    Button submit_button;
    Button cancel_button;

    int score = 0;


    String [][] board;

    private LinearLayout main;
    private SquareTextView sq;
    private TextView wordIn;
    private String letter, word,letter_path="";
    private ArrayList<String> selected_words;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout);

        scoreView = (TextView) findViewById(R.id.score_textView);

        submit_button = (Button) findViewById(R.id.submit_button);
        cancel_button = (Button) findViewById(R.id.cancel_button);

        selected_words = new ArrayList();

        final WordDBHelper dbHelper = new WordDBHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        scoreView.setText("Your Score: "+score);


        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String input = wordIn.getText().toString();

                boolean isValidWord = dbHelper.getWord(input);

                Toast.makeText(getApplicationContext(),letter_path,Toast.LENGTH_SHORT).show();

                if (isValidWord == true) {

                    if(selected_words.contains(letter_path)) {
                        Toast.makeText(getApplicationContext(), "you have already selected this word!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_SHORT).show();
                        selected_words.add(letter_path);
                        scoreView.setText("Your Score: " + calculateScore(input));
                    }

                    letter_path ="";
                }
                else {
                    Toast.makeText(getApplicationContext(), "Wrong!", Toast.LENGTH_SHORT).show();
                    letter_path ="";
                }
                wordIn.setText("");
                resetHighlight();
            }


        });

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wordIn.setText("");
                letter_path ="";
                resetHighlight();
            }
        });


        //init
        board = new String[4][4];
        lMatrix = new Point[4][4];
        touchPath = new int[4][4];
        viewHeight = 0;
        viewWidth = 0;
        wordIn = (TextView)findViewById(R.id.WordInput);

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
        Log.i("*** TAG :: ","gridY = "+ gridY);
        //start
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
        letter_path="";
        wordIn.setText(word);
        //gen board
        for(int i=0;i<4;++i) {
            for (int j = 0; j < 4; ++j) {
                sq = (SquareTextView) findViewById(matrix[i][j]);
                touchPath[i][j] = 0;
                BoardCreator bc = new BoardCreator();
                String[] str = bc.getBoardLayout();
                board[i][j] = str[i*4+j];

                sq.setText(board[i][j], TextView.BufferType.EDITABLE);

            }
        }
        //start timer
        // TODO: create motion lock
        final TextView a = (TextView) findViewById(R.id.timer);

        new CountDownTimer(180000, 1000) {

            public void onTick(long millisUntilFinished) {
                a.setText("Time left: " + ((millisUntilFinished/1000)/60)  + ":"+ ((millisUntilFinished/1000)%60));
            }

            public void onFinish() {


                a.setText("Time's up!");


                AlertDialog.Builder alertDialog = new AlertDialog.Builder(spNewGame.this);
                alertDialog.setTitle("GAME OVER");
                alertDialog.setPositiveButton("BACK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        //quit go back to Mainacitivyt
                        Intent intent = new Intent(spNewGame.this, MainActivity.class);
                        startActivity(intent);

                    }
                });
                alertDialog.setNegativeButton("PLAY AGAIN", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        //restart the screen
                        finish();
                        startActivity(getIntent());



                    }
                });
                alertDialog.create();
                if(active)
                    alertDialog.show();

            }
        }.start();


    }

    private void track(int x, int y){
        int pointX;
        int pointY;
        //letter_path = "";
        //wordIn.setText("");
        //wordIn.append("X:"+x+" Y:"+y+"\n");
        //wordIn.append("GX:"+gridX+"GY: "+gridY+" OS:"+offset+" VW:"+viewWidth+"\n");
        for(int i = 0; i < 4; ++i){
            for(int j = 0; j < 4; ++j){
                //wordIn.append("|"+lMatrix[i][j].x+","+lMatrix[i][j].y+"|");
                pointX = lMatrix[i][j].x + gridX;
                pointY = lMatrix[i][j].y + gridY;

                if(x > pointX + offset && x < pointX + viewWidth - offset){
                    if(y > pointY + offset && y < pointY + viewHeight - offset){
                        if(touchPath[i][j] == 0){

                            sq = (SquareTextView) findViewById(matrix[i][j]);
                            letter = sq.getText().toString();

                            //highlight
                            sq.setBackgroundColor(Color.RED);

                            word = word + letter;
                            letter_path = letter_path + i+j;
                            wordIn.setText(word);
                            //wordIn.setGravity(Gravity.LEFT);

                            //un highlight

                            touchPath[i][j] = 1;
                        }
                    }
                }
            }
        }
    }

    private void resetHighlight(){
        for(int i = 0; i < 4; ++i){
            for(int j = 0; j < 4; ++j){
                sq = (SquareTextView) findViewById(matrix[i][j]);

                sq.setBackgroundColor(Color.WHITE);
            }
        }
    }
    public void submit(){
        // clear touchPath
        for(int i = 0; i < 4; ++i){
            for(int j = 0; j < 4; ++j){
                touchPath[i][j] = 0;
            }
        }
        // clear word
        word = "";
        //letter_path="";
        //wordIn.setText("");
        //wordIn.append(" ");
        //resetHighlight();
    }

    public boolean onTouchEvent(MotionEvent event) {

        int X = (int) event.getX();
        int Y = (int) event.getY();
        int EA = event.getAction();
        //Log.d("*** MotionEvent :: "," Event: "+EA);
        switch (EA) {
            case MotionEvent.ACTION_DOWN:
                track(X, Y);
                break;

            case MotionEvent.ACTION_MOVE:
                track(X, Y);
                break;

            case MotionEvent.ACTION_UP:
                submit();
                break;
        }

        return true;
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

}