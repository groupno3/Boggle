package com.projects.sweproject.boggle;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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


    private SquareTextView Point00;
    private SquareTextView Point01;
    private SquareTextView Point02;
    private SquareTextView Point03;

    private SquareTextView Point10;
    private SquareTextView Point11;
    private SquareTextView Point12;
    private SquareTextView Point13;

    private SquareTextView Point20;
    private SquareTextView Point21;
    private SquareTextView Point22;
    private SquareTextView Point23;

    private SquareTextView Point30;
    private SquareTextView Point31;
    private SquareTextView Point32;
    private SquareTextView Point33;

    private TextView scoreView;

    Button submit_button;
    Button cancel_button;

    int score = 0;


    String [][] board;

    private LinearLayout main;
    private SquareTextView sq;
    private TextView wordIn;
    private String letter, word;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout);

        Point00 = (SquareTextView) findViewById(R.id.Point00);
        Point01 = (SquareTextView) findViewById(R.id.Point01);
        Point02 = (SquareTextView) findViewById(R.id.Point02);
        Point03 = (SquareTextView) findViewById(R.id.Point03);

        Point10 = (SquareTextView) findViewById(R.id.Point10);
        Point11 = (SquareTextView) findViewById(R.id.Point11);
        Point12 = (SquareTextView) findViewById(R.id.Point12);
        Point13 = (SquareTextView) findViewById(R.id.Point13);

        Point20 = (SquareTextView) findViewById(R.id.Point20);
        Point21 = (SquareTextView) findViewById(R.id.Point21);
        Point22 = (SquareTextView) findViewById(R.id.Point22);
        Point23 = (SquareTextView) findViewById(R.id.Point23);

        Point30 = (SquareTextView) findViewById(R.id.Point30);
        Point31 = (SquareTextView) findViewById(R.id.Point31);
        Point32 = (SquareTextView) findViewById(R.id.Point32);
        Point33 = (SquareTextView) findViewById(R.id.Point33);

        scoreView = (TextView) findViewById(R.id.score_textView);

        submit_button = (Button) findViewById(R.id.submit_button);
        cancel_button = (Button) findViewById(R.id.cancel_button);

        final WordDBHelper dbHelper = new WordDBHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        scoreView.setText("Your Score: "+score);


        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String input = wordIn.getText().toString();

                boolean isValidWord = dbHelper.getWord(input);

                if (isValidWord == true) {
                    Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_SHORT).show();
                    score = score +input.length();
                    scoreView.setText("Your Score: "+score);

                }
                else
                    Toast.makeText(getApplicationContext(), "Wrong!", Toast.LENGTH_SHORT).show();

                wordIn.setText("");
                resetAllSquareViews();
            }


        });

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wordIn.setText("");
                resetAllSquareViews();
            }
        });

        Point00.setOnClickListener(squareViewListener);
        Point01.setOnClickListener(squareViewListener);
        Point02.setOnClickListener(squareViewListener);
        Point03.setOnClickListener(squareViewListener);

        Point10.setOnClickListener(squareViewListener);
        Point11.setOnClickListener(squareViewListener);
        Point12.setOnClickListener(squareViewListener);
        Point13.setOnClickListener(squareViewListener);

        Point20.setOnClickListener(squareViewListener);
        Point21.setOnClickListener(squareViewListener);
        Point22.setOnClickListener(squareViewListener);
        Point23.setOnClickListener(squareViewListener);

        Point30.setOnClickListener(squareViewListener);
        Point31.setOnClickListener(squareViewListener);
        Point32.setOnClickListener(squareViewListener);
        Point33.setOnClickListener(squareViewListener);


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
        //gridY += getResources().getIdentifier("status_bar_height", "dimen", "android");

        //start
        startGame();
    }

    private void startGame(){
        //clear
        word = "";
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

                            word = word + letter;
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

    public void submit(){
        // clear touchPath
        for(int i = 0; i < 4; ++i){
            for(int j = 0; j < 4; ++j){
                touchPath[i][j] = 0;
            }
        }
        // clear word
        word = "";
        //wordIn.setText("");
        //wordIn.append(" ");
    }

    public boolean onTouchEvent(MotionEvent event) {

        int X = (int) event.getX();
        int Y = (int) event.getY();
        int EA = event.getAction();
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

    private View.OnClickListener squareViewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            SquareTextView st_view = (SquareTextView) v;

            wordIn.setText(wordIn.getText()+st_view.getText().toString());
            st_view.setBackgroundColor(getResources().getColor(R.color.orange));


        }


    };


    private void resetAllSquareViews(){

        Point00.setBackgroundColor(getResources().getColor(R.color.white));
        Point01.setBackgroundColor(getResources().getColor(R.color.white));
        Point02.setBackgroundColor(getResources().getColor(R.color.white));
        Point03.setBackgroundColor(getResources().getColor(R.color.white));

        Point10.setBackgroundColor(getResources().getColor(R.color.white));
        Point11.setBackgroundColor(getResources().getColor(R.color.white));
        Point12.setBackgroundColor(getResources().getColor(R.color.white));
        Point13.setBackgroundColor(getResources().getColor(R.color.white));

        Point20.setBackgroundColor(getResources().getColor(R.color.white));
        Point21.setBackgroundColor(getResources().getColor(R.color.white));
        Point22.setBackgroundColor(getResources().getColor(R.color.white));
        Point23.setBackgroundColor(getResources().getColor(R.color.white));

        Point30.setBackgroundColor(getResources().getColor(R.color.white));
        Point31.setBackgroundColor(getResources().getColor(R.color.white));
        Point32.setBackgroundColor(getResources().getColor(R.color.white));
        Point33.setBackgroundColor(getResources().getColor(R.color.white));

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

}
